CREATE TABLE available_slots
(
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    start_time  DATETIME         NOT NULL,
    end_time    DATETIME         NOT NULL,
    is_reserved TINYINT(1) DEFAULT 0,
    version     BIGINT DEFAULT 0 NOT NULL,
    KEY         ix_reserved_start (is_reserved, start_time)
);