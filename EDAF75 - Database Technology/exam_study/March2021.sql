-- 1a
CREATE TABLE price(
    hourly_price INTEGER,
    brand_name TEXT,
    model_name TEXT,
    company_id TEXT,
    PRIMARY KEY (brand_name, model_name, company_id),
    FOREIGN KEY (brand_name, model_name) REFERENCES model(brand_name, model_name),
    FOREIGN KEY (company_id) REFERENCES company(company_id)
);
CREATE TABLE model(
    brand_name TEXT,
    model_name TEXT,
    PRIMARY KEY (brand_name, model_name)
);
CREATE TABLE printer(
    printer_id TEXT,
    purchase_date DATE,
    room_number INTEGER,
    department_code TEXT,
    brand_name TEXT,
    model_name TEXT,
    PRIMARY KEY (printer_id),
    FOREIGN KEY (brand_name, model_name) REFERENCES model(brand_name, model_name)
    FOREIGN KEY (department_code) REFERENCES department(department_code)
);
CREATE TABLE department(
    department_code TEXT,
    department_name TEXT,
    contact_phone TEXT,
    PRIMARY KEY (department_code)
);
CREATE TABLE company(
    company_id TEXT,
    company_name TEXT,
    PRIMARY KEY (company_id)
);
CREATE TABLE report(
    report_id TEXT,
    error_date DATE,
    error_description TEXT,
    repair_date DATE,
    repair_description TEXT,
    spare_part_cost FLOAT,
    total_work_hours INTEGER,
    company_id TEXT,
    printer_id TEXT,
    PRIMARY KEY (report_id),
    FOREIGN KEY (company_id) REFERENCES company(company_id),
    FOREIGN KEY (printer_id) REFERENCES printer(printer_id)
);

-- 1c
SELECT printer_id, department_name
FROM printer
JOIN department
USING (department_code)
WHERE printer_id NOT IN (
    SELECT printer_id
    FROM report
)

-- 2a
CREATE TABLE employees(
    employee_id TEXT,
    name TEXT,
    hourly_wage FLOAT,
    PRIMARY KEY (employee_id)
);
CREATE TABLE areas(
    area_id TEXT,
    area_type TEXT,
    employee_id TEXT,
    size INTEGER,
    PRIMARY KEY (area_id),
    FOREIGN KEY (area_type) REFERENCES area_types(area_type),
    FOREIGN KEY (employee_id) REFERENCES employees(employee_id)
);
CREATE TABLE area_types(
    area_type TEXT,
    work_time INTEGER,
    PRIMARY KEY (area_type)
);

-- 2b
SELECT employee_id, name, hourly_wage
FROM employees
ORDER BY name

-- 2c
SELECT area_type, sum(size)
FROM areas
GROUP BY area_type

-- 2d
SELECT employee_id, name
FROM employees
LEFT JOIN areas
USING (employee_id)
WHERE area_id IS NULL

-- 2e
SELECT employee_id, name
FROM employees
JOIN areas
USING (employee_id)
GROUP BY employee_id
HAVING sum(size) > 1000

-- 2f
SELECT sum(size * work_time)
FROM areas
JOIN area_types
USING (area_type)

-- 3e
SELECT A,B,C,D,E,F,G,H
FROM R2a
JOIN R2b
USING (E)
JOIN R1
USING (H)
