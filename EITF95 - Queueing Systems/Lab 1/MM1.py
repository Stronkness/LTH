import heapq
import random
import matplotlib.pyplot as plt
import numpy as np
from queue import Queue

signalList = []
def send(signalType, evTime, destination, info):
    heapq.heappush(signalList, (evTime, signalType, destination, info))

GENERATE = 1
ARRIVAL = 2
MEASUREMENT = 3
DEPARTURE = 4

simTime = 0.0
stopTime = 10000.0

class larger():
    def __gt__(self, other):
        return False

class generator(larger):
    def __init__(self, sendTo,lmbda):
        self.sendTo = sendTo
        self.lmbda = lmbda
        self.arrivalTimes = []
    def arrivalTime(self):
        return simTime + random.expovariate(self.lmbda)
    def treatSignal(self, x, info):
        if x == GENERATE:
            send(ARRIVAL, simTime, self.sendTo, simTime)  #Send new cusomer to queue
            send(GENERATE, self.arrivalTime(), self, [])  #Schedule next arrival
            self.arrivalTimes.append(simTime)


class queue(larger):
    def __init__(self, mu, sendTo):
        self.numberInQueue = 0
        self.sumMeasurements = 0
        self.numberOfMeasurements = 0
        self.measuredValues = []
        self.buffer = Queue(maxsize=0)
        self.mu = mu 
        self.sendTo = sendTo
    def serviceTime(self):
        return simTime + random.expovariate(self.mu)
    def treatSignal(self, x, info):
        if x == ARRIVAL:
            if(self.numberInQueue) < 7:
                if self.numberInQueue == 0:
                    send(DEPARTURE,self.serviceTime() , self, []) #Schedule  a departure for the arrival customer if queue is empty
                self.numberInQueue = self.numberInQueue + 1
            self.buffer.put(info)
        elif x == DEPARTURE:
            self.numberInQueue = self.numberInQueue - 1
            if self.numberInQueue > 0:
                send(DEPARTURE, self.serviceTime(), self, [])  # Schedule  a departure for next customer in queue
            send(ARRIVAL, simTime, self.sendTo, self.buffer.get())
        elif x == MEASUREMENT:
            self.measuredValues.append(self.numberInQueue)
            self.sumMeasurements = self.sumMeasurements + self.numberInQueue
            self.numberOfMeasurements = self.numberOfMeasurements + 1
            send(MEASUREMENT, simTime + random.expovariate(1), self, [])



class sink(larger):
    def __init__(self):
        self.numberArrived = 0
        self.departureTimes = []
        self.totalTime = 0
        self.T = []
    def treatSignal(self, x, info):
        self.numberArrived = self.numberArrived + 1
        self.departureTimes.append(info)
        self.totalTime = self.totalTime + simTime - info
        self.T.append(simTime - info)

          
  ###################################################
  #
  # Add code to create a queuing system  here
  #
  ###################################################
s = sink()
q = queue(10,s)
gen = generator(q,11)

#s1 = sink()
#q1 = queue(10,s1)
#gen1 = generator(q1,7)

send(GENERATE, 0, gen, [])
send(MEASUREMENT, 10.0, q, [])

#send(GENERATE, 0, gen1, [])
#send(MEASUREMENT, 10.0, q1, [])

while simTime < stopTime:
    [simTime, signalType, dest, info] = heapq.heappop(signalList)
    dest.treatSignal(signalType, info)





  ###################################################
  #
  # Add code to print final result
  #
  ###################################################
k =  np.array([i for i in range(0,20)])
rho = 7/10
pk = pow(rho,k)*(1-rho)

#plt.plot(q.measuredValues)
#plt.plot(q1.measuredValues)
#plt.hist(q.measuredValues,bins=100,density=True)
#plt.plot(k,pk)
#plt.hist(q1.measuredValues,bins=100,density=True)

#a = [0.136,0.20,0.34,0.99,25,439]
#plt.plot(a)

#plt.plot(s.T[2:101],s.T[1:100],'*')
#plt.plot(s.T)
#plt.show()

print('Mean number in queue: ', np.mean(q.measuredValues))
print('Mean response time T:', np.mean(s.T))

plt.hist(q.measuredValues, bins=10, density=True)
plt.show()
print('Call congestion: ', q.measuredValues[-1]/q.measuredValues)