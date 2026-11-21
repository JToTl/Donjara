create table gameLog
(
    id int unsigned auto_increment,
    startTime DATETIME,
    endTime DATETIME,
    P1 varchar(16) null,
    P2 varchar(16) null,
    P3 varchar(16) null,
    P4 varchar(16) null,
    Rate DECIMAL,
    P1Point INT default 0,
    P2Point INT default 0,
    P3Point INT default 0,
    P4Point INT default 0,

    primary key(id)
);

create table handsLog
(
    id int unsigned auto_increment,
    gameid int unsigned,
    player varchar(16) null,
    point int unsigned,
    hands TEXT,

    primary key(id),
    foreign key id_gameid(gameid) references gameLog(id)
);

create table playerData
(
    id int unsigned auto_increment,
    name varchar(16) null,
    uuid varchar(36) unique,
    totalWinningPoint int default 0,
    totalWinningMoney int default 0,
    totalPoint int default 0,
    totalMoney int default 0,

    primary key(id)
);

create index gameLog_player_index on gameLog(P1);

create index playerData_point_index on playerData(totalPoint);

create index playerData_money_index on playerData(totalMoney);

create index playerData_uuid_index on playerData(uuid);

-- レート（起動時に無ければ自動で作る）
create table if not exists donjara_rating
(
    uuid varchar(36) primary key,
    name varchar(16),
    rating int not null,
    games int not null default 0,
    rank1 int not null default 0,
    rank2 int not null default 0,
    rank3 int not null default 0,
    rank4 int not null default 0,
    updated_at datetime,
    index donjara_rating_rating_index (rating)
);
