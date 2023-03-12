-- 1b
CREATE TABLE sektion(
    sektionsnamn TEXT,
    uppdrag TEXT,
    PRIMARY KEY (sektionsnamn)
);

CREATE TABLE roll(
    title TEXT,
    stil_id TEXT,
    sektionsnamn TEXT,
    PRIMARY KEY (stil_id, sektionsnamn),
    FOREIGN KEY (sektionsnamn) REFERENCES sektion(sektionsnamn),
    FOREIGN KEY (stil_id) REFERENCES karnevalist(stil_id)
);

CREATE TABLE karnevalist(
    stil_id TEXT,
    namn TEXT, 
    telefonnummer TEXT,
    PRIMARY KEY (stil_id)
);

CREATE TABLE resurs(
    resursid TEXT,
    namn TEXT,
    stil_id TEXT,
    PRIMARY KEY (stil_id),
    FOREIGN KEY (stil_id) REFERENCES karnevalist(stil_id)
);

CREATE TABLE uppgift(
    uppgiftid TEXT,
    beskrivning TEXT,
    aktivitetid TEXT,
    sektionsnamn TEXT,
    stil_id TEXT,
    PRIMARY KEY (uppgiftsid),
    FOREIGN KEY (stil_id) REFERENCES karnevalist(stil_id),
    FOREIGN KEY (aktivitetid) REFERENCES aktivitet(aktivitetid),
    FOREIGN KEY (sektionsnamn) REFERENCES sektion(sektionamn)
);

CREATE TABLE aktivitet(
    aktivitetid TEXT,
    starttid TIME,
    sluttid TIME,
    PRIMARY KEY (aktivitetid)
);

-- 1c
SELECT namn, telefonnummer
FROM uppgift
JOIN aktivitet
USING (aktivitetid)
JOIN karnevalist
USING (stil_id)
WHERE sektionsnamn = "Barnevalen" AND starttid = "2022-05-21 15:00" AND sluttid = "2022-05-21 16:00"

-- 2b
INSERT INTO groups(assignment_numbeer, group_number, grade)
VALUES (3,12,0);
INSERT INTO group_memberships(stil_id, assignment_number, group_number)
VALUES ("alice",3,12);
INSERT INTO group_memberships(stil_id, assignment_number, group_number)
VALUES ("bob",3,12);

-- 2c
SELECT description, grade
FROM group_memberships
JOIN assignments
USING (assignment_number)
JOIN groups
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
WHERE grade > 0
GROUP BY stil_id
HAVING count() = 4

-- 2e
SELECT stil_id, name
FROM students
JOIN group_memberships
USING (stil_id)
JOIN groups
USING (assignment_number, group_number)
WHERE asignment_number = 1 AND grade = (
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
ORDER BY  assignment_number

-- 3e
SELECT A,B,C,D,E,F
FROM R1a
JOIN R1b
USING (D)
JOIN R2
USING (B)
