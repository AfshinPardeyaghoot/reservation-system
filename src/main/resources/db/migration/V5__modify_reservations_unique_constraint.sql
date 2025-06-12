ALTER TABLE reservations
DROP
FOREIGN KEY fk_slot;

ALTER TABLE reservations
DROP INDEX uk_slot;

ALTER TABLE reservations
    ADD CONSTRAINT fk_slot FOREIGN KEY (slot_id) REFERENCES available_slots (id);