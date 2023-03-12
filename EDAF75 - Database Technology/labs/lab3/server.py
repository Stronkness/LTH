from bottle import get, post, run, request, response
from setup import setupTables
import sqlite3


db = sqlite3.connect("movies.sqlite")

PORT = 7007
@get('/ping')
def ping():

    return "pong"    


@get('/table')
def table():
    c = db.cursor()

    c.execute(
        """
        SELECT *
        FROM theaters
        """
    )
    
    for i in c:
        print(i)
    response.status = 200
    return "OK"

@post('/reset')
def reset():
    
    c = db.cursor()


    c.execute(
        """DROP TABLE IF EXISTS theaters"""
    )
    c.execute(
        """DROP TABLE IF EXISTS movies"""
    )
    c.execute(
        """DROP TABLE IF EXISTS screenings"""
    )
    c.execute(
        """DROP TABLE IF EXISTS customers"""
    )
    c.execute(
        """DROP TABLE IF EXISTS ticket"""
    )
    c.execute(
        """PRAGMA foreign_keys=ON"""
    )
    
    for command in setupTables():
        c.execute(command)


    theatres = [("Kino", 10),
                ("Regal", 16),
                ("Skandia", 100)]
    
    for name, capacity in theatres:
        c.execute(
            """
            INSERT
            INTO theaters(name, capacity)
            VALUES (?, ?) ;       
            """, [name, capacity]
        )
    db.commit()
    return "RESET DB"

@post('/users')
def users():
    user = request.json

    c = db.cursor()

    c.execute(
        """
        SELECT username
        FROM customers
        WHERE username = ?;
        """,
        [user["username"]]
    )

    entry = c.fetchone()
    username = user["username"]
    pw = hash(user["pwd"])
    if entry is None:
        c.execute(
            """
            INSERT
            INTO customers(username, full_name, password)
            VALUES (?, ?, ?);
            """,
            [user["username"], user["fullName"], pw]
        )
        db.commit()
        response.status = 201
        return f"/users/{username}"
    else:
        response.status = 400
        return "EMPTY STRING"
    
@post('/movies')
def movies():
    movie = request.json

    c = db.cursor()

    c.execute(
        """
        SELECT imdb_key
        FROM movies
        WHERE imdb_key = ?;
        """,
        [movie["imdbKey"]]
    )

    entry = c.fetchone()
    imdb_key = movie["imdbKey"]
    if entry is None:
        c.execute(
            """
            INSERT
            INTO movies(imdb_key, title, prod_year, running_time)
            VALUES (?, ?, ?, ?);
            """,
            [movie["imdbKey"], movie["title"], movie["year"], 9999] #FIXA SÅ RUNNING TIME INTE ÄR MED
        )
        db.commit()
        response.status = 201
        return f"/movies/{imdb_key}"
    else:
        response.status = 400
        return "EMPTY STRING"

@post('/performances')
def perfomances():
    performance = request.json

    c = db.cursor()

    c.execute(
        """
        SELECT name
        FROM theaters
        WHERE name = ?
        """,
        [performance["theater"]]
    )
    theaterName = c.fetchone()

    c.execute(
        """
        SELECT imdb_key
        FROM movies
        WHERE imdb_key = ?
        """,
        [performance["imdbKey"]]
    )
    imdb_key = c.fetchone()

    if theaterName is not None and imdb_key is not None:
        c.execute(
            """
            INSERT 
            INTO screenings(date, time, imdb_key, theater)
            VALUES(?, ?, ? ,?)
            RETURNING screening_id
            """,
            [performance["date"], performance["time"], performance["imdbKey"], performance["theater"]]
        )
        found = c.fetchone()
        screening_id, = found
        db.commit()
        response.status = 201
        return f"/performances/{screening_id}"
    else:
        response.status = 400
        return "No such movie or theater"
    
def hash(msg):
    import hashlib
    return hashlib.sha256(msg.encode('utf-8')).hexdigest()

@get('/movies')
def movies():
    c = db.cursor()
    prefix = "WHERE"
    query = """
        SELECT imdb_key, title, prod_year
        FROM movies
    """
    params = []
    print(request.query.title)
    if request.query.title:
        query += prefix + " title = ?"
        prefix = "AND"
        params.append(request.query.title)
    if request.query.year:
        query += prefix + " prod_year = ?"
        prefix = "AND"
        params.append(request.query.year)
    c.execute(query, params)
        
    found = [{"imdbKey": imdb_key, "title": title, "year": prod_year}
            for imdb_key, title, prod_year in c]
    response.status = 200
    return {"data": found}

@get('/movies/<imdb_key>')
def movies(imdb_key):
    c = db.cursor()
    c.execute(
        """
        SELECT imdb_key, title, prod_year
        FROM movies
        WHERE imdb_key = ?
        """, [imdb_key]
    )
    
    found = [{"imdbKey": imdb_key, "title": title, "year": prod_year}
             for imdb_key, title, prod_year in c]
    response.status = 200
    return {"data": found}

@get('/performances')
def performances():
    c = db.cursor()

    c.execute("""
        WITH ticket_count AS (
            SELECT screening_id, count(id) AS bought_tickets
            FROM screenings
            LEFT OUTER JOIN ticket
            USING (screening_id)
            GROUP BY screening_id
        )
        SELECT screening_id, date, time, theater, title, prod_year, capacity - bought_tickets AS remaining_seats
        FROM screenings
        JOIN movies
        USING (imdb_key)
        JOIN ticket_count
        USING (screening_id)
        JOIN theaters
        ON theaters.name == screenings.theater
    """)
        
    found = [{"performanceId": screening_id, "date": date, "startTime": time, "title": title, 
              "year": prod_year, "theater": theater, "remainingSeats": remaining_seats}
            for screening_id, date, time, theater, title, prod_year, remaining_seats  in c]
    response.status = 200
    return {"data": found}


@post('/tickets')
def postTickets():
    c = db.cursor()
    userInfo = request.json
    username = userInfo["username"]

    c.execute("""
    SELECT password
    FROM customers
    WHERE username = ?
    """, 
    [username]
    )
    passW = c.fetchone()
    if passW is None or passW[0] != hash(userInfo["pwd"]):
        response.status = 401
        return "Wrong user credentials"
    

    c.execute("""
        WITH ticket_count AS (
            SELECT screening_id, count(id) AS bought_tickets
            FROM screenings
            LEFT OUTER JOIN ticket
            USING (screening_id)
            GROUP BY screening_id
        )
        SELECT name, capacity - bought_tickets, screening_id
        FROM screenings
        JOIN ticket_count 
        USING (screening_id)
        JOIN theaters
        ON screenings.theater = theaters.name
        WHERE screening_id = ?
    """,
    [userInfo["performanceId"]])

    ticketsLeft = c.fetchone()[1]
    if ticketsLeft <= 0:
        response.status = 400
        return "No tickets left"
    
    c.execute("""
        INSERT 
        INTO ticket(screening_id, ticket_holder)
        VALUES (?, ?)
        RETURNING id
    """,
    [userInfo["performanceId"], userInfo["username"]])
    id = c.fetchone()[0]
    response.status = 201
    return f"/tickets/{id}"


@get('/users/<username>/tickets')
def getAllTicketsForUser(username):
    c = db.cursor()

    c.execute("""
        SELECT date, time, theater, title, prod_year, count() as nbrOfTickets
        FROM ticket
        JOIN screenings 
        USING (screening_id)
        JOIN movies
        USING (imdb_key)
        GROUP BY date, time, theater, title, prod_year, ticket_holder
        HAVING ticket_holder = ?
    """,
    [username])
    found = [{"date": date, "startTime": time, "theater": theater, "title": title, 
            "year": prod_year, "nbrOfTickets": nbrOfTickets}
        for date, time, theater, title, prod_year, nbrOfTickets  in c]
    
    return {"data": found}


run(host='localhost', port=PORT)    