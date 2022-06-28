create database if not exists `personal_budget_tg_bot`;
CREATE USER 'tgUser'@'172.17.0.1' IDENTIFIED BY ',cN^PC6Zkz!X&`)?';
GRANT ALL PRIVILEGES ON `personal_budget_tg_bot` . * TO 'tgUser'@'172.17.0.1';