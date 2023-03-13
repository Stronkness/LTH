-- 1c
SELECT personnummer, kommunnamn
FROM personer
JOIN kommuner
USING (kommunnamn)
JOIN vårdakter
USING (personnummer)
JOIN journaler
USING (vård_id)
WHERE region = "Skåne" AND kommentar LIKE "%inbillningssjuk%"

-- 2b
CREATE TABLE apartments(
    apartment_id TEXT,
    owner_id TEXT,
    building_id TEXT,
    nbr_of_rooms INTEGER,
    area FLOAT,
    monthly_rent FLOAT,
    latest_owner_change_year INTEGER,
    PRIMARY KEY (apartment_id),
    FOREIGN KEY (building_id) REFERENCES buildings(building_id),
    FOREIGN KEY (owner_id) REFERENCES owners(owner_id)
);

-- 2c
SELECT owner_id, namn
FROM owners
JOIN apartments
USING (owner_id)
ORDER BY latest_owner_change DESC
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
WHERE building_id IN (
    SELECT building_id
    FROM apartments
    JOIN buildings
    USING (building_id)
    WHERE address = "Storgatan 1"
)

-- 2f
SELECT namn
FROM owners
JOIN apartments
USING (owner_id)
GROUP BY owner_id
HAVING count() > 1

-- 2g
SELECT apartment_id, addres
FROM buildings
JOIN apartments
USING (building_id)
WHERE apartment_id NOT IN (
    SELECT apartment_id
    FROM repairs
    WHERE year > 2010
)

-- 2h
SELECT building_id, coalesce(sum(cost), 0)
FROM repairs
LEFT JOIN apartments
USING (apartment_id)
GROUP BY apartment_id
HAVING year = 2019

-- 2i
SELECT address, coalesce(sum(area), 0) AS tot_sum
FROM buildings
LEFT JOIN apartments
USING (building_id)
GROUP BY building_id
ORDER BY tot_sum DESC