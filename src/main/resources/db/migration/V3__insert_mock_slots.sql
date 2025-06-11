DELIMITER $$

CREATE PROCEDURE insert_large_data()
BEGIN
    DECLARE i INT DEFAULT 1;
    WHILE i <= 100000 DO
        INSERT INTO available_slots (start_time, end_time, is_reserved) 
        VALUES 
        (current_timestamp + INTERVAL i MINUTE, current_timestamp + INTERVAL (i + 1) MINUTE, FALSE);
        SET i = i + 1;
END WHILE;
END$$

DELIMITER ;

CALL insert_large_data();