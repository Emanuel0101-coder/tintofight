PRAGMA foreign_keys = ON;

CREATE TABLE IF NOT EXISTS fighters (
    id                INTEGER PRIMARY KEY,
    name              TEXT    NOT NULL UNIQUE,
    max_hp            INTEGER NOT NULL,
    difficulty_level  INTEGER NOT NULL
);

CREATE TABLE IF NOT EXISTS moves (
    id          INTEGER PRIMARY KEY,
    fighter_id  INTEGER NOT NULL,
    name        TEXT    NOT NULL,
    damage      INTEGER NOT NULL,
    has_buff    INTEGER NOT NULL CHECK (has_buff IN (0,1)),
    has_debuff  INTEGER NOT NULL CHECK (has_debuff IN (0,1)),
    has_poison  INTEGER NOT NULL CHECK (has_poison IN (0,1)),
    FOREIGN KEY (fighter_id) REFERENCES fighters(id) ON DELETE CASCADE
);
