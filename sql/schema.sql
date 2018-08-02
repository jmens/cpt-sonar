DROP TABLE IF EXISTS `token`;
DROP TABLE IF EXISTS `schedule_to_captain`;
DROP TABLE IF EXISTS `captain`;
DROP TABLE IF EXISTS `schedule`;

CREATE TABLE IF NOT EXISTS `captain` (
  `id` INT PRIMARY KEY AUTO_INCREMENT,
  `surname` VARCHAR(256),
  `name` VARCHAR(256),
  `access_key` VARCHAR(256),
  `email` VARCHAR(256)
);

CREATE INDEX `idx_captain_surname` ON captain(`surname`);
CREATE INDEX `idx_captain_name` ON captain(`name`);
CREATE INDEX `idx_captain_access_key` ON captain(`access_key`);
CREATE INDEX `idx_captain_email` ON captain(`email`);

CREATE TABLE IF NOT EXISTS `schedule` (
  `id` INT PRIMARY KEY AUTO_INCREMENT,
  `day` DATE NOT NULL
);

CREATE INDEX `idx_schedule_key` ON schedule(`day`);

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

INSERT INTO `captain`
(`id`, `surname`, `name`, `access_key`, `email`) VALUES
  (DEFAULT, 'Clemens', 'von Musil', 'XXX', 'clemens@vonmusil.de'),
  (DEFAULT, 'Arti', 'Klabautermann', 'a', 'klabautermann@vonmusil.de'),
  (DEFAULT, 'Benni', 'Plankenschreck', 'b', 'plankenschreck@vonmusil.de');

INSERT INTO `schedule`
(`id`, `day`) VALUES
  (DEFAULT, '2018-08-23'),
  (DEFAULT, '2018-08-24'),
  (DEFAULT, '2018-08-27'),
  (DEFAULT, '2018-08-28');

INSERT INTO `schedule_to_captain`
(`id`, `captain_id`, `schedule_id`, `state`) VALUES
  (DEFAULT, 1, 1 , 'ACCEPTED'),
  (DEFAULT, 1, 2 , 'REJECTED'),
  (DEFAULT, 2, 2 , 'ACCEPTED'),
  (DEFAULT, 2, 3 , 'REJECTED'),
  (DEFAULT, 3, 1 , 'REJECTED'),
  (DEFAULT, 3, 3 , 'REJECTED');

