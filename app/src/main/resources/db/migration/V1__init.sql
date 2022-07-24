create table user (
    user_id bigint not null auto_increment,
    created_at datetime(6),
    updated_at datetime(6),
    email varchar(255) not null,
    nickname varchar(255) not null,
    password varchar(255) not null,
    status varchar(255) not null,
    primary key (user_id)
) engine=InnoDB default charset = utf8mb4;

alter table user
    add constraint uk_user_email unique (email);

alter table user
    add constraint uk_user_nickname unique (nickname);

create table role (
    role varchar(255) not null,
    user_id bigint not null,
    created_by bigint not null,
    last_modified_by bigint not null,
    created_at datetime(6) not null,
    updated_at datetime(6) not null,
    primary key (role, user_id)
) engine=InnoDB default charset = utf8mb4;

alter table role
    add constraint fk_role_user_user_id foreign key (user_id) references user (user_id);

create table category (
    category_id integer unsigned not null,
    created_at datetime(6),
    updated_at datetime(6),
    created_by bigint,
    last_modified_by bigint,
    name varchar(255) not null comment '카테고리 이름',
    ordering integer not null comment '정렬 순서',
    parent_id integer unsigned,
    primary key (category_id)
) engine=InnoDB default charset = utf8mb4;

alter table category
    add constraint fk_category_category_id_category_parent_id
        foreign key (parent_id) references category (category_id);
