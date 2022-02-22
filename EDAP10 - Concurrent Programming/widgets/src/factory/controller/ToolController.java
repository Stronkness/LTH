package factory.controller;

import factory.model.WidgetKind;

/**
 * Interface for callbacks in the widget factory. Each factory has a
 * ToolController, responsible for taking action upon sensor input.
 *
 * Note that the factory is multi-threaded, and the ToolController methods
 * below may be invoked concurrently, in separate threads. 
 */
public interface ToolController {

    /**
     * Called by the factory whenever the sensor under the pressing device is
     * triggered.
     */
    void onPressSensorHigh(WidgetKind widgetKind) throws InterruptedException;

    /**
     * Called by the factory whenever the sensor under the painting device is
     * triggered.
     */
    void onPaintSensorHigh(WidgetKind widgetKind) throws InterruptedException;
}
