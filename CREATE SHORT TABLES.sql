DROP DATABASE `chat02`;
CREATE DATABASE IF NOT EXISTS`chat02`;
USE `cIDhat02`;

CREATE TABLE IF NOT EXISTS `roles` (
	`id` INT NOT NULL AUTO_INCREMENT,
	`name` VARCHAR(15) NOT NULL,
	`can_create_user` TINYINT NOT NULL,
	`can_view_msgs` TINYINT NOT NULL,
	PRIMARY KEY (`id`)
);

CREATE TABLE IF NOT EXISTS `users` (
	`id` INT NOT NULL AUTO_INCREMENT,
	`username` VARCHAR(15) NOT NULL,
	`pass` VARCHAR(15) NOT NULL,
	`role_id` INT NOT NULL,
	PRIMARY KEY (`id`),
	FOREIGN KEY (`role_id`) REFERENCES `roles`(`id`)
);

CREATE TABLE IF NOT EXISTS `chats` (
	`id` INT NOT NULL AUTO_INCREMENT,
	`name` VARCHAR(15) NOT NULL,
	`tb_name` VARCHAR(15) NOT NULL,
	`user_id` INT NOT NULL,
	PRIMARY KEY (`id`),
	FOREIGN KEY (`user_id`) REFERENCES `users`(`id`)
);

CREATE TABLE IF NOT EXISTS `chat_users` (
	`id` INT NOT NULL AUTO_INCREMENT,
	`chat_id` INT NOT NULL,
	`user_id` INT NOT NULL,
	PRIMARY KEY (`id`),
	FOREIGN KEY (`chat_id`) REFERENCES `chats`(`id`),
	FOREIGN KEY (`user_id`) REFERENCES `users`(`id`)
);

CREATE TABLE IF NOT EXISTS `msgs` (
	`id` INT NOT NULL AUTO_INCREMENT,
	`user_id` INT NOT NULL,
	`msg` VARCHAR(200) NOT NULL,
	`sent_date` DATE NOT NULL,
	PRIMARY KEY (`id`),
	FOREIGN KEY (`user_id`) REFERENCES `users`(`id`)
);
	
    
	
	
	
	
	
