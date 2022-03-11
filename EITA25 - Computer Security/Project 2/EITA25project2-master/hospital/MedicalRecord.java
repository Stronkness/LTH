package hospital;
import java.util.ArrayList;

public class MedicalRecord {
    private String name;
    private ArrayList<String> doctor;
    private ArrayList<String> nurse;
    private String division;
    private ArrayList<String> log;

    public MedicalRecord(String name, String division, ArrayList<String> doctor, ArrayList<String> nurses, ArrayList<String> log){
        this.name = name;
        this.doctor = doctor;
        this.nurse = nurses;
        this.log = log; 
        this.division = division;
    }

    public String getName(){
        return name;
    }
    public String getDivision(){
        return division;
    }
    public ArrayList<String> getDoctor(){
        return doctor;
    }
    public ArrayList<String> getNurse(){
        return nurse;
    }
    public ArrayList<String> getLog() {
        return log;
    }
    public void changeName(String name){
        this.name = name;
    }
    public void changeDivision(String division){
        this.division = division;
    }
    public void addDoctor(String doctor){
        this.doctor.add(doctor);
    }
    public void addNurse(String nurse){
        this.nurse.add(nurse);
    }
    public void removeDoctor(String doctor){
        this.doctor.remove(doctor);
    }
    public void removeNurse(String nurse){
        this.nurse.remove(nurse);
    }
    public void addComment(String msg){
        this.log.add(msg);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(); 
        sb.append("Name: "+ this.name);
        sb.append(", Division: " + this.division);
        sb.append(", Doctor(s): ");
        for (String doctor : this.doctor) {
            sb.append(doctor + "/");
        }
        sb.deleteCharAt(sb.length()-1);
        sb.append(", Nurse(s): ");
        for (String nurse : this.nurse) {
            sb.append(nurse + "/");
        }
        sb.deleteCharAt(sb.length()-1);
        sb.append(", Logs: ");
        System.out.println("logen är såhär lång: " + log.size());
        for(String log : this.log) {
            sb.append(log + "-");
        }
        sb.deleteCharAt(sb.length()-1);
        return sb.toString();
    }
}
