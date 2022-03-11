package hospital;

public class UserAccount {
    String name, division ;
    public UserAccount(String name, String division){
        this.name = name;
        this.division = division;
    }

    public String getName(){
        return name;
    }
    public String getDivision(){
        return division; 
    }
}
