create table notification_config(id bigint primary key auto_increment,
province varchar(50) not null,partner varchar(50) not null, mail_list varchar(250) not null, active tinyint not null);