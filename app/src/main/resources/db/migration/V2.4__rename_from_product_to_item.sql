alter table product
    drop foreign key fk_product_category_category_id

alter table product_option
    drop foreign key fk_product_option_product

RENAME table product to item;

RENAME table product_option to item_option;

alter table item
    CHANGE product_id item_id bigint not null auto_increment;

alter table item
    CHANGE product_options_title item_options_title varchar(255);

alter table item_option
    CHANGE product_option_id item_option_id bigint not null auto_increment;

alter table item_option
    CHANGE product_option_name item_option_name varchar(255) not null;

alter table item_option
    CHANGE product_id item_id bigint;

alter table item
    add constraint fk_item_category_category_id
        foreign key (category_id) references category (category_id);

alter table item_option
    add constraint fk_item_option_item
        foreign key (item_id) references item (item_id)
