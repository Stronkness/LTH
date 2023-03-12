-- 2b
CREATE TABLE authors(
    name TEXT,
    title TEXT,
    PRIMARY KEY (name, title),
    FOREIGN KEY (name) REFERENCES persons(title),
    FOREIGN KEY (title) REFERENCES publications(title)
);

-- 2c
SELECT title, journal
FROM publications
WHERE year = "2017"
ORDER BY title

-- 2d
SELECT title
FROM publications
JOIN authors
USING (title)
JOIN persons
USING (name)
WHERE affiliation = "Lunds Universitet"

-- 2e
SELECT name
FROM persons
WHERE affiliation = "Lunds Universitet" AND name NOT IN (
    SELECT name
    FROM authors
)

-- 2f
SELECT title
FROM publications
JOIN authors
USING (title)
GROUP BY title
HAVING count() > 1

-- 2g
SELECT name, COALESCE(COUNT(), 0) AS cnt
FROM persons
LEFT JOIN authors
USING (name)
GROUP BY name
ORDER BY cnt DESC