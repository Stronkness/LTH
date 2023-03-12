-- 1b
CREATE TABLE roll(
    titel TEXT,
    sektionsnamn TEXT,
    stil_id TEXT,
    PRIMARY KEY (stil_id, sektionsnamn),
    FOREIGN KEY (stil_id) REFERENCES karnevalist(stil_id),
    FOREIGN KEY (sektionsnamn) RREFERENCES sektion(sektionsnamn)
);
CREATE TABLE karnevalist(
    stil_id TEXT DEFAULT lower(hex(randomblob(16))),
    namn TEXT,
    telefon TEXT
    PRIMARY KEY (stil_id)
);
CREATE TABLE sektion(
    sektionsnamn TEXT,
    uppdrag TEXT,
    PRIMARY KEY (sektionsnamn)
);
CREATE TABLE aktivitet(
    aktivitetsid TEXT DEFAULT lower(hex(randomblob(16)))
    beskrivning TEXT,
    starttid DATETIME,
    sluttid DATETIME,
    resursid TEXT,
    PRIMARY KEY (aktivitetsid),
    FOREIGN KEY (resursid) REFERENCES resurs(resursid)
);
CREATE TABLE uppgift(
    uppgiftsid TEXT DEFAULT lower(hex(randomblob(16)))
    beskrivning TEXT,
    stil_id TEXT,
    sektionsnamn TEXT,
    aktivitetsid TEXT,
    PRIMARY KEY (uppgiftsid)
    FOREIGN KEY (sektionsnamn) REFERENCES sektion(sektionsnamn),
    FOREIGN KEY (stil_id) REFERENCES karnevalist(stil_id),
    FOREIGN KEY (aktivitetsid) REFERENCES aktivitet(aktivitetsid)
);
CREATE TABLE resurs(
    resursid TEXT DEFAULT lower(hex(randomblob(16)))
    beskrivning TEXT,
    aktivitetsid TEXT,
    PRIMARY KEY(resursid),
    FOREIGN KEY (aktivitetsid) REFERENCES aktivitet(aktivitetsid)
);

-- 1c
SELECT namn, telefon
FROM uppgift
JOIN aktivitet
USING (aktivitetid)
JOIN karnevalist
USING (stil_id)
WHERE sektionsnamn = "Barneval" AND starttid = "2022-05-21 15:00" AND sluttid = "2022-05-21 16:00"

-- 2a
CREATE TABLE students(
    stil_id TEXT,
    name TEXT,
    PRIMARY KEY (stil_id)
);
CREATE TABLE assignments(
    assignment_number TEXT,
    description TEXT,
    PRIMARY KEY (assignment_number)
);
CREATE TABLE groups(
    assignment_number TEXT,
    group_number INTEGER,
    grade INTEGER,
    PRIMARY KEY (assingment_number, group_number),
    FOREIGN KEY (assignment_number) REFERENCES assignments(assignment_number)
);
CREATE TABLE group_memberships(
    stil_id TEXT,
    assignment_number TEXT,
    group_number INTEGER,
    PRIMARY KEY (stil_id, assignment_number, group_number),
    FOREIGN KEY (assignment_number) REFERENCES assignments(assignment_number),
    FOREIGN KEY (group_number) REFERENCES groups(group_number)
);

-- 2b
INSERT INTO groups(assignment_number, group_number, grade)
VALUES (12,3,0);
INSERT INTO group_memberships(stil_id, assingment_number, group_number)
VALUES ("alice",12,3);
INSERT INTO group_memberships(stil_id, assingment_number, group_number)
VALUES ("bob",12,3);

-- 2c
SELECT description, grade
FROM assignments
JOIN groups
USING (assignment_number)
JOIN group_memberships
USING (assignment_number, group_number)
WHERE stil_id = "alice"
ORDER BY assignment_number

-- 2d
SELECT stil_id, name
FROM students
JOIN group_memberships
USING (stil_id)
JOIN groups
USING (assignment_number, group_number)
WHERE grade > 0 AND
GROUP BY stil_id
HAVING count() = 4

-- 2e
SELECT stil_id, name
FROM students
JOIN group_memberships
USING (stil_id)
JOIN groups
USING (assignment_number, group_number)
WHERE assignment_number = 1 AND grade = (
    SELECT max(grade)
    FROM groups
    WHERE assignment_number = 1
)

-- 2f
WITH remaining_groups(assignment_number, group_number) AS (
  SELECT  assignment_number, group_number
  FROM    groups
  WHERE   grade = 0
)
SELECT    assignment_number, count(group_number)
FROM      assignments
          LEFT JOIN remaining_groups USING (assignment_number)
GROUP BY  assignment_number
ORDER BY  assignment_number;

-- 3e
SELECT A,B,C,D,E,F
FROM R1a
JOIN R1b
USING (D)
JOIN R2
USING (B)