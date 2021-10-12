package factory.controller;

import factory.model.DigitalSignal;
import factory.model.WidgetKind;
import factory.simulation.Painter;
import factory.simulation.Press;
import factory.swingview.Factory;

/**
 * Implementation of the ToolController interface,
 * to be used for the Widget Factory lab.
 * 
 * @see ToolController
 */
public class LabToolController implements ToolController {
    private final DigitalSignal conveyor, press, paint;
    private final long pressingMillis, paintingMillis;
    private boolean pressDone;
    private boolean paintDone;
    
    public LabToolController(DigitalSignal conveyor, DigitalSignal press, DigitalSignal paint, long pressingMillis, long paintingMillis) {
        this.conveyor = conveyor;
        this.press = press;
        this.paint = paint;
        this.pressingMillis = pressingMillis;
        this.paintingMillis = paintingMillis;
    }

    @Override
    public synchronized void onPressSensorHigh(WidgetKind widgetKind) throws InterruptedException {
        if (widgetKind == WidgetKind.BLUE_RECTANGULAR_WIDGET) {
        	pressDone = false;
        	conveyorControl();
            press.on();
            waitOutside(pressingMillis);
            press.off();
            waitOutside(pressingMillis);
            pressDone = true;
            conveyorControl();
        }
    }

    @Override
    public synchronized void onPaintSensorHigh(WidgetKind widgetKind) throws InterruptedException {  	
        if (widgetKind == WidgetKind.ORANGE_ROUND_WIDGET) {
        	paintDone = false;
        	conveyorControl();
        	paint.on();
        	waitOutside(paintingMillis);
            paint.off();
            paintDone = true;
            conveyorControl();
        }
    }
    
	private synchronized void conveyorControl() {
    	if(pressDone == true && paintDone == true) {
    		conveyor.on();
    	}else {
    		conveyor.off();
    	}
    }
	
	private void waitOutside(long millis) throws InterruptedException {
		long timeToWakeUp = System.currentTimeMillis() + millis;

		while (timeToWakeUp > System.currentTimeMillis() ) {
			long dt = timeToWakeUp - System.currentTimeMillis();
			wait(dt);
		}
	}

    
    // -----------------------------------------------------------------------
    
    public static void main(String[] args) {
        Factory factory = new Factory();
        ToolController toolController = new LabToolController(factory.getConveyor(),
                                                              factory.getPress(),
                                                              factory.getPaint(),
                                                              Press.PRESSING_MILLIS,
                                                              Painter.PAINTING_MILLIS);
        factory.startSimulation(toolController);
    }
}
