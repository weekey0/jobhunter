create table `JobInfo` (
  `ID` int primary key auto_increment,
  `title` varchar(200) not null default "",
  `salary` varchar(200) not null default "",
  `company` varchar(200) not null default "",
  `description` varchar(6000) not null default "",
  `requirement` varchar(6000) not null default "",
  `source` varchar(200) not null default "",
  `url` varchar(5000) not null default "",
  `urlMd5` varchar(100) not null default "",
  key `ix_source` (`source`),
  unique key `un_ix_url_md5` (`urlMd5`)
) default charset 'utf8' ENGINE='innodb';
CREATE TABLE `Drug` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `classificationTid` int(11) DEFAULT NULL COMMENT '危险类型id',
  `classificationFtid` int(11) DEFAULT NULL COMMENT '危险父类型id',
  `url` varchar(255) DEFAULT NULL COMMENT 'url',
  `name` varchar(255) DEFAULT NULL COMMENT '中文名称',
  `eName` varchar(255) DEFAULT NULL COMMENT '英文名称',
  `casNum` varchar(255) DEFAULT NULL COMMENT 'cas号',
  `formula` varchar(255) DEFAULT NULL COMMENT '分子式',
  `riskClassification` varchar(255) DEFAULT NULL COMMENT '危险性分类',
  `language` varchar(255) DEFAULT NULL COMMENT '语言',
  `createTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '创建时间',
  `updateTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `Classification` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `TId` int(11) NOT NULL COMMENT '类型id',
  `fTid` int(11) NOT NULL COMMENT '父类型id',
  `name` varchar(255) DEFAULT NULL COMMENT '分类名',
  `url` varchar(255) DEFAULT NULL COMMENT '网址',
  `source` varchar(255) DEFAULT NULL COMMENT '来源',
  `createTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '创建时间',
  `updateTime` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=105 DEFAULT CHARSET=utf8;



