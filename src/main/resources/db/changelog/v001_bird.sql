CREATE TABLE bird
(
    id     BIGINT PRIMARY KEY AUTO_INCREMENT,
    name   VARCHAR(255) NOT NULL,
    color  VARCHAR(255),
    weight DECIMAL(10, 2),
    height DECIMAL(10, 2)
);

