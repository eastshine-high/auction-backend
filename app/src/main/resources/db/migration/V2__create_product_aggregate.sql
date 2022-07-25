create table product (
    product_id bigint not null auto_increment,
    name varchar(255) not null,
    on_sale bit not null,
    price integer not null,
    stock_quantity integer not null,
    created_at datetime(6),
    updated_at datetime(6),
    created_by bigint,
    last_modified_by bigint,
    primary key (product_id)
) engine=InnoDB default charset = utf8mb4;

create table product_category (
    category_id integer unsigned not null,
    product_id bigint not null,
    created_at datetime(6),
    updated_at datetime(6),
    created_by bigint,
    last_modified_by bigint,
    primary key (category_id, product_id)
) engine=InnoDB default charset = utf8mb4;

alter table product_category
    add constraint fk_product_category_category_category_id foreign key (category_id) references category (category_id);

alter table product_category
    add constraint fk_product_category_product_product_id foreign key (product_id) references product (product_id);
