PRAGMA foreign_keys=OFF;

DROP TABLE IF EXISTS teathers; 
DROP TABLE IF EXISTS movies;
DROP TABLE IF EXISTS screenings;
DROP TABLE IF EXISTS customers;
DROP TABLE IF EXISTS ticket;

CREATE TABLE teathers (
    name TEXT,
    capacity INT,
    PRIMARY KEY (name)
);

CREATE TABLE movies (
    imdb_key TEXT,
    title TEXT,
    prod_year INT,
    running_time INT,
    PRIMARY KEY (imdb_key)
);

CREATE TABLE screenings (
    uid TEXT, 
    start_date DATE,
    start_time TIME,
    title TEXT,
    teather TEXT,
    seats INT,
    imdb_key TEXT, 
    PRIMARY KEY (uid),
    FOREIGN KEY (teather) REFERENCES teathers(name),
    FOREIGN KEY (imdb_key) REFERENCES movies(imdb_key)
);

CREATE TABLE customers (
    username TEXT,
    full_name TEXT,
    password TEXT,
    PRIMARY KEY (username)
);

CREATE TABLE ticket (
    id TEXT DEFAULT (lower(hex(randomblob(16)))),
    screening TEXT,
    ticket_holder TEXT,
    PRIMARY KEY (id),
    FOREIGN KEY (screening) REFERENCES screenings(uid),
    FOREIGN KEY (ticket_holder) REFERENCES customers(username)
);