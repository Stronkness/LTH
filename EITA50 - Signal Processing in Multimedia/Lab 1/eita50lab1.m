%% Exercise 1
clear all
clc 
close all
load('ekg1.mat');
size(ekg1)
plot(ekg1)


%Does the signal come from the real world or is it a simulation? - real world, since it's heartbeats. 

%Does the signal contain any noise? - yes, looking at the plot it contains som noise around the peaks.

%Include the plot of the raw signal. - do you mean the plot we have here? 
%% Exercise 2
clear all
clc 
close all
load('ekg1.mat');

FT=1000; 
N=10000; 
n=0 : N-1; 
t=n/FT;

plot(t,ekg1)
xlabel('time (s)') 
ylabel('amplitude (microV)')


%Does the file itself contain data that allows you to calculate the time interval of the recording?
% - No, but we can calculate it with the help from the code above. Sampling frequency not included in file. 

%What is missing for you to know that the recording is ten seconds? - The sampling frequency

%(Include the plot with time scale on the t axis.)

%% Exercise 3-1
clear all
clc 
close all
load('ekg1.mat');

FT=1000; 
N=10000; 
n=0 : N-1; 
t=n/FT;
M=10000;
ekgspek=fft(ekg1,M);

plot(abs(ekgspek))


%% Exercise 3-2
clear all
clc 
close all
load('ekg1.mat');

FT=1000; 
N=10000; 
n=0 : N-1; 
t=n/FT;
M=10000;
ekgspek=fft(ekg1,M);

f=(0:M-1)/M*FT;
plot(f,abs(ekgspek)) 
xlabel('frequency (Hz)')

%axis ([0 500 0 800000])

axis ([0 20 0 150000])

%Does the signal mainly contain low, medium, or high frequencies? - Low (see later part of plot)

%What are the maximum frequencies that are interesting? - 500 is the largest, we sample at 1000Hz to catch all (Nyqvist rate)

%What causes the higher frequencies? - Frequencies above 500Hz are just mirrored of the lower ones (theorem) (lab manual)


%% Exercise 4
clear all
clc 
close all
load('ekg1.mat');

FT=1000; 
N=10000; 
n=0 : N-1; 
t=n/FT;

subplot(2,1,1) 
plot(t,ekg1)

%part 2 of excercise 4

h=0.2*ones(1,15); 
y=conv(ekg1,h); 
y=y(15:end);

subplot(2,1,2) 
plot(t,y)
 


%What kind of filter are you implementing? - FIR (finite impulse response), heartbeat = mipulse

%What is the filter called? - Low pass filter. (Filter the noise from a circuit as noise is high frequency)

%Does the filter work as expected? - Yes. 

%How can you see this? - It brings forth the things we want to see and reduces the noise.


 
