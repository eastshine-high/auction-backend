create table orders (
    order_id bigint not null auto_increment,
    created_at datetime(6) not null,
    updated_at datetime(6) not null,
    created_by bigint not null,
    last_modified_by bigint not null,
    user_id bigint not null,
    order_status varchar(255) not null,
    etc_message varchar(255) not null,
    receiver_address1 varchar(255) not null,
    receiver_address2 varchar(255) not null,
    receiver_name varchar(255) not null,
    receiver_phone varchar(255) not null,
    receiver_zipcode varchar(255) not null,
    primary key (order_id)
);

create table order_line (
    order_line_id bigint not null auto_increment,
    delivery_status varchar(255) not null,
    item_id bigint not null,
    item_name varchar(255) not null,
    item_option_id bigint,
    item_option_name varchar(255),
    item_option_price integer,
    item_price integer not null,
    order_count integer not null,
    seller_id bigint not null,
    order_id bigint not null,
    primary key (order_line_id)
);

create
    index order_line_idx01 on order_line (order_id);
