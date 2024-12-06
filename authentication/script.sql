create table user
(
    user_id    int auto_increment
        primary key,
    first_name varchar(50)  not null,
    last_name  varchar(50)  not null,
    email      varchar(320) not null,
    password   varchar(255) not null,
    constraint user_email_uindex
        unique (email)
);

create table refresh_token
(
    rt_id       int auto_increment
        primary key,
    rt_token    varchar(255) not null,
    expiry_date datetime     not null,
    user_id     int          not null,
    reuse_flag  tinyint(1)   not null,
    used        tinyint(1)   not null,
    constraint refresh_token_user_user_id_fk
        foreign key (user_id) references user (user_id)
);


