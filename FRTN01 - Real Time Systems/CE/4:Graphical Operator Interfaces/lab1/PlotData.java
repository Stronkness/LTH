public class PlotData implements Cloneable {
	double ref, y;
	double x; // holds the current time 
	
	public Object clone() {
		try {
			return super.clone();
		} catch (Exception e) {
			return null;
		}
	}
}
