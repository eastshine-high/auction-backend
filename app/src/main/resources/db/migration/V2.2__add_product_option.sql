alter table product
    ADD COLUMN product_options_title varchar(255) after stock_quantity;

create table product_option (
    product_option_id bigint not null auto_increment,
    created_at datetime(6),
    updated_at datetime(6),
    created_by bigint,
    last_modified_by bigint,
    ordering integer not null,
    product_option_name varchar(255) not null,
    stock_quantity integer,
    product_id bigint,
    primary key (product_option_id)
) engine=InnoDB default charset = utf8mb4;

alter table product_option
    add constraint fk_product_option_product
        foreign key (product_id) references product (product_id)
