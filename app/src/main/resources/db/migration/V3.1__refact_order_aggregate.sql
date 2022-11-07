drop index order_line_idx01 on order_line;
drop table order_line

create table order_item (
    order_item_id bigint not null auto_increment,
    created_at datetime(6) not null,
    updated_at datetime(6) not null,
    created_by bigint not null,
    last_modified_by bigint not null,
    delivery_status varchar(255) not null,
    item_id bigint not null,
    item_name varchar(255) not null,
    item_price integer not null,
    order_count integer not null,
    seller_id bigint not null,
    order_id bigint not null,
    primary key (order_item_id)
);

create
    index order_item_idx01 on order_item (order_id);

create table order_item_option (
    order_item_option_id bigint not null auto_increment,
    created_at datetime(6) not null,
    updated_at datetime(6) not null,
    created_by bigint not null,
    last_modified_by bigint not null,
    item_option_id bigint,
    item_option_name varchar(255),
    item_option_price integer,
    order_count integer not null,
    order_item_id bigint not null,
    primary key (order_item_option_id)
);

create
    index order_item_option_idx01 on order_item_option (order_item_id);
