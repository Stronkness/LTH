confidenceInterval <- source("https://fileadmin.cs.lth.se/cs/Education/EDAA35/R_resources.R")$value

plotresult <- function(file, start = 1)
{
  data <- read.csv(file)
  data <- data[start:nrow(data),]
  plot(data, type = "l")
}

Lab <- c()
for(i in 1:10){
  system("java -cp C:/IntelliJ-Java/Labb5-EDAA35/src/ Lab C:/IntelliJ-Java/Labb5-EDAA35/src/data2.txt result1.txt 600")
  data <- read.csv("result1.txt", sep = " ")
  data <- data[360:nrow(data),]
  Lab <- append(Lab, mean(data$Time))
}
print(mean(Lab))
print(confidenceInterval(Lab))


LabListSorter <- c()
for(i in 1:10){
  system("java -cp C:/IntelliJ-Java/Labb5-EDAA35/src/ ListSorter C:/IntelliJ-Java/Labb5-EDAA35/src/data2.txt result1.txt 600")
  dataListSorter <- read.csv("result1.txt", sep = " ")
  dataListSorter <- dataListSorter[360:nrow(dataListSorter),]
  LabListSorter <- append(LabListSorter, mean(dataListSorter$Time))
}
print(mean(LabListSorter))
print(confidenceInterval(LabListSorter))
print(t.test(LabListSorter, Lab))