/*
SQLyog Ultimate v8.71 
MySQL - 5.1.41 : Database - charashop_dev
*********************************************************************
*/


/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
CREATE DATABASE /*!32312 IF NOT EXISTS*/`charashop_test` /*!40100 DEFAULT CHARACTER SET utf8 COLLATE utf8_unicode_ci */;

USE `charashop_test`;

/*Table structure for table `item` */

DROP TABLE IF EXISTS `item`;

CREATE TABLE `item` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `description` text COLLATE utf8_unicode_ci COMMENT 'description for this item',
  `item_id` varchar(256) COLLATE utf8_unicode_ci DEFAULT NULL,
  `price` bigint(20) DEFAULT NULL COMMENT 'Mark item as on sale',
  `quantity` int(11) DEFAULT NULL,
  `weight` int(4) DEFAULT NULL,
  `category_id` int(11) DEFAULT NULL,
  `is_hot` tinyint(1) DEFAULT '0',
  `is_discounting` tinyint(1) DEFAULT '0',
  `last_update` datetime DEFAULT NULL,
  `first_added` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `FK_item_category` FOREIGN KEY (`category_id`) REFERENCES `category` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;


DROP TABLE IF EXISTS `category`;

CREATE TABLE `category` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `description` text COLLATE utf8_unicode_ci COMMENT 'description for this category',
  `title` varchar(256) DEFAULT NULL,
  `category_code` char(2) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

/*Data for the table `item` */

/*Table structure for table `item_picture` */

DROP TABLE IF EXISTS `item_picture`;

CREATE TABLE `item_picture` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `title` varchar(256) COLLATE utf8_unicode_ci DEFAULT NULL,
  `description` text COLLATE utf8_unicode_ci COMMENT 'description for each item picture',
  `link` varchar(256) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'link to this url',
  `internal_link` varchar(256) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'use this as an backup, refer to server link to this picture',
  `item_id` int(11) DEFAULT NULL COMMENT 'foreign key to ITEM table',
  `is_thumbnail_picture` tinyint(1) DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `FK_item_picture` (`item_id`),
  CONSTRAINT `FK_item_picture` FOREIGN KEY (`item_id`) REFERENCES `item` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

/*Data for the table `item_picture` */

/*Table structure for table `user` */

DROP TABLE IF EXISTS `user`;

CREATE TABLE `user` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `first_name` varchar(256) COLLATE utf8_unicode_ci NOT NULL,
  `middle_name` varchar(256) COLLATE utf8_unicode_ci DEFAULT NULL,
  `last_name` varchar(256) COLLATE utf8_unicode_ci NOT NULL,
  `email` varchar(256) COLLATE utf8_unicode_ci NOT NULL,
  `username` varchar(50) COLLATE utf8_unicode_ci NOT NULL,
  `password` varchar(256) COLLATE utf8_unicode_ci DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `last_login_time` datetime DEFAULT NULL,
  `account_locked` tinyint(1) DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `username` (`username`),
  UNIQUE KEY `username_2` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

/*Data for the table `user` */

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

INSERT INTO USER (first_name, middle_name, last_name, email, username, password)
VALUES ('Hoang', 'Khanh', 'Nguyen', 'nkhoang.it@gmail.com', 'nkhoang.it', md5('blackdragon'));

insert into authassignment (itemname, userid) values ('admin','1');


INSERT INTO `item_picture` (`title`, `description`, `link`, `internal_link`, `item_id`, `is_thumbnail_picture`) VALUES
( 'Item Pic 001', 'Item Pic 001 Description', 'http://t2.gstatic.com/images?q=tbn:ANd9GcR2U5qUtOSj9FiiM5vKL9MLRMV-rNq7NOa6gZj8C7ITCnPUqNPS', 'http://localhost.com', 2, 1),
( 'Item Pic 002', 'Item Pic 002 Description', 'http://t2.gstatic.com/images?q=tbn:ANd9GcR2U5qUtOSj9FiiM5vKL9MLRMV-rNq7NOa6gZj8C7ITCnPUqNPS', 'http://localhost.com', 3, 1),
( 'Item Pic 004', 'Item Pic 004 Description', 'http://t2.gstatic.com/images?q=tbn:ANd9GcR2U5qUtOSj9FiiM5vKL9MLRMV-rNq7NOa6gZj8C7ITCnPUqNPS', 'http://localhost.com', 4, 1),
( 'Item Pic 005', 'Item Pic 005 Description', 'http://t2.gstatic.com/images?q=tbn:ANd9GcR2U5qUtOSj9FiiM5vKL9MLRMV-rNq7NOa6gZj8C7ITCnPUqNPS', 'http://localhost.com', 5, 1),
( 'Item Pic 006', 'Item Pic 006 Description', 'http://t2.gstatic.com/images?q=tbn:ANd9GcR2U5qUtOSj9FiiM5vKL9MLRMV-rNq7NOa6gZj8C7ITCnPUqNPS', 'http://localhost.com', 6, 1),
( 'Item Pic 007', 'Item Pic 007 Description', 'http://t2.gstatic.com/images?q=tbn:ANd9GcR2U5qUtOSj9FiiM5vKL9MLRMV-rNq7NOa6gZj8C7ITCnPUqNPS', 'http://localhost.com', 7, 1),
( 'Item Pic 008', 'Item Pic 008 Description', 'http://t2.gstatic.com/images?q=tbn:ANd9GcR2U5qUtOSj9FiiM5vKL9MLRMV-rNq7NOa6gZj8C7ITCnPUqNPS', 'http://localhost.com', 8, 1),
( 'Item Pic 009', 'Item Pic 009 Description', 'http://t2.gstatic.com/images?q=tbn:ANd9GcR2U5qUtOSj9FiiM5vKL9MLRMV-rNq7NOa6gZj8C7ITCnPUqNPS', 'http://localhost.com', 9, 1);



INSERT INTO `item` (`description`, `item_id`, `price`, `quantity`, `category_id`, `is_hot`, `is_discounting`, `last_update`, `first_added`) VALUES
( 'item 001', 'IT001', 123000, 10, 2, 0, 0, NULL, NULL),
( 'item 002', 'IT002', 45000, 10, 2, 0, 0, NULL, NULL),
( 'item 003', 'IT003', 10000, 5, 3, 0, 0, NULL, NULL),
( 'item 004', 'IT004', 12000, 5, 3, 0, 0, NULL, NULL),
( 'item 005', 'IT005', 12000, 5, 2, 0, 0, NULL, NULL),
( 'item 006', 'IT006', 12000, 5, 2, 0, 0, NULL, NULL),
( 'item 007', 'IT007', 12000, 5, 2, 0, 0, NULL, NULL),
( 'item 008', 'IT008', 12000, 5, 2, 0, 0, NULL, NULL);

insert into category (title, description) values ('short', 'Short');
insert into category (title, description) values ('pains', 'Pains');

ALTER TABLE category ADD COLUMN hide INT(1) DEFAULT 0;
ALTER TABLE item ADD COLUMN material VARCHAR(4000) DEFAULT NULL;
ALTER TABLE item ADD COLUMN size VARCHAR(4000) DEFAULT NULL;