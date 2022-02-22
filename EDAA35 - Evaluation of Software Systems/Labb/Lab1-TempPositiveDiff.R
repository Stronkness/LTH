data <- read.csv("http://fileadmin.cs.lth.se/cs/Education/EDAA35/lab1/weather.txt")
rows <- nrow(data)
rows
cols <- ncol(data)
cols
dataTemp <- read.csv("http://fileadmin.cs.lth.se/cs/Education/EDAA35/lab1/weather.txt")[1:rows,2]

total.count <- 0
positive.count <- 0

total.count
positive.count

for(val in dataTemp){
  if(val > 0) positive.count = positive.count + 1
  
  total.count = total.count + 1
}

total.count
positive.count
