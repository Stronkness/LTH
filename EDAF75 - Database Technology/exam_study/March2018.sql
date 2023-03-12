-- 1c
SELECT price, description
FROM products
JOIN offers
USING (ean_code)
WHERE seller_name = "Acme Explosives"
AND "2018-03-13" BETWEEN start_date AND finish_date

-- 2b
CREATE TABLE tickets(
    trip_nbr INTEGER,
    trip_date DATE,
    passenger_name TEXT,
    points INTEGER,
    PRIMARY KEY (trip_nbr, trip_date, passenger_name),
    FOREIGN KEY (trip_nbr, trip_date) REFERENCES train_trips(trip_nbr, trip_date),
    FOREIGN KEY (passenger_name) REFERENCES passengers(passenger_name)
);

-- 2c
SELECT trip_nbr
FROM train_trips
WHERE trip_date = "2018-03-13"
ORDER BY departure_time

-- 2d
SELECT departure_time, origin, passenger_name
FROM train_trips
JOIN tickets
USING (trip_nbr, trip_date)
JOIN passengers
USING (passenger_name)
WHERE trip_date = "2018-03-13"

-- 2e
SELECT passenger_name
FROM passengers
WHERE city = "Lund" AND passenger_name NOT IN (
    SELECT passenger_name
    FROM tickets
)

-- 2f
SELECT trip_date, count() AS trips
FROM train_trips
GROUP BY trip_date
ORDER BY trip_date

-- 2g
SELECT passenger_name, coalesce(sum(points), 0) AS total_points
FROM passengers
LEFT JOIN tickets
USING (passenger_name)
GROUP BY passenger_name
ORDER BY total_points DESC, passenger_name
