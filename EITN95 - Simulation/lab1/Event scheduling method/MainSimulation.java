import java.util.*;
import java.io.*;


public class MainSimulation extends GlobalSimulation{
 
    public static void main(String[] args) throws IOException {
    	Event actEvent;
    	State actState = new State(); // The state that shoud be used
    	// Some events must be put in the event list at the beginning
        insertEvent(ARRIVAL, 0);  
        insertEvent(MEASURE, 5);
        
        // The main simulation loop
		int simulation_no = 1;
		for(int i = 0; i <= 10; i++){
			while (time < 5000){
				actEvent = eventList.fetchEvent();
				time = actEvent.eventTime;
				actState.treatEvent(actEvent);
			}
			System.out.println("Simulation no. " + simulation_no + ", " + 1.0*actState.accumulated/actState.noMeasurements);
			simulation_no++;
		}
    	
    	// Printing the result of the simulation, in this case a mean value
    	System.out.println(1.0*actState.accumulated/actState.noMeasurements);
    }
}