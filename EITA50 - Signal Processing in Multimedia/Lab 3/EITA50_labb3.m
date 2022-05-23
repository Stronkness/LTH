%% Exercise 1

N = 256;
n = (0:N-1);
f = 12/N; % normalized frequency
%f = 30/N; % try different frequencies
x = square(2*pi*n*f); % square pulse signal with frequency f

L = 7;
h = 1/L * ones(1,L); % 7-tap moving average filter y_h = filter(h, 1, x);
y_h = filter(h, 1, x);

subplot(2, 1, 1); plot(n, x, '.-'); 
subplot(2, 1, 2); plot(n, y_h, '.-');

%% Questions 1:

% Try different frequencies of the square signal and see what happens to the filtered signal.

% The signals intervall changes but also the amplitude of the signal after
% filtering (depends on the frequency), the amplitude gets lower with higher frequencies

%  The moving average filter has a smoothing effect on the input signal. And as since it is a moving average 
% filter, so it is a low pass filter which removes the high frequencies in our signal.

%% Exercise 2

N = 256;
n = (0:N-1);
f = 12/N; % normalized frequency
x = square(2*pi*n*f); % square pulse signal with frequency f

g = [zeros(1, floor(L/2)) 1 zeros(1, floor(L/2))] - h; 
y_g = filter(g, 1, x);

subplot(2, 1, 1); plot(n, x, '.-');
subplot(2, 1, 2); plot(n, y_g, '.-');



%% Questions 2:

%Explore the differences between the filtered signal when using the filters 
%h(n) and g(n) at different frequencies of the input signal. What happens to 
%the input signal at sharp edges, and what happens on areas with constant amplitude?

% With higher frequencies the sharp edges become "rectangles", and with
% lower frequencies we get ofcourse less sharp edges but they still keep
% their sharp edges. The filtered signal only get constant amplitude at
% higher frequencies, otherise it just got the spikes which doesn't
% indicate of a constant amplitude

% We can notice that the used filter is a high pass filter due to peaks in the filtered input signal. 
% The sharp edges in the filtered input signal represent the high
% frequencies of our input signal. While the constant areas “which are equals to zero”
% represent the low frequencies in our input signal.

%% Exercise 3

H = fft(h,128); 
G = fft(g,128);

%L = 20;

subplot(2, 2, 1); stem(h);
axis([0 L (-1/L)*2 1]);
title('h(n)'); xlabel('Time index n'); ylabel('Amplitude');

subplot(2, 2, 2); stem(g);
axis([0 L (-1/L)*2 1]);
title('g(n)'); xlabel('Time index n'); ylabel('Amplitude');

subplot(2, 2, 3); plot(0:(2)/128:(1-1/128)*2,abs(H));
axis([0 1 0 max([abs(H), abs(G)])]);
title('H(\omega)'); xlabel('\omega / \pi'); ylabel('Amplitude');

subplot(2, 2, 4); plot(0:(2)/128:(1-1/128)*2,abs(G));
axis([0 1 0 max([abs(H), abs(G)])])
title('G(\omega)'); xlabel('\omega / \pi'); ylabel('Amplitude');

%% Questions 3:

%What is the frequency characteristic of these two filters? (i.e. which one 
%is lowpass and which one is highpass) Change the filter length L. Do you observe 
%any change in the frequency response of the filters?

% H is lowpass and G is highpass. There is no change in the frequency
% response at different L values

%% Exercise 4
clear all
clc 
close all

I = imread('laboration-files/data/grace-hopper.tif'); 
x2 = double(I)/255;
imshow(x2);

L = 5;
%L = 1; %clear
%L = 10; %blurry
h2 = 1/(L*L) * ones(L); 
y_h2 = filter2(h2, x2); 
imshow(y_h2);

%% Questions 4: 

%Look at the image in Matlab.
%Apply the two-dimensional moving average filter and look at the picture.
%What effect does it have on the picture?

% With higher L the image ges blurried 
% Using the moving average filter which is a low pass filter as mentioned earlier.

%% Exercise 5
clear all
clc 
close all

I = imread('laboration-files/data/grace-hopper.tif'); 
x2 = double(I)/255;
%imshow(x2);

L = 25;
h2 = 1/(L*L) * ones(L); 

delta = zeros(L); 
delta(ceil(L/2),ceil(L/2)) = 1; 
g2 = delta - h2;
y_g2 = filter2(g2, x2); 
imshow(y_g2);


%% Questions 5: 

%Explore the differences between the filtered image when using the filters 
%h(m,n) and g(m,n) at different frequencies of the input signal. What happens 
%to the input signal at sharp edges, and what happens on areas with constant amplitude?

% H2 makes it blurrier, however G2 at low L makes the picture black and not
% very viewable. At higher L the contours begin to show and slowly
% processes the image to be more clear for even higher L. It focuses around
% the edges which is represents the dividing lines betweem the black and
% white areas (difference in brightness)

% We can say that its a highpass filter as it allows high frequencies (high
% brightness) to pass.

% Object recognition in computer vision

%% Exercise 6
clear all
clc 
close all

L = 2;
h2 = 1/(L*L) * ones(L);
delta = zeros(L); 
delta(ceil(L/2),ceil(L/2)) = 1; 
g2 = delta - h2;

N = 32;
H2 = fftshift(fft2(h2, N, N));
G2 = fftshift(fft2(g2, N, N));
M = max(max([abs(H2), abs(G2)]));

n = -(L-1)/2:(L-1)/2; 
w = -(N-1)/2:(N-1)/2;

subplot(2, 2, 1); 
stem3(n, n, h2); 
set(gca, 'ZLim', [-0.5, 1]);

subplot(2, 2, 2) 
stem3(n, n, g2); 
set(gca, 'ZLim', [-0.5, 1]); 

subplot(2, 2, 3); 
surf(w, w, abs(H2));

subplot(2, 2, 4);
surf(w, w, abs(G2));


%% Questions 6: 

%What is the frequency characteristic of these two filters? (i.e. which one 
%is lowpass and which one is highpass) Change the filter length L. Do you 
%observe any change in the frequency response of the filters?

% The first one is lowpass and the second is highpass. With lower L the
% output gets more wider and with higher L it becomes more like a spike.


