-- 1c
SELECT start_time, room_name
FROM booked_rooms
JOIN sessions
USING (session_id)
JOIN classes
USING (class_id)
JOIN courses
USING (course_code)
WHERE course_name = "Databasteknik" AND term = "2010-vt1"
ORDER BY start_time, room_name

-- 2b
CREATE TABLE calls(
    call_id TEXT,
    caller_no TEXT,
    callee_no TEXT,
    start_time DATETIME,
    finish_time DATETIME,
    cost FLOAT,
    PRIMARY KEY (call_id),
    FOREIGN KEY (caller_no) REFERENCES subscriptions(phone_no)
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
HAVING count() > 1
ORDER BY username

-- 2f
SELECT username, address, sum(cost)
FROM users
JOIN subscriptions
using (user_id)
JOIN calls
ON subscriptions.phone_no = calls.caller_no
GROUP BY user_id
HAVING sum(cost) > 1000

-- 2g
SELECT phone_no
FROM subscriptions
WHEN phone_no NOT IN (
    SELECT caller_no
    FROM calls
) AND phone_no NOT IN (
    SELECT callee_no
    FROM calls
)
