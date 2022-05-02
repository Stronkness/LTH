function mkiir
    Modify = {'Poles', 'Zeros'};
    Filters = {'None', 'Low pass', 'High pass', 'Band pass'};
    
    Designs = {
        {nan, nan, nan, nan},...
        {[0, .1, .1, 0], [-3, -3, -80, -80], [0, .2, .2, .5, .5, 0], [0, 0, -40, -40, 20, 20]},...
        {[0, .1, .1, .5, .5, 0], [-40, -40, 0, 0, 20, 20], [.5, .2, .2, .5], [-3, -3, -80, -80]},...
        {[0, .15, .15, .35, .35, .5, .5, 0], [-40, -40, 0, 0, -40, -40, 20, 20], [.2, .2, .3, .3], [-80, -3, -3, -80]},...
        {[0, .2, .2, .2, .5, .5, 0], [0, 0, -20, 3, 3, 20, 20], [0, .18, .18, .22, .22, .5, .5, 0], [-3, -3, -80, -80, -3, -3, -80, -80]},...
    };
    
    Panels = {
        {{'filter', 'panel'},      'Filter',       [  1,  1, 80, 35]}, ...
        {{'settings', 'panel'},    'Settings',     [82,  1,  30, 35]}, ...
    };

    Axes = {
        {{'plots', 'pzplot'},       [10, 14, 66, 20]}, ...
        {{'plots', 'freqresp'},     [10, 2,  66, 10]}, ...
    };

    Settings = {
        {[],                        'text',         'Modify poles or zeros',    [ 1, 31, 28, 1.25]}, ... 
        {[],                        'text',         'Filter prototype',         [ 1, 27, 28, 1.25]}, ...
        {[],                        'text',         'Filter gain [dB]',         [ 1, 19, 28, 1.25]}, ... 
        ...
        {{'settings', 'gain'},      'edit',         '-40',                      [1, 17, 28, 1.75]}, ...
        {{'settings', 'modify'},    'popupmenu',    Modify,                     [1, 29, 28, 1.75]}, ...
        {{'settings', 'design'},    'popupmenu',    Filters,                    [1, 25, 28, 1.75]}, ...
        ...
        {{'settings', 'reset'},     'pushbutton',   'Reset filter',             [1,  1, 28, 1.75]}, ...
        {{'settings', 'export'},    'pushbutton',   'Export filter',            [1,  5, 28, 1.75]}, ...
        {{'settings', 'import'},    'pushbutton',   'Import filter',            [1,  7, 28, 1.75]}, ...
        {{'settings', 'switch'},    'pushbutton',   'Switch poles and zeros',   [1, 9, 28, 1.75]}, ...
        {{'settings', 'mirror'},    'pushbutton',   'Mirror poles and zeros',   [1, 11, 28, 1.75]}, ...
    };

    %%
    g = gui.window('MainWindow', 'IIR Filter Design', [114 36]);

    addpanels(g, Panels, 'MainWindow');
    addaxes(g, Axes, {'filter', 'panel'});
    addcontrols(g, Settings, {'settings', 'panel'});
    
    states = struct();
    states.points = {[], []};
    states.moving = [];
    handles = g.Controls;

    objects = findall(handles.MainWindow, '-property', 'FontWeight');
    set(objects, 'FontWeight', 'bold');
    
    pztick = linspace(-1, 1, 5);
    fxtick = linspace(0, .5, 6);
    fytick = linspace(-80, 20, 6);
    
    set(handles.plots.pzplot, 'Unit', 'pixels');
    pzsize = get(handles.plots.pzplot, 'Position');
    pzratio = pzsize(3)/pzsize(4);
    set(handles.plots.pzplot, 'Unit', 'character');
        
    set(handles.plots.pzplot, 'XTick', pztick, 'YTick', pztick, 'XLim', [-1.2, 1.2]*pzratio, 'YLim', [-1.2, 1.2]);
    set(handles.plots.freqresp, 'XTick', fxtick, 'YTick', fytick, 'XLim', [0, .5], 'YLim', [-80, 20]);
    set(handles.MainWindow, 'WindowButtonDownFcn', @MainWindowButtonDownFcn);
    set(handles.MainWindow, 'WindowButtonUpFcn', @MainWindowButtonUpFcn);
    set(handles.MainWindow, 'WindowButtonMotionFcn', @MainWindowButtonMotionFcn);
    set(handles.settings.gain, 'Callback', @(varargin) UpdateInfo);
    set(handles.settings.design, 'Callback', @DesignCallback);
    set(handles.settings.reset, 'Callback', @ResetCallback);
    set(handles.settings.import, 'Callback', @ImportCallback);
    set(handles.settings.export, 'Callback', @ExportCallback);
    set(handles.settings.switch, 'Callback', @SwitchCallback);
    set(handles.settings.mirror, 'Callback', @MirrorCallback);
    set(handles.MainWindow, 'Visible', 'on');

    
    xlabel(handles.plots.pzplot, 'Real axis');
    ylabel(handles.plots.pzplot, 'Imaginary axis');
    xlabel(handles.plots.freqresp, 'Normalized frequency');
    ylabel(handles.plots.freqresp, 'Ampliture response');
    
    PrepareInfo;
    DesignCallback;
    UpdateInfo;

    function MainWindowButtonDownFcn(varargin)
        CurrentObject = get(handles.MainWindow, 'CurrentObject');
        CurrentParent = get(CurrentObject, 'Parent');

        if(all([CurrentObject, CurrentParent] ~= handles.plots.pzplot))
            return;
        end
        
        SelectionType = get(handles.MainWindow, 'SelectionType');
        CurrentPoint = get(handles.plots.pzplot, 'CurrentPoint');
        which = get(handles.settings.modify, 'value');
                  
        zp = complex(CurrentPoint(1,1), CurrentPoint(1,2));

        [distance, nearest] = findnearest(states.points{which}, zp);
            
        if distance < 0.05
            if strcmp(SelectionType, 'alt')
                states.points{which}(nearest) = [];
            else
                states.moving = [which, nearest];
            end
        else
            if strcmp(SelectionType, 'normal')
                if abs(imag(zp)) < 0.1
                    zp = real(zp);
                end
                
                if abs(zp) > 1
                    zp = zp/abs(zp);
                end

                states.points{which} = vertcat(states.points{which}, zp);
                states.moving = [which, size(states.points{which},1)];
            end
        end
        
        UpdateInfo;
    end

    function MainWindowButtonUpFcn(varargin)
        states.moving = [];
    end

    function MainWindowButtonMotionFcn(varargin)
        if states.moving
            CurrentPoint = get(handles.plots.pzplot, 'CurrentPoint');
        
            which = states.moving(1);
            nearest = states.moving(2);
                   
            zp = complex(CurrentPoint(1,1), CurrentPoint(1,2));
            
            if abs(imag(zp)) < 0.02
                zp = real(zp);
            end
            
            if abs(zp) > 1
                zp = zp/abs(zp);
            end
            
            states.points{which}(nearest) = zp;
            
            UpdateInfo;
        end
    end

    function PrepareInfo
        
        t = linspace(0, 2*pi, 101);
        
        h21 = patch(nan, nan, [1, .75, .75], 'Parent', handles.plots.freqresp);
        h32 = patch(nan, nan, [1, .75, .75], 'Parent', handles.plots.freqresp);
        h1 = line(nan, nan, 'Parent', handles.plots.freqresp);
        
        h4 = line([sin(t), nan, -10, 10, nan, 0, 0], [cos(t), nan, 0, 0, nan, -10, 10], 'Parent', handles.plots.pzplot);
        h5 = line(nan, nan, 'Parent', handles.plots.pzplot);
        h6 = line(nan, nan, 'Parent', handles.plots.pzplot);
        h7 = line(nan, nan, 'Parent', handles.plots.pzplot);
        h8 = line(nan, nan, 'Parent', handles.plots.pzplot);
        
        set(h1, 'LineWidth', 2, 'Color', [0, 0, 1]);
        set(h4, 'LineStyle', ':', 'Color', [.5, .5, 1]);
        set([h5, h6], 'LineStyle', 'none', 'Marker', 'x', 'MarkerSize', 8, 'MarkerEdgeColor', 'r', 'LineWidth', 2);
        set([h7, h8], 'LineStyle', 'none', 'Marker', 'o', 'MarkerSize', 8, 'MarkerEdgeColor', 'r', 'LineWidth', 2);
    
        states.plot.response = h1;
        states.plot.upper = h21;
        states.plot.lower = h32;
        states.plot.poles1 = h5;
        states.plot.poles2 = h6;
        states.plot.zeros1 = h7;
        states.plot.zeros2 = h8;
    end

    function UpdateInfo
        poles = states.points{1};
        zeros = states.points{2};
        gain = gui.getdouble(handles.settings.gain);
        
        if isnan(gain)
            gain = 0;
        end
        
        amp = 10^(gain/20);
        f = linspace(0, .5, 1001);
        
        [num, den] = zp2tf([zeros; conj(zeros)], [poles; conj(poles)], amp);

        h = polyval(num, exp(1j*2*pi*f)) ./ polyval(den, exp(1j*2*pi*f));
        a = 20*log10(abs(h));
        i = impz(num, den, 200);
        
        set(states.plot.response, 'XData', f, 'YData', a);
        set(states.plot.poles1, 'XData', real(poles), 'YData', imag(poles));
        set(states.plot.poles2, 'XData', real(poles), 'YData', -imag(poles));
        set(states.plot.zeros1, 'XData', real(zeros), 'YData', imag(zeros));
        set(states.plot.zeros2, 'XData', real(zeros), 'YData', -imag(zeros));
    end

    function DesignCallback(varargin)
        which = get(handles.settings.design, 'Value');
        set(states.plot.upper, 'XData', Designs{which}{1}, 'YData', Designs{which}{2});
        set(states.plot.lower, 'XData', Designs{which}{3}, 'YData', Designs{which}{4});
    end

    function MirrorCallback(varargin)
        poles = states.points{1};
        zeros = states.points{2};
        states.points = {-poles, -zeros};
        UpdateInfo;
    end

    function SwitchCallback(varargin)
        poles = states.points{1};
        zeros = states.points{2};
        states.points = {zeros, poles};
        UpdateInfo;
    end

    function ResetCallback(varargin)
        states.points = {[], []};
        set(handles.settings.gain, 'String', -40);
        UpdateInfo;
    end

    function ExportCallback(varargin)
        file = uiputfile('*.mat');
        
        if file == 0
            return;
        end
        
        poles = states.points{1};
        zeros = states.points{2};
        gain = gui.getdouble(handles.settings.gain);
        
        if isnan(gain)
            gain = 0;
        end
        
        [num, den] = zp2tf([zeros; conj(zeros)], [poles; conj(poles)], 10^(gain/20));
        
        s.b = num;
        s.a = den;
        
        save(file, '-struct', 's', 'b', 'a');
    end

    function ImportCallback(varargin)
        [file, path] = uigetfile('*.mat');
        
        if file == 0
            return;
        end
        
        s = load(fullfile(path, file));
        
        if all(isfield(s, {'b', 'a'}))
            [zeros, poles, gain] = tf2zpk(s.b, s.a);
        elseif all(isfield(s, {'z', 'p', 'k'}))
            zeros = s.z;
            poles = s.p;
            gain = s.k;
        else
            error('Not a valid filter file.');
        end

        try
            poles = cplxpair(poles);
            zeros = cplxpair(zeros);
        catch
            error('Poles and zeros must be in complex conjugate pairs.');
        end

        if mod(numel(poles), 2) == 1 || mod(numel(zeros), 2) == 1
            error('Only odd-numbered polynomial orders allowed.');
        end
        
        poles(imag(poles)<0 | imag(poles)==0 & abs(poles)>1) = [];
        zeros(imag(zeros)<0 | imag(zeros)==0 & abs(zeros)>1) = [];

        states.points = {poles, zeros};
        
        set(handles.settings.gain, 'String', sprintf('%f', 20*log10(gain)));
        
        UpdateInfo;
    end
end

function [distance, nearest] = findnearest(zp, p)
    d1 = abs(zp - p);
    d2 = abs(conj(zp) - p);

    [distance, nearest] = min(min([d1, d2], [], 2));
end

