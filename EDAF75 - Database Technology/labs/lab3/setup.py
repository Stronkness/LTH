def setupTables():
    return [
         """CREATE TABLE theaters (
                name TEXT,
                capacity INT,
                PRIMARY KEY (name)
            );"""
            ,
         """CREATE TABLE movies (
                imdb_key TEXT,
                title TEXT,
                prod_year INT,
                running_time INT,
                PRIMARY KEY (imdb_key)
            );"""
            ,
         """CREATE TABLE screenings (
                screening_id TEXT DEFAULT (lower(hex(randomblob(16)))),
                date DATE,
                time TIME,
                imdb_key TEXT,
                theater TEXT,
                PRIMARY KEY (screening_id)
                FOREIGN KEY (theater) REFERENCES theaters(name)
                FOREIGN KEY (imdb_key) REFERENCES movies(imdb_key)
            );"""
            ,
         """CREATE TABLE customers (
                username TEXT,
                full_name TEXT,
                password TEXT,
                PRIMARY KEY (username)
            );"""
            ,
         """CREATE TABLE ticket (
                id TEXT DEFAULT (lower(hex(randomblob(16)))),
                screening_id TEXT,
                ticket_holder TEXT,
                PRIMARY KEY (id),
                FOREIGN KEY (screening_id) REFERENCES screenings(screening_id),
                FOREIGN KEY (ticket_holder) REFERENCES customers(username)
            );"""

    ]