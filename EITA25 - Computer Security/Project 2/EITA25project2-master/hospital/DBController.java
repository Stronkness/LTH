package hospital;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;

public class DBController {
    String medicalRecordsPath = "./medical_records/Database.txt";
    String userAccountsPath= "./UserAccounts/UserAccounts.txt";
    Map<String, ArrayList<MedicalRecord>> db; 
    Map<String, UserAccount> userDB; 

    public DBController(){
        db = readAllRecords();
        userDB = readAllUserAccounts();
    }

    
    public Map<String, ArrayList<MedicalRecord>> readAllRecords() {
        Map<String, ArrayList<MedicalRecord>> records = new HashMap<String, ArrayList<MedicalRecord>>();
        try{
            File file = new File(medicalRecordsPath);
            Scanner sc = new Scanner(file);
            sc.nextLine();
            while(sc.hasNextLine()){
                String[] line = sc.nextLine().split("; ");   //läser in rader från Database.txt
                String temp = line[2]; //doctors
                String temp2 = line[3]; //nurses
                String temp3 = line[4]; //logs
                ArrayList<String> doctors = new ArrayList<String>(Arrays.asList(temp.split("/"))); // om det finns flera nurses/doctors
                ArrayList<String> nurses = new ArrayList<String>(Arrays.asList(temp2.split("/")));
                ArrayList<String> logs = new ArrayList<String>(Arrays.asList(temp3.split(":")));
                if(!records.containsKey(line[0])){
                    records.put(line[0], new ArrayList<MedicalRecord>()); //lägger in i mapen, [identifier -> line]
                }
                records.get(line[0]).add(new MedicalRecord(line[0], line[1], doctors, nurses, logs));
                db = records;
            }
            sc.close();
        } catch(FileNotFoundException e){
            System.out.println(e.getMessage());
        }
        return records;
    }  

    public Map<String, UserAccount> readAllUserAccounts(){
        Map<String, UserAccount> records = new HashMap<>();
        try{
            File file = new File(userAccountsPath);
            Scanner sc = new Scanner(file);
            sc.nextLine();
            while(sc.hasNextLine()){
                String[] line = sc.nextLine().split("; ");   //läser in rader från Database.txt
                if(!records.containsKey(line[0])){
                    records.put(line[0], new UserAccount(line[0], line[1])); //lägger in i mapen, [identifier -> line]
                }
            }
            sc.close();
        } catch(FileNotFoundException e){
            System.out.println(e.getMessage());
        }
        return records;
    }

    public void addJournal(String name, String division, String doctors, String nurses, String logs) {
        if (!db.containsKey(name)) {
            try {
                FileWriter fw = new FileWriter(medicalRecordsPath, true);
                StringBuilder sb = new StringBuilder();
                sb.append(name + "; " + division + "; " + doctors + "; " + nurses + "; " + logs + "\n");
                fw.append(sb.toString());
                fw.close();
                ArrayList<String> doctorss = new ArrayList<String>(Arrays.asList(doctors.split("/"))); // om det finns flera nurses/doctors
                ArrayList<String> nursess = new ArrayList<String>(Arrays.asList(nurses.split("/")));
                ArrayList<String> logss = new ArrayList<String>(Arrays.asList(logs.split(":")));
                
                db.put(name, new ArrayList<>(Arrays.asList(new MedicalRecord(name, division, doctorss, nursess, logss))));

            } catch (Exception e) {
                throw new Error(e);
            }
        }
    }

    public void deleteEntryForPatient(String name) {
        try {
            //hämtar alla records och tar bort alla för en person
            Map<String, ArrayList<MedicalRecord>> records = readAllRecords();
            records.remove(name);
            this.db = records;
            writeAll(records);            
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println("Deletion failed");
        }
    }

    public boolean writeLog(String name, String log) {
        Map<String, ArrayList<MedicalRecord>> records = readAllRecords();
        if(records.containsKey(name)){
            MedicalRecord record = records.get(name).get(0);
            record.addComment(log);
            writeAll(records);
            this.db = records;
            return true;
        } 
        return false; 
    }

    private void writeAll(Map<String, ArrayList<MedicalRecord>> records) {
        try {
            FileWriter fw = new FileWriter(medicalRecordsPath);
                fw.append("Name; Division; Doctor(s); Nurse(s); Logs\n");
                for (String i : records.keySet()){
                    List<MedicalRecord> patientRecords = records.get(i);
                    //för alla records per person 
                    patientRecords.forEach(record -> {
                        StringBuilder sb = new StringBuilder();
                        sb.append(i + "; " +
                                    record.getDivision().toLowerCase() + "; ");
                        record.getDoctor().forEach(doctor -> {
                            sb.append(doctor.toLowerCase() + "-");
                        });
                        sb.deleteCharAt(sb.length() - 1);
                        sb.append("; ");
                        record.getNurse().forEach(nurse -> {
                            sb.append(nurse.toLowerCase() + "-");
                        });
                        sb.deleteCharAt(sb.length() - 1);
                        sb.append("; ");
                        record.getLog().forEach(log -> {
                            sb.append(log + ":");
                        });
                        try {
                            fw.append(sb.toString() + "\n");
                        } catch (IOException e) {
                            System.out.println(e.getMessage());
                        }
                    });  
                }
                fw.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public ArrayList<MedicalRecord> getMedicalRecord(String name){
        return db.get(name);
    }

    public UserAccount getUserAccount(String name){
        return userDB.get(name);
    }
    
    public static void main(String[] args) {
        DBController db = new DBController();
        Map<String, UserAccount> usermap = db.readAllUserAccounts();
        // db.addEntry("05", "lars", "hjärta", "emil-göran", "gösta", "Sjuk-Mer sjuk-väldigt sjuk");
        for(String key: usermap.keySet()){
            System.out.println(key);
        }
        //db.deleteEntryForPatient("05");
    }
}