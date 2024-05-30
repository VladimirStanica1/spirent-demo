SET @sparrowId = (SELECT id FROM bird WHERE name = 'Sparrow');
SET @robinId = (SELECT id FROM bird WHERE name = 'Robin');
SET @blueJayId = (SELECT id FROM bird WHERE name = 'Blue Jay');

INSERT INTO sighting (bird_id, location, date_time)
VALUES
    (@sparrowId, 'Park', '2024-05-08 10:00:00'),
    (@sparrowId, 'Garden', '2024-05-08 12:00:00');

INSERT INTO sighting (bird_id, location, date_time)
VALUES
    (@robinId, 'Forest', '2024-05-08 11:00:00'),
    (@robinId, 'Lake', '2024-05-08 14:00:00');

INSERT INTO sighting (bird_id, location, date_time)
VALUES
    (@blueJayId, 'Backyard', '2024-05-08 13:00:00'),
    (@blueJayId, 'River', '2024-05-08 16:00:00');
