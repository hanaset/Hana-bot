CREATE TABLE `calendar` (
  `id` int NOT NULL AUTO_INCREMENT,
  `guild_id` bigint NOT NULL,
  `channel_id` bigint NOT NULL,
  `user_id` bigint NOT NULL,
  `title` varchar(128) NOT NULL,
  `reserve_date` datetime NOT NULL,
  `status` varchar(32) NOT NULL,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8

CREATE TABLE `calendar_apply` (
  `id` int NOT NULL AUTO_INCREMENT,
  `calendar_id` int NOT NULL,
  `user_id` bigint NOT NULL,
  `username` varchar(128) NOT NULL,
  `comment` varchar(128) NOT NULL,
  `status` varchar(32) NOT NULL,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8