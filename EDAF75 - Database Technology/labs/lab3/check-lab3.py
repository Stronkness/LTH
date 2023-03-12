#!/usr/bin/env python

from collections import Counter
import itertools
import json
import random
import re
import requests
import urllib.parse


HOST="localhost"
PORT=7007


USERS = [("alice", "Alice Lidell", "ecila"),
         ("bob", "Bob Hund", "bob"),
         ("carol", "Carol Christmas", "lorac")]


# Movies taken from "Most Popular Movies" on IMDB
# (https://www.imdb.com/chart/moviemeter/?ref_=nv_mv_mpm)

# The titles were initially "The Dig", "The Little Things", "Palmer",
# and "Finding 'Ohana", but I've since removed characters in need of
# url-encoding, to simplify things for this lab
MOVIES = [("TheDig", "tt3661210", 2021),
          ("TheLittleThings", "tt10016180", 2021),
          ("Palmer", "tt6857376", 2021),
          ("FindingOhana", "tt10332588", 2021)]


THEATER_SIZES = {"Kino": 10,
                 "Regal": 16,
                 "Skandia": 100}


PERFORMANCES = [("tt3661210", "Regal", "2021-02-22", "19:00"),
                ("tt3661210", "Regal", "2021-02-22", "21:00"),
                ("tt10016180", "Skandia", "2021-02-22", "19:00")]


def url(resource):
    return f"http://{HOST}:{PORT}{resource}"


def response_to_dicts(r):
    return list(dict(d) for d in r.json()['data'])


def abort(msg):
    print(f"Error: {msg}")
    exit(1)


def abort_on_resource(method, resource, msg):
    abort(f"curl {method} {resource} failed: {msg}")


def require(found, expected, *msg):
    if not found == expected:
        for m in msg:
            print(f"    {m}")
        else:
            abort(f" expected {expected}, got {found}")


def check(method, resource):
    print(f"Checking: {method} {resource}")


def ok(method, resource):
    print(f"  => OK: {method} {resource}")


def fail(method, resource):
    print(f"Failed: {method} {resource}")


def check_ping():
    resource = url("/ping")
    check("GET", resource)
    try:
        r = requests.get(resource)
        require(r.status_code, 200)
        require(r.text.strip(), 'pong')
        ok("GET", resource)
    except Exception as e:
        abort_on_resource("GET", resource, "does not return pong -- is your server running?")


def check_reset():
    resource = url("/reset")
    check("POST", resource)
    try:
        r = requests.post(resource)
        # require(r.status_code, 205)
        ok("POST", resource)
    except Exception as e:
        abort_on_resource("POST", resource, f"crashes ({e})")


def check_post_user():
    resource = url("/users")
    check("POST", resource)
    try:
        for username, full_name, pwd in USERS:
            payload = {"username": username, "fullName": full_name, "pwd": pwd}
            r = requests.post(resource, json=payload)
            require(r.text.strip(), f"/users/{username}")
        ok("POST", resource)
    except Exception as e:
        abort_on_resource("POST", resource, f"crashes ({e})")
    

def check_post_movie():
    resource = url("/movies")
    check("POST", resource)
    try:
        for title, imdb_key, year in MOVIES:
            payload = {"imdbKey": imdb_key, "title": title, "year": year}
            r = requests.post(resource, json=payload)
            require(r.text.strip(), f"/movies/{imdb_key}")
        ok("POST", resource)
    except Exception as e:
        abort_on_resource("POST", resource, f"crashes ({e})")
    

def check_post_performances():
    resource = url("/performances")
    check("POST", resource)
    performances = []
    try:
        for imdb_key, theater, date, time in PERFORMANCES:
            payload = {"imdbKey": imdb_key, "theater": theater, "date": date, "time": time}
            r = requests.post(resource, json=payload)
            m = re.match("/performances/(.+)", r.text.strip())
            if not m:
                abort_on_resource("POST", resource, f"does not return a valid resource ({r.text.strip()})")
            else:
                performance_id = m.group(1)
                performances.append(performance_id)
        ok("POST", resource)
    except Exception as e:
        abort_on_resource("POST", resource, f"crashes ({e})")
    return performances


def check_get_movies():
    resource = url("/movies")
    check("GET", resource)
    try:
        r = requests.get(resource)
        movies = response_to_dicts(r)
        expected = set(imdb_key for _,imdb_key,_ in MOVIES)
        found = set(movie['imdbKey'] for movie in movies)
        require(found, expected)
        ok("GET", resource)
    except Exception as e:
        abort_on_resource("GET", resource, f"crashes ({e})")


def check_get_movies_with_query(title,year):
    resource = url(f"/movies?title={urllib.parse.quote(title)}&year={year}")
    check("GET", resource)
    try:
        r = requests.get(resource)
        movies = response_to_dicts(r)
        expected = set(imdb_key for t,imdb_key,y in MOVIES if t == title and y == year)
        found = set(movie['imdbKey'] for movie in movies)
        require(found, expected)
        ok("GET", resource)
    except Exception as e:
        abort_on_resource("GET", resource, f"crashes ({e})")


def check_get_movies_with_queries():
    for title,imdb_key,year in MOVIES:
        check_get_movies_with_query(title, year)
    check_get_movies_with_query("", 0)
    

def check_get_movies_with_id(imdb_key):
    resource = url(f"/movies/{imdb_key}")
    check("GET", resource)
    try:
        r = requests.get(resource)
        movies = response_to_dicts(r)
        expected = set(imdb_key for _,key,_ in MOVIES if key == imdb_key)
        found = set(movie['imdbKey'] for movie in movies)
        require(found, expected)
        ok("GET", resource)
    except Exception as e:
        abort_on_resource("GET", resource, f"crashes ({e})")


def check_get_movies_with_ids():
    for _,imdb_key,_ in MOVIES:
        check_get_movies_with_id(imdb_key)
    check_get_movies_with_id("not_a_real_imdb_key")


def check_get_performances(actual_performances):
    resource = url("/performances")
    check("GET", resource)
    try:
        r = requests.get(resource)
        returned_performances = response_to_dicts(r)
        expected = set(actual_performances)
        found = set(performance['performanceId'] for performance in returned_performances)
        require(found, expected)
        for p in returned_performances:
            theater = p["theater"]
            remaining_seats = p["remainingSeats"]
            require(THEATER_SIZES[theater], remaining_seats)
        ok("GET", resource)
    except Exception as e:
        abort_on_resource("GET", resource, f"crashes ({e})")


def valid_ticket(reply):
    return not re.match("/tickets/(.+)", reply.strip()) is None


def random_user():
    return random.choice(USERS)


def check_ticket_hoarding():
    r = requests.get(url("/performances"))
    found_performances = response_to_dicts(r)
    seats_left = {p['performanceId']: p['remainingSeats'] for p in found_performances}
    perf_ids = [[p_id] * (count+2) for p_id, count in seats_left.items()]
    all_attempts = list(itertools.chain(*perf_ids))
    random.shuffle(all_attempts)
    users = {username: Counter() for username,_,_ in USERS}
    resource = url("/tickets")
    check("POST", resource)
    for p_id in all_attempts:
        username,_,pwd = random_user()
        payload = {"username": username, "pwd": pwd, "performanceId": p_id}
        r = requests.post(resource, json=payload)
        if seats_left[p_id] > 0:
            require(r.status_code, 201)
            require(valid_ticket(r.text), True, f"{r.text.strip()} is not a valid ticket resource")
            seats_left[p_id] -= 1
            users[username][p_id] += 1
        else:
            require(r.status_code, 400)
            require(r.text.strip(), "No tickets left")
    ok("POST", resource)
    r = requests.get(url("/performances"))
    for p in response_to_dicts(r):
        require(p['remainingSeats'], 0, "after a ticket sales bonanza, there should be no tickets left for the performances")
    ok("GET", url("/performances"))
    perf_id_lookup = create_performance_id_lookup()
    for username, counts in users.items():
        resource = url(f"/users/{username}/tickets")
        r = requests.get(resource)
        summary = response_to_dicts(r)
        for perf_info in summary:
            date = perf_info["date"]
            start_time = perf_info["startTime"]
            theater = perf_info["theater"]
            require(perf_info["nbrOfTickets"], users[username][perf_id_lookup[(theater, date, start_time)]], "the number of tickets for a user doesn't add up")
        ok("GET", resource)


def create_performance_id_lookup():
    r = requests.get(url("/performances"))
    perf_lookup = dict()
    for p in response_to_dicts(r):
        perf_id = p["performanceId"]
        date = p["date"]
        start_time = p["startTime"]
        theater = p["theater"]
        perf_lookup[(theater, date, start_time)] = perf_id
    return perf_lookup


def main():
    check_ping()
    check_reset()
    check_post_user()
    check_post_movie()
    performances = check_post_performances()
    check_get_movies()
    # check_get_movies_with_queries()
    check_get_movies_with_ids()
    check_get_performances(performances)
    check_ticket_hoarding()
    print()
    print("==========================================")
    print("           Initial version                ")
    print("Your server passes our tests -- well done!")
    print("==========================================")


if __name__ == '__main__':
    main()