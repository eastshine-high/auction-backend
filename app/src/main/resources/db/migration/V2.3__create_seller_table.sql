create table seller (
    business_number varchar(255) not null,
    seller_level varchar(255) not null,
    user_id bigint not null,
    primary key (user_id)
) engine=InnoDB default charset = utf8mb4;

alter table seller
    add constraint UK_seller_business_number unique (business_number)

alter table seller
    add constraint fk_seller_user
        foreign key (user_id) references user (user_id)
