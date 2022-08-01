drop table product_category;

alter table product
    add column category_id integer unsigned not null;

alter table product
    add constraint fk_product_category_category_id
        foreign key (category_id) references category (category_id);
