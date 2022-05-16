import heapq
import random
import numpy as np
from queue import Queue
import time

#import matplotlib as mpl
#mpl.use('tkagg')
import matplotlib.pyplot as plt

signalList = []

def send(signalType, evTime, destination, info):
    heapq.heappush(signalList, (evTime, signalType, destination, info))

GENERATE = 1
ARRIVAL = 2
MEASUREMENT = 3
DEPARTURE = 4

simTime = 0.0
stopTime = 10000.0
bufferSize = 4
        
class larger():
    def __gt__(self, other):
        return False


class generator(larger):
    def __init__(self, sendTo,lambda1):
        self.sendTo = sendTo
        self.lambda1 = lambda1
        self.arrivalTimes = []
    def arrivalTime(self):
        return simTime + random.expovariate(self.lambda1)
        # return simTime + 1.0
    def treatSignal(self, x, info):
        if x == GENERATE:
            send(ARRIVAL, simTime, self.sendTo, simTime)
            send(GENERATE, self.arrivalTime(), self, [])
            self.arrivalTimes.append(simTime)


class queue(larger):
    def __init__(self, mu, L, sendTo1,sendTo2,alpha,s):
        self.numberInQueue = 0
        self.sumMeasurements = 0
        self.numberOfMeasurements = 0
        self.measuredValues = []
        self.arrivalTimes = []
        self.buffer = Queue(maxsize=0)
        self.mu = mu 
        self.L = L
        self.alpha = alpha
        self.sendTo1 = sendTo1
        self.sendTo2 = sendTo2
        self.s = s
        # Preparatory task 5
        self.Nq = []
        self.Ts = []

    def serviceTime(self):
        # Preparatory task 5
        #a = random.expovariate(self.mu)
        #a = 1 / self.mu
        choice = random.uniform(0, 1.0)
        if choice < 1/1.5:
            a = random.expovariate(2*self.mu)
            self.Ts.append(a)
            return a + simTime
        a = random.expovariate(self.mu/2)
        self.Ts.append(a)
        return simTime + a
        # return simTime + 1.0
    def treatSignal(self, x, info):
        if x == ARRIVAL:
            if self.numberInQueue < self.L: 
                if self.numberInQueue == 0:
                    send(DEPARTURE, self.serviceTime(), self, []) #Schedule  a departure for the arrival customer if queue is empty
                self.numberInQueue = self.numberInQueue + 1
            self.buffer.put(info)
            self.arrivalTimes.append(simTime)

        elif x == DEPARTURE:
            self.numberInQueue = self.numberInQueue - 1
            if self.numberInQueue > 0:
                send(DEPARTURE,  self.serviceTime(), self, [])  # Schedule  a departure for next customer
            tid = self.buffer.get()
            if random.uniform(0, 1) <= self.alpha :
               if self.sendTo1:
                  send(ARRIVAL, simTime, self.sendTo1, tid)
               send(ARRIVAL, simTime, self.s, tid)
            else : # rand >= self.alpha:
               if self.sendTo2:
                  send(ARRIVAL, simTime, self.sendTo2, tid) 
               send(ARRIVAL, simTime, self.s, tid)


        elif x == MEASUREMENT:
            self.measuredValues.append(self.numberInQueue)
            self.sumMeasurements = self.sumMeasurements + self.numberInQueue
            self.numberOfMeasurements = self.numberOfMeasurements + 1
            send(MEASUREMENT, simTime + random.expovariate(1), self, [])
            # Preparatory task 5
            if(self.numberInQueue == 0):
                self.Nq.append(0)
            else:
                self.Nq.append(self.numberInQueue - 1)

        

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






# Parameters are given values
[ lambda1,lambda2] = [7.5, 10]  #Arrival rate  
[mu1,mu2,mu3,mu4,mu5] = [10, 14, 22, 9, 11]     # Service rate
[L1,L2,L3,L4,L5] = [np.inf, np.inf, np.inf, np.inf, np.inf]    
# [L1,L2,L3,L4,L5] = [4, 10, 3, 20, 7] 
alpha = 0.4

          
  ###################################################
  #
  # Add code to create a queuing system  here
  #
  ###################################################

s = sink()
s1 = sink()
s2 = sink()
s3 = sink()
s4 = sink()
s5 = sink()

# (self, mu, L, sendTo1,sendTo2,alpha,s):, labb 1 var sendTo sink
# q5 = queue(mu5, (1-alpha)*(lambda1 + lambda2), s, s, 1, s)
# q4 = queue(mu4, alpha*(lambda1 + lambda2), s, s, 1, s)
# q3 = queue(mu3, lambda1 + lambda2, q4, q5, alpha, s1)
# q2 = queue(mu2, lambda2, q3, q3, 1, s2)
# q1 = queue(mu1, lambda1, q3, q3, 1, s3)

q5 = queue(mu5, L5, s, s, 1, s5)
q4 = queue(mu4, L4, s, s, 1, s4)
q3 = queue(mu3, L3, q4, q5, alpha, s3)
q2 = queue(mu2, L2, q3, q3, 1, s2)
q1 = queue(mu1, L1, q3, q3, 1, s1)

queue = [q1,q2,q3,q4,q5]

gen1 = generator(q1,lambda1)
gen2 = generator(q2,lambda2)

send(GENERATE, 0, gen1, [])
send(MEASUREMENT, 10.0, q1, [])

send(GENERATE, 0, gen2, [])
send(MEASUREMENT, 10.0, q2, [])

send(MEASUREMENT, 10.0, q3, [])
send(MEASUREMENT, 10.0, q4, [])
send(MEASUREMENT, 10.0, q5, [])

while simTime < stopTime:
    [simTime, signalType, dest, info] = heapq.heappop(signalList)
    dest.treatSignal(signalType, info)



  ###################################################
  #
  # Add code to print final result
  #
  ###################################################

totalN = 0
for q in queue:
    print("Nq: {}", np.sum(q.Nq)/q.numberOfMeasurements)
    print("Ts: {}", np.sum(q.Ts)/len(q.Ts))
    print(np.mean(q.measuredValues))
    totalN += np.sum(q.Nq)/q.numberOfMeasurements

print(np.mean(s1.T))
print(np.mean(s2.T))
a1 = lambda1/(lambda1+lambda2)
print(np.mean(s3.T) - a1*np.mean(s1.T)-(1-a1)*np.mean(s2.T))
print(np.mean(s4.T) - np.mean(s3.T))
print(np.mean(s5.T) - np.mean(s3.T))

total_time = np.mean(s1.T) + np.mean(s2.T) + (np.mean(s3.T) - a1*np.mean(s1.T)-(1-a1)*np.mean(s2.T)) + (np.mean(s4.T) - np.mean(s3.T)) + (np.mean(s5.T) - np.mean(s3.T))
Little = totalN / total_time
print(Little) # Verified Little's thereom