-- 17 Mars 2020
-- 1c
SELECT personnummer, kommunnamn
FROM person
JOIN kommun
USING (kommunnamn)
JOIN rvårdakter
USING (personnummer)
JOIN journalanteckningar
USING (aktnummer)
WHERE kommentar LIKE "%inbillningsjuk%" AND regionnamn = "Skåne"

-- 2b
CREATE TABLE apartments(
    apartment_id TEXT DEFAULT lower(hex(randomblob(16))),
    building_id TEXT,
    nbr_of_rooms INTEGER,
    area TEXT,
    monthly_rent FLOAT,
    latest_owner_change_year INTEGER,
    owner_id TEXT,
    PRIMARY KEY (apartment_id),
    FOREIGN KEY (building_id) REFERENCES buildings(building_id),
    FOREIGN KEY (owner_id) REFERENCES owners(owner_id)
);

-- 2c
SELECT owner_id, name
FROM owners
JOIN apartments
USING (owner_id)
ORDER BY latest_owner_change_year DESC
LIMIT 10

-- 2d
SELECT name
FROM owners
JOIN apartments
USING (owner_id)
JOIN buildings
USING (building_id)
WHERE nbr_of_rooms = 1 AND address = "Storgatan 1"
ORDER BY name

-- 2e
UPDATE apartments
SET monthly_rent = monthly_rent * 1.018
WHEN building_id IN (
    SELECT building_id
    FROM buildings
    WHERE address = "Storgatan 1"
)

-- 2f
SELECT name
FROM owners
JOIN apartments 
USING (owner_id)
GROUP BY owner_id
HAVING count() > 1

-- 2g
SELECT apartment_id, address
FROM apartments
JOIN buildings
USING (building_id)
WHERE apartment_id NOT IN (
    SELECT apartment_id
    FROM repairs
    WHERE year > 2010
)