-- 1b
CREATE TABLE roll(
  titel TEXT,
  sektionsnamn TEXT,
  stil_id TEXT,
  PRIMARY KEY (sektionsnamn, stil_id),
  FOREIGN KEY (sektionsnamn) REFERENCES sektion(sektionsnamn),
  FOREIGN KEY (stil_id) REFERENCES karnevalist(stil_id)
);
CREATE TABLE sektion(
  sektionsnamn TEXT,
  PRIMARY KEY (sektionsnamn)
);
CREATE TABLE karnevalist(
  stil_id TEXT,
  namn TEXT,
  telefonnummer TEXT,
  PRIMARY KEY (stil_id)
);
CREATE TABLE aktivitet(
  aktivitet_id TEXT,
  start DATETIME,
  slut DATETIME,
  beskrivning TEXT,
  PRIMARY KEY (aktivitet_id)
);
CREATE TABLE resurs(
  resurs_id TEXT,
  resurs_namn TEXT,
  stil_id TEXT,
  PRIMARY KEY (resurs_id),
  FOREIGN KEY (stil_id) REFERENCES karnevalist(stil_id)
);
CREATE TABLE uppgift(
  uppgift_id TEXT,
  beskrivning TEXT,
  aktivitet_id TEXT,
  stil_id TEXT,
  sektionsnamn TEXT,
  PRIMARY KEY (uppgift_id),
  FOREIGN KEY (aktivitet_id) REFERENCES aktivitet(aktivitet_id),
  FOREIGN KEY (stil_id) REFERENCES karnevalist(stil_id),
  FOREIGN KEY (sektionsnamn) REFERENCES sektion(sektionsnamn)
);

-- 1c
SELECT namn, telefonnummer
FROM karnevalist
JOIN uppgift
USING (stil_id)
JOIN aktivitet
USING (aktivitet_id)
WHERE sektionsnamn = "Barnevalen" AND start = "2022-05-21 15:00" AND slut = "2022-05-21 16:00"

-- 2b
INSERT INTO groups(assignment_number, group_number, grade) VALUES (3,12,0);
INSERT INTO group_memberships(stil_id, assignment_number, group_number) VALUES ("alice",3,12), ("bob",3,12);

-- 2c
SELECT description, grade
FROM group_memberships
JOIN groups
USING (assignment_number)
JOIN assignments
USING (assignment_number)
WHERE stil_id = "alice"
ORDER BY assignment_number

-- 2d
SELECT name, grade
FROM group_memberships
JOIN groups
USING (assignment_number, group_number)
JOIN students
USING (stil_id)
WHERE grade > 0
GROUP BY stil_id
HAVING count() = 4

-- 2e
SELECT stil_id, name
FROM groups
JOIN group_memberships
USING (assignment_number)
JOIN students
USING (stil_id)
WHERE assignment_number = 1 AND grade = (
  SELECT max(grade)
  FROM groups
  WHERE assignment_number = 1
)

-- 2f
SELECT assignment_number, count()
FROM groups
GROUP BY group_number
HAVING grade = 0
ORDER BY assignment_number

-- 3e
SELECT A,B,C,D,E,F
FROM R1a
JOIN R1b
USING (D)
JOIN R2
USING (B)
