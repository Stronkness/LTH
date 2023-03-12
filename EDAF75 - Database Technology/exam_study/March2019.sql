-- 1c
SELECT time, room_name
FROM booked_rooms
JOIN sessions
USING (session_id)
JOIN classes
USING (class_id)
JOIN courses
USING (course_id)
WHERE course_name = "Databasteknik" AND term = "2019-vt1"
ORDER BY time, room_name

-- 2b
CREATE TABLE calls(
    call_id TEXT,
    caller_no TEXT,
    callee_no TEXT,
    start_time DATETIME,
    finish_time DATETIME,
    cost INTEGER,
    PRIMARY KEY (call_id),
    FOREIGN KEY (caller_no) REFERENCES subscriptions(phone_no),
    FOREIGN KEY (callee_no) REFERENCES subscriptions(phone_no)
);

-- 2c
SELECT caller_no, cost
FROM calls
ORDER BY start_time DESC
LIMIT 10

-- 2d
SELECT username, address
FROM users
JOIN subscriptions
USING (user_id)
WHERE phone_no = "0707-123456"

-- 2e
SELECT username, address
FROM users
JOIN subscriptions
USING (user_id)
GROUP BY user_id
HAVING count(user_id) > 1
ORDER BY username

-- 2f
SELECT username, address, sum(cost) AS total_cost
FROM calls
JOIN subscriptions
ON calls.caller_no = subscriptions.phone_no
JOIN users
USING (user_id)
GROUP BY user_id
HAVING total_cost > 1000

-- 2g
SELECT phone_no
FROM subscriptions
JOIN calls 
ON subscriptions.phone_no = calls.caller_no
WHERE sum(cost) = 0