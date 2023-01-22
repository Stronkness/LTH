public class Screen {
    public synchronized void writePeriod(int p){
        try {
            System.out.println(p);
            System.out.println(", ");
        } catch (Exception e) {
            
        }
    }
}
