CREATE SCHEMA collabnotes;
USE collabnotes;

CREATE TABLE notes (
    id INT PRIMARY KEY,
    note_version INT NOT NULL,
    domain_id VARCHAR(255) NOT NULL,
    title VARCHAR(255) NOT NULL,
    UNIQUE KEY domain_id (domain_id)
);

CREATE TABLE note_items (
    id INT PRIMARY KEY,
    note_domain_id VARCHAR(255) NOT NULL,
    note_id INT NOT NULL,
    item_text VARCHAR(255) NOT NULL,
    FOREIGN KEY (note_id) REFERENCES notes (id)
);

CREATE TABLE statistics (
    stats_id INT NOT NULL,
    item_count INT NOT NULL,
    note_count  INT NOT NULL
);

CREATE TABLE persisted_events (
    id INT PRIMARY KEY,
    created DATETIME NOT NULL,
    type VARCHAR(255) NOT NULL,
    consumed BOOLEAN NOT NULL,
    payload VARCHAR(255) NOT NULL
);

