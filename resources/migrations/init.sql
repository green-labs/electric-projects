create table attendances
(
    id          int unsigned auto_increment
        primary key,
    user_id     int unsigned                             not null,
    created_at  datetime(6) default CURRENT_TIMESTAMP(6) not null,
    attended_at date        default (curdate())          not null,
    updated_at  datetime(6) default CURRENT_TIMESTAMP(6) not null on update CURRENT_TIMESTAMP(6)
);

create index attendances_created_at_idx
    on attendances (created_at);

create index attendances_updated_at_idx
    on attendances (updated_at);

create index attendances_user_id_idx
    on attendances (user_id);
