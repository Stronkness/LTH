%% Preparatory assignment
    % Task 1: Match the pole-zero plots with their respective amplitude response function in figure 1
    % A: we have two conjugated poles at angles omega0, −omega0. The frequency response should then go 
    % up and down and again up as it showed in figure 4
    
    % B: we have two zeros at the angles 0, � and two conjugated poles at angles �!, −�!. The
    % frequency response starts from zero and peaks up, then go down to the zero then again up as it
    % showed in figure 3
    
    % C: two conjugated poles and two zeros; the frequency response go up and hardly down at the
    % angles omega0, −omega0 then again up as showed in figure 1
    
    % D: three poles and one zero, the frequency response starts with small radius at the angle omega0 =
    % 0. Go up at the angle omega0, down at the angle pi. And up again at the angle −omega0 as showed in the figure 2
    
    % Task 2: Sketch the approximate amplitude response functions of the pole-zero plots in figure 2
    sketch = imread('pat2.png');
    imshow(sketch);
    
%% Laboration Tasks

%% Exercise 1: 
    % What happens to the frequency response as you move the pole or the zero around the z-plane?
    % What is the relationship between the frequency response and the angle and radius of the pole or zero?

    % Answer (use mkiir): 
    % Depending on what we are moving, the frequency response will amplify or attenuate.
    % Moving the poles will cause to amplify the frequency response at the angle omega0 and
    % vice versa when we move the zeros the frequency response will attenuate
    % at the angle omega0
    %
    % The relationship between the frequency response, angle and radius is how much is the
    % attenuation/amplification. The angle omega0 for example will indicate where the
    % attenuation/amplification take a room on the frequency response, the radius will
    % determine how strong or soft the the attenuation/ amplification will be

%% Exercise 2: Fulfill the filter specification in the unshaded region
    % Answer:
    unshaded = imread('unshaded.png');
    imshow(unshaded);

%% Exercise 3: (jukebox) Which frequencies are disturbing the signal you selected? List these frequencies in a vector
    % Answer:
    % The song I listened to was Children - Robert Miles. It contains a high
    % disturbing frequency which cna be seen in the following figure
    children = imread('children.png');
    imshow(children)
    % where the high and low peaks indicate the disturbance sinusodial signal

    % Not sure if this can be done in a good-looking way but according to the
    % graph the frequencies that disturbs is estimated (in Hz)
    freq = [1050.05, 1250, 2850.1]

%% Exercise 4: (jukebox) Fixing the disturbance in the chosen song
    % r (radius) will be chosen to 0.4
    % As seen in the following figure compared to the previous one we notice
    % that the narrow peaks are removed
    fixed = imread('fixed.png');
    imshow(fixed)
    % and the song could be listened to without the high pitch noise








