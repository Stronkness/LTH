data <- read.csv("http://fileadmin.cs.lth.se/cs/Education/EDAA35/lab1/weather.txt")
rows <- nrow(data)
cols <- ncol(data)

dataTimeFirst <- read.csv("http://fileadmin.cs.lth.se/cs/Education/EDAA35/lab1/weather.txt")[1,1]
dataTimeLast <- read.csv("http://fileadmin.cs.lth.se/cs/Education/EDAA35/lab1/weather.txt")[rows,1]
TimeFirst <- head(dataTimeFirst)
TimeLast <- head(dataTimeLast)

TimeDiff <- (TimeLast - TimeFirst) / 3600
TimeDiff
