#Förberedelseuppgift
data <- read.csv(file.choose())
summary(data)
str(data)
head(data)

removeNA <- function(data) {
  tempData <- data[complete.cases(data),]
  return(tempData)
}

a <- c(12, NA, 10, NA)
b <- c(NA, NA, 15, 20)
df <- data.frame(a, b)
removeNA(df)
nrow(removeNA(data))
removeNA(data)



#Laborationsuppgift 13.3.1
analysePotentialOutliners <- function(data, threshold) {
  if(length(threshold) < 4){
    newData <- data.frame(
      Variable = c("variable1", "variable2", "variable3"),
      NrPotentialOutliners = c(0,0,0),
      MeanNoOutliners = c(0,0,0)
    )

    potentialOutlinersCounter1 <- 0
    potentialOutlinersCounter2 <- 0
    potentialOutlinersCounter3 <- 0

    noOutliner1 <- vector()
    noOutliner2 <- vector()
    noOutliner3 <- vector()

    for(num in data$variable1){
      if(num > threshold[1]){
        potentialOutlinersCounter1 <- potentialOutlinersCounter1 + 1
      }else{
        noOutliner1 <- append(noOutliner1, num, length(noOutliner1))
      }
    }

    for(num in data$variable2){
      if(num > threshold[2]){
        potentialOutlinersCounter2 <- potentialOutlinersCounter2 + 1
      }else{

        if(length(noOutliner2) == 0){
          noOutliner2 <- num
        }else{
          noOutliner2 <- append(noOutliner2, num, length(noOutliner2))
        }
      }
    }

    for(num in data$variable3){
      if(num > threshold[3]){
        potentialOutlinersCounter3 <- potentialOutlinersCounter3 + 1
      }else{

        if(length(noOutliner3) == 0){
          noOutliner3 <- num
        }else{
          noOutliner3 <- append(noOutliner3, num, length(noOutliner3))
        }
      }
    }

    newData$NrPotentialOutliners[1] <- potentialOutlinersCounter1
    newData$NrPotentialOutliners[2] <- potentialOutlinersCounter2
    newData$NrPotentialOutliners[3] <- potentialOutlinersCounter3

    newData$MeanNoOutliners[1] <- mean(noOutliner1)
    newData$MeanNoOutliners[2] <- mean(noOutliner2)
    newData$MeanNoOutliners[3] <- mean(noOutliner3)

    return(newData)
    
  }else{
    print("Please choose a vector with maximum 3 numbers!")
    return(frame <- data.frame())
  }
}

analysePotentialOutliners(removeNA(data), c(5, 5, 0))



#Laborationsuppgift 13.3.2
contributors <- function(file, n) {
  fixedData <- read.csv(file.choose(), sep="|") #Seperate after every |
  names(fixedData) <- c("R.", "Developer","Date", "Lines") #4 groups
  commitAmount = sort(table(fixedData$Developer), decreasing=TRUE) #Sort Developer with highest commits first, decreasing
  barplot(commitAmount[1:n])
  
  dateVector <- vector()
  fixedData <- fixedData[order(as.Date(fixedData$Date), decreasing=TRUE),] #Latest date first
  developers <- fixedData[!duplicated(fixedData$Developer), "Developer"] #Insert Developers in dataframe, if not duplicated insert

  for (developer in developers) {
    latestCommit <- fixedData[fixedData$Developer == developer, "Date"][1] #If the Developer = Developer in this Date, insert the last commit date to the developer
    dateVector[developer] = toString(as.Date(latestCommit)) #Change the date and developer toString to get the result the lab wants
  }
  sort(dateVector) #Sort with the newest date first
  return(dateVector)
}

contributors("newfile.txt", 4)


