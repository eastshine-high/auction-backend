alter table item add column delivery_charge integer unsigned
alter table item add column delivery_charge_policy varchar(255) not null
alter table item add column delivery_method varchar(255) not null
alter table item add column delivery_time integer unsigned
alter table item add column free_ship_over_amount integer unsigned
alter table item add column return_address varchar(255)
alter table item add column return_address_detail varchar(255)
alter table item add column return_charge integer
alter table item add column return_charge_name varchar(255)
alter table item add column return_contact_number varchar(255)
alter table item add column return_zip_code varchar(255)

alter table order_item_option CHANGE item_option_price additional_price integer;
