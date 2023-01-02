alter table role change role role_type varchar(255) not null;
RENAME TABLE role TO user_role;
ALTER TABLE user_role MODIFY COLUMN user_id bigint not null FIRST;
alter table user_role drop primary key, add primary key(user_id, role_type);

ALTER TABLE user_role
DROP FOREIGN KEY `fk_role_user_user_id`,
    ADD CONSTRAINT `fk_user_role_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`);

create table role (
    role_type varchar(255) not null,
    primary key (role_type)
) engine=InnoDB default charset = utf8mb4;

ALTER TABLE user_role
    ADD CONSTRAINT `fk_user_role_role` FOREIGN KEY (`role_type`) REFERENCES `role` (`role_type`);
