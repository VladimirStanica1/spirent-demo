CREATE TABLE sighting
(
    id       BIGINT PRIMARY KEY AUTO_INCREMENT,
    bird_id  BIGINT ,
    location VARCHAR(255) NOT NULL,
    date_time DATETIME     NOT NULL,
    CONSTRAINT fk_sighting_bird FOREIGN KEY (bird_id) REFERENCES bird (id) ON DELETE CASCADE
);