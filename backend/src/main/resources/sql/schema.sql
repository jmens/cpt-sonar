CREATE TABLE IF NOT EXISTS `captain` (
  `id` INT PRIMARY KEY AUTO_INCREMENT,
  `surname` VARCHAR(256),
  `name` VARCHAR(256),
  `access_key` VARCHAR(256),
  `email` VARCHAR(256),
  INDEX `idx_captain_surname` (`surname`),
  INDEX `idx_captain_name` (`name`),
  INDEX `idx_captain_access_key` (`access_key`),
  INDEX `idx_captain_email` (`email`)
);


CREATE TABLE IF NOT EXISTS `schedule` (
  `id` INT PRIMARY KEY AUTO_INCREMENT,
  `day` DATE NOT NULL,
  INDEX `idx_schedule_key` (`day`)
);


CREATE TABLE IF NOT EXISTS schedule_to_captain (
  `id` INT PRIMARY KEY AUTO_INCREMENT,
  `captain_id` INT NOT NULL,
  `schedule_id` INT NOT NULL,
  `state` VARCHAR(256) NOT NULL DEFAULT 'PENDING',
  FOREIGN KEY (`captain_id`) REFERENCES `captain`(`id`),
  FOREIGN KEY (`schedule_id`) REFERENCES `schedule`(`id`)
);

CREATE TABLE IF NOT EXISTS `token` (
  `id` INT PRIMARY KEY AUTO_INCREMENT,
  `captain_id` INT NOT NULL,
  `token` VARCHAR(256) NOT NULL,
  FOREIGN KEY (`captain_id`) REFERENCES captain(`id`)
);
