create table notification_config(id bigint(20)  primary key auto_increment,
province varchar(50) not null,partner varchar(50) not null, mail_list varchar(250) not null, active tinyint(1) not null);
INSERT INTO `notification_config` VALUES (1,'Zambezia','FGH',
'roxanne.hoek@fgh.org.mz,amrita.costa@fgh.org.mz,muhammad.sidi@fgh.org.mz,eurico.jose@fgh.org.mz,helio.machabane@fgh.org.mz,judiao.mbaua@fgh.org.mz,laboratory.quelimane.dg@fgh.org.mz',1);
