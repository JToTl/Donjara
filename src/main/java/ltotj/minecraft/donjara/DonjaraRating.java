package ltotj.minecraft.donjara;

import java.util.*;

/**
 * ドンジャラのレート計算。お金の精算には関係なく、レートにだけ順位ウマを付ける。
 *
 * スコア = (最後の持ち点 - 開始点) / 1000 + ウマ
 * 変動   = スコア × scoreMultiplier + (相手の平均レート - 自分のレート) / correctionDivisor
 *
 * 点差・ウマ・補正はどれも卓の合計が 0 なので、卓全体のレートの合計は変わらない
 * （0 未満にしない下限と、protectionThreshold 以下の人の負けを半分にする分だけずれる）。
 * 上げ下げの大きさ（1位・4位で ±50 前後）と平均への戻り方はクイックポーカーのレートに合わせてある。
 */
public class DonjaraRating {

    public static class Settings {
        public int startPoint = 24000;
        public double[] uma3 = {8, 0, -8};
        public double[] uma4 = {8, 4, -4, -8};
        public double scoreMultiplier = 2.4;
        public double correctionDivisor = 125.0;
        public int protectionThreshold = 1000;
    }

    public static class Entry {
        public final UUID uuid;
        public final int rating;
        public final int point;

        public Entry(UUID uuid, int rating, int point) {
            this.uuid = uuid;
            this.rating = rating;
            this.point = point;
        }
    }

    public static class Result {
        public final int rank;      // 同点は同じ順位（上の順位にそろえる）
        public final double uma;    // 同点は該当する順位のウマを人数で割ったもの
        public final double score;
        public final int delta;     // 下限で切った後の実際の増減
        public final int after;

        Result(int rank, double uma, double score, int delta, int after) {
            this.rank = rank;
            this.uma = uma;
            this.score = score;
            this.delta = delta;
            this.after = after;
        }
    }

    public static Map<UUID, Result> calculate(List<Entry> players, Settings s) {
        int n = players.size();
        double[] umaTable = n == 3 ? s.uma3 : n == 4 ? s.uma4 : null;
        if (umaTable == null) throw new IllegalArgumentException("ドンジャラのレートは3人か4人の卓だけ: " + n);

        List<Entry> sorted = new ArrayList<>(players);
        sorted.sort((a, b) -> Integer.compare(b.point, a.point));

        int ratingSum = 0;
        for (Entry e : players) ratingSum += e.rating;

        Map<UUID, Result> results = new LinkedHashMap<>();
        int i = 0;
        while (i < n) {
            // 同点のまとまり [i, j) は、その順位のウマを足して人数で割る
            int j = i;
            double umaSum = 0;
            while (j < n && sorted.get(j).point == sorted.get(i).point) {
                umaSum += umaTable[j];
                j++;
            }
            double uma = umaSum / (j - i);
            for (int k = i; k < j; k++) {
                Entry e = sorted.get(k);
                double score = (e.point - s.startPoint) / 1000.0 + uma;
                double opponentAverage = (ratingSum - e.rating) / (double) (n - 1);
                double raw = score * s.scoreMultiplier + (opponentAverage - e.rating) / s.correctionDivisor;
                if (raw < 0 && e.rating <= s.protectionThreshold) raw /= 2; // 初心者保護
                int after = Math.max(0, e.rating + (int) Math.round(raw));
                results.put(e.uuid, new Result(i + 1, uma, score, after - e.rating, after));
            }
            i = j;
        }
        return results;
    }
}
