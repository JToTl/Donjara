package ltotj.minecraft.donjara;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * donjara_rating テーブルの読み書き。DB に触るのでメインスレッドから呼ばないこと。
 * MySQLManager はクエリごとに接続を開くので、呼び出しごとに作って閉じる。
 */
public class DonjaraRatingRepository {

    public static class Stats {
        public final UUID uuid;
        public final String name;
        public final int rating;
        public final int games;
        public final int[] places; // 1位〜4位の回数

        Stats(UUID uuid, String name, int rating, int games, int[] places) {
            this.uuid = uuid;
            this.name = name;
            this.rating = rating;
            this.games = games;
            this.places = places;
        }
    }

    private interface Reader<T> {
        T read(ResultSet rs) throws SQLException;
    }

    public static int initialRating() {
        return GlobalClass.config.getConfig().getInt("rating.initialRating", 2500);
    }

    public static void createTable() {
        execute("create table if not exists donjara_rating(" +
                "uuid varchar(36) primary key," +
                "name varchar(16)," +
                "rating int not null," +
                "games int not null default 0," +
                "rank1 int not null default 0," +
                "rank2 int not null default 0," +
                "rank3 int not null default 0," +
                "rank4 int not null default 0," +
                "updated_at datetime," +
                "index donjara_rating_rating_index (rating)" +
                ");");
    }

    public static int getRating(UUID uuid) {
        Integer rating = query("select rating from donjara_rating where uuid='" + uuid + "';",
                rs -> rs.next() ? rs.getInt("rating") : null);
        return rating != null ? rating : initialRating();
    }

    // 1試合分の変動を DB 側で足し引きして記録する（0 未満にはしない）。rank は 1〜4（同点は上の順位）。
    // 計算した値で上書きしないので、起点がずれていても DB のレートを消さない。初めての人は初期レートに足して作る
    public static boolean applyChange(UUID uuid, String name, int delta, int rank) {
        String column = "rank" + rank;
        int firstRating = Math.max(0, initialRating() + delta);
        return execute("insert into donjara_rating(uuid,name,rating,games," + column + ",updated_at) values('" +
                uuid + "','" + name + "'," + firstRating + ",1,1,now()) on duplicate key update " +
                "name=values(name),rating=greatest(0,rating+(" + delta + ")),games=games+1," + column + "=" + column + "+1,updated_at=now();");
    }

    public static Stats getStats(UUID uuid) {
        return query("select * from donjara_rating where uuid='" + uuid + "';",
                rs -> rs.next() ? readStats(rs) : null);
    }

    // 自分より高いレートの人数 + 1
    public static int getServerRank(int rating) {
        Integer above = query("select count(*) as c from donjara_rating where rating>" + rating + ";",
                rs -> rs.next() ? rs.getInt("c") : null);
        return above != null ? above + 1 : 0;
    }

    public static UUID findUuidByName(String name) {
        return query("select uuid from donjara_rating where name='" + name + "' order by updated_at desc limit 1;",
                rs -> rs.next() ? UUID.fromString(rs.getString("uuid")) : null);
    }

    // 賭けた卓すべての通算収支（playerData.totalMoney）
    public static Long getTotalMoney(UUID uuid) {
        return query("select totalMoney from playerData where uuid='" + uuid + "';",
                rs -> rs.next() ? rs.getLong("totalMoney") : null);
    }

    public static List<Stats> getTop(int limit) {
        List<Stats> top = query("select * from donjara_rating order by rating desc limit " + limit + ";", rs -> {
            List<Stats> list = new ArrayList<>();
            while (rs.next()) list.add(readStats(rs));
            return list;
        });
        return top != null ? top : new ArrayList<>();
    }

    private static Stats readStats(ResultSet rs) throws SQLException {
        return new Stats(UUID.fromString(rs.getString("uuid")), rs.getString("name"), rs.getInt("rating"), rs.getInt("games"),
                new int[]{rs.getInt("rank1"), rs.getInt("rank2"), rs.getInt("rank3"), rs.getInt("rank4")});
    }

    private static boolean execute(String sql) {
        return new MySQLManager(Main.getPlugin(Main.class), "DonjaraRating").execute(sql);
    }

    private static <T> T query(String sql, Reader<T> reader) {
        MySQLManager mysql = new MySQLManager(Main.getPlugin(Main.class), "DonjaraRating");
        ResultSet rs = mysql.query(sql);
        if (rs == null) return null;
        try {
            return reader.read(rs);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                rs.close();
                mysql.close();
            } catch (Exception ignored) {
            }
        }
    }
}
