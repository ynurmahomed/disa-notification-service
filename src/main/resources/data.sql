create table notification_config(id bigint(20)  primary key auto_increment,
province varchar(50) not null,partner varchar(50) not null, mail_list varchar(250) not null, active tinyint(1) not null);
