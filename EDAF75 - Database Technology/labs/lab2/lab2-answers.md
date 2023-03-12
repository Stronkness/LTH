### Which relations have natural keys?
Movie --> Movie Teather - Name
Movie Teather --> Movie - IMDB key

Movie Teather --> Screening - Title, Screen-time
Screening --> Movie Teather - Name

Ticket --> Screening - Title, Start-time
Ticket --> Customer - Username

### Is there a risk that any of the natural keys will ever change?
Could be changed, but they are still unique

### Are there any weak entity sets?
Ticket

### In which relations do you want to use an invented key. Why?
Ticket. We need to include an ID to keep track of which customers bought the ticket even though there exist the same tickets. Must have unique ID.

### Keeping track of number of seats
- Capacity on screening. Downside: must create and update a new column for ticket amount. Upside: easier to check the condition
- Check if tickets don't exceed the movie teather capacity. Downside: need to calculate tickets. Upside: -||-