ALTER TABLE user_catalogues
ADD COLUMN added_by BIGINT UNSIGNED DEFAULT NULL,
ADD COLUMN edited_by BIGINT UNSIGNED DEFAULT NULL;

ALTER TABLE user_catalogues
ADD CONSTRAINT fk_user_catalogues_added_by
FOREIGN KEY (added_by)
REFERENCES users(id)
ON DELETE SET NULL;

ALTER TABLE user_catalogues
ADD CONSTRAINT fk_user_catalogues_edited_by
FOREIGN KEY (edited_by)
REFERENCES users(id)
ON DELETE SET NULL;