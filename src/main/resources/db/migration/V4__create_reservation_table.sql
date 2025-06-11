CREATE TABLE reservations
(
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    slot_id     BIGINT NOT NULL,
    user_id     BIGINT NOT NULL,
    reserved_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    status      ENUM('ACTIVE','CANCELLED') DEFAULT 'ACTIVE',
    CONSTRAINT uk_slot UNIQUE (slot_id),
    CONSTRAINT fk_slot FOREIGN KEY (slot_id) REFERENCES available_slots (id),
    CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES users (id)
);