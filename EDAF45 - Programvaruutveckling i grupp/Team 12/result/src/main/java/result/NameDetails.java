package result;

import java.util.*;

public class NameDetails {
    private List<String> columnNames;
    private Map<String,List<String>> classDetails;
    private Map<String,String> nameDetails;

    public NameDetails (){
        columnNames = new LinkedList<>();
        classDetails = new LinkedHashMap<>();
        nameDetails = new TreeMap<>();
    }

    public void addColumnNames(String column){
        Collections.addAll(columnNames, column.split("; "));
    }

    public void addClassName(String className){
        if(!classDetails.containsKey(className)){
            classDetails.put(className, new LinkedList<String>());
        }
    }

    public void addNameDetails(String startNbr, String name, String className){
        if(className.equals("STANDARDKLASS")){
            addClassName(className);
        }
        classDetails.get(className).add(startNbr);
        nameDetails.put(startNbr, name);
    }

    public List<String> getColumnNames(){
        return columnNames;
    }

    public List<DriverEntry> getDriverEntries() {
        List<DriverEntry> list = new LinkedList<DriverEntry>();

        for (Map.Entry<String, String> driver : nameDetails.entrySet()) {
            DriverEntry entry = new DriverEntry(driver.getKey(), driver.getValue());
            list.add(entry);
        }
        return list;
    }

    public List<String> getStartNumbers(){
        List<String> list = new LinkedList<>();
        list.addAll(nameDetails.keySet());
        return list;
    }

    public Set<Map.Entry<String,List<String>>> getClassDetailsSet(){
        return classDetails.entrySet();
    }
}
