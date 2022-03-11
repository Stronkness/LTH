package hospital;

import java.io.*;
import java.math.BigInteger;
import java.net.*;
import javax.net.*;
import javax.net.ssl.*;

import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.ArrayList;

public class Server implements Runnable {
  private ServerSocket serverSocket = null;
  private static int numConnectedClients = 0;
  private static Logger logger = new Logger();
  private static DBController db = new DBController();
  
  public Server(ServerSocket ss) throws IOException {
    serverSocket = ss;
    newListener();
  }

  public void run() {
    try {
      SSLSocket socket=(SSLSocket)serverSocket.accept();
      newListener();
      SSLSession session = socket.getSession();
      Certificate[] cert = session.getPeerCertificates();
      String subject = ((X509Certificate) cert[0]).getSubjectX500Principal().getName();
      String issuer = ((X509Certificate) cert[0]).getIssuerX500Principal().getName();
      BigInteger serialNumb = ((X509Certificate) cert[0]).getSerialNumber();
      numConnectedClients++;
      System.out.println("client connected");
      System.out.println("client name (cert subject DN field): " + subject);
      System.out.println("Issuer of cert is :" + issuer);
      System.out.println("Serialnumber of cert is: " +  serialNumb);

      System.out.println(numConnectedClients + " concurrent connection(s)\n");

      PrintWriter out = null;
      BufferedReader in = null;
      out = new PrintWriter(socket.getOutputStream(), true);
      in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

      String name = subject.split("\\.")[1].split(",")[0].toLowerCase();
      String title =  subject.split("\\.")[0].split("=")[1].toLowerCase();
      

    
      if      (title.equalsIgnoreCase("doctor"))      doctorAccess(out, in, name);
      else if (title.equalsIgnoreCase("nurse"))       nurseAccess(out, in, name);
      else if (title.equalsIgnoreCase("patient"))     patientAccess(out, in, name);
      else if (title.equalsIgnoreCase("government"))  governmentAccess(out, in, name);
      else out.println("invalid role");
      
      out.flush();
      in.close();
      out.close();
      socket.close();
      numConnectedClients--;
      System.out.println("client disconnected");
      System.out.println(numConnectedClients + " concurrent connection(s)\n");
    } catch (Exception e) {
      System.out.println("Client died: " + e.getMessage());
      e.printStackTrace();
      return;
    }
  }
  
  private void nurseAccess(PrintWriter out, BufferedReader in, String name) throws IOException{
    for(;;){
    out.println("What do you want to do? (Read, Write, Exit)");
    out.flush();
    String command = in.readLine().toLowerCase().trim();
    switch(command){
      case "read":
        out.println("Which name do you want to read?");
        out.flush();
        String patientName = in.readLine().toLowerCase();
        if(divisionCheck(patientName, name)){
          readFile(patientName, out, in);
          logger.log(name, patientName, "read");
        }else if(associationCheck(patientName, name)){
          readFile(patientName, out, in);
          logger.log(name, patientName, "read");
        }
        else{
          out.println("Invalid access");
          out.flush();
        }
        break;
        case "write":
          out.println("Name of patient's journal to change");
          out.flush();
          String id2 = in.readLine();
          if(divisionCheck(id2, name)){
            writeFile(id2, out, in);
            logger.log(name, id2, "write");;
          }else if(associationCheck(id2, name)){
            writeFile(id2, out, in);
            logger.log(name, id2, "write");
          }else{
            out.println("Invalid access");
            out.flush();
          }
          break;
        case "exit":
          return;

        default:
          out.println("Invalid command");
          out.flush();
        }
    }
  }

  private void governmentAccess(PrintWriter out, BufferedReader in, String name) throws IOException{
    for(;;){
    out.println("What do you want to do? (Read, Delete, Exit)");
    out.flush();
    String command = in.readLine().toLowerCase().trim();
    switch(command){
      case "read":
        out.println("Which name do you want to read?");
        out.flush();
        String id = in.readLine().toLowerCase();
        readFile(id, out, in);
        logger.log(name, id, "read");
        break;

      case "delete":
        out.println("Which name do you want to delete?");
        out.flush();
        String id8 = in.readLine().toLowerCase();
        logger.log(name, id8, "delete");
        deleteFile(id8, out);
        break;
      case "exit":
        return;
      default:
        out.println("Invalid command");
        out.flush();
    }
  }
  }


private void doctorAccess(PrintWriter out, BufferedReader in, String name) throws IOException {
  for (;;) {
    out.println("What do you want to do? (Read, Write, Create, Exit)");
    out.flush();
    String command = in.readLine().toLowerCase().trim();
   
    switch(command){
      case "create":
        out.println("Write the information in the format: name;nurses;first_log (separate nurses with slash)");
        out.flush();
        String[] msg = in.readLine().toLowerCase().split(";");  
        if(msg.length != 3){
          out.println("Wrong format");
          out.flush();
        } else{

        db.addJournal(msg[0], db.getUserAccount(name).getDivision(), name, msg[1], msg[2]);
        out.println("Added to DB");
        out.flush();
        logger.log(name, msg[0], "create");
        }
        break;
      case "read":
        out.println("Which Name do you want to read?");
        out.flush();

        String id = in.readLine().toLowerCase();
        if(divisionCheck(id, name)){
          readFile(id, out, in);
          logger.log(name, id, "read");
        }else if(associationCheck(id, name)){
          readFile(id, out, in);
          logger.log(name, id, "read");
        }else{
          out.println("Invalid access");
          out.flush();
        }
        break;
      case "write":
        out.println("Name of patient's journal to change");
        out.flush();
        String id2 = in.readLine().toLowerCase();
        if(divisionCheck(id2, name)){
          writeFile(id2, out, in);
          logger.log(name, id2, "write");;
        }else if(associationCheck(id2, name)){
          writeFile(id2, out, in);
          logger.log(name, id2, "write");
        }else{
          out.println("Invalid access");
          out.flush();
        }
        break;
      case "exit":
        return;
      default:
        out.println("Non-valid command");
        out.flush();
      }
    }
  }

  private void patientAccess(PrintWriter out, BufferedReader in, String patientName) throws IOException{
      
    for (;;) {
      out.println("Do you want to show your files or exit? (Show, exit)");
      out.flush();
      String command = in.readLine().toLowerCase().trim();
      if (command.trim().equalsIgnoreCase("show")) {
        if(db.getMedicalRecord(patientName) == null){
          out.println("No files with your name");
          out.flush();
          return;
        }
        out.println("Your patient records:");
        out.flush();
        readFile(patientName, out, in);
        
        logger.log(patientName, patientName, "read");
      }
      else if(command.trim().equalsIgnoreCase("exit"))
        return;
    }
  }
  private boolean divisionCheck(String patientName, String workerName){
    System.out.println("Test");
    ArrayList<MedicalRecord> medTemp = db.getMedicalRecord(patientName);
    UserAccount workTemp = db.getUserAccount(workerName);
    
    if(medTemp != null && workTemp != null){
    return medTemp.get(0).getDivision().equals(workTemp.getDivision());
    }else{
      return false; 
    }
  }

  private boolean associationCheck(String patientName, String workerName) {
    ArrayList<MedicalRecord> medTemp = db.getMedicalRecord(patientName);
    if(medTemp != null){
      return medTemp.get(0).getDoctor().contains(workerName) || medTemp.get(0).getNurse().contains(workerName); 
    }else{
      return false; 
    }
  }

  private void writeFile(String patientName, PrintWriter out, BufferedReader in) throws IOException {
    ArrayList<MedicalRecord> records = db.getMedicalRecord(patientName);
    if (records != null) {     
      while(true) {
        out.println(records.get(0).toString());
        out.flush();
        out.println("Do you want to add record to patient? (y/n)");
        out.flush();
        
        String logMsg = in.readLine().toLowerCase();
        if(logMsg.equalsIgnoreCase("y")) {
          out.println("Write log:> ");
          boolean succesfullWrite = db.writeLog(patientName, in.readLine());
          if(succesfullWrite){
            out.println("Added log input");
            out.flush();
            break;
          }else{
            out.println("File does not exist");
            out.flush();
            break;
          }
        }
        else {
          break;
        }
      }
    }

  }

  private void readFile(String patientName, PrintWriter out, BufferedReader in){
    ArrayList<MedicalRecord> tmpList = db.getMedicalRecord(patientName);
    if(tmpList != null){
      out.println(tmpList.get(0).toString());
    } else {
      out.println("No files with this name exists.");
    }
  }

  private void deleteFile(String patientName, PrintWriter out) {
    db.deleteEntryForPatient(patientName);
    out.println("Deletion successful");
    out.flush();
  }

  private void newListener() { (new Thread(this)).start(); } // calls run()
  public static void main(String args[]) {
    System.out.println("\nServer Started\n");
    int port = -1;
    if (args.length >= 1) {
      port = Integer.parseInt(args[0]);
    }
    String type = "TLSv1.2";
    try {
      ServerSocketFactory ssf = getServerSocketFactory(type);
      ServerSocket ss = ssf.createServerSocket(port);
      ((SSLServerSocket)ss).setNeedClientAuth(true); // enables client authentication
      new Server(ss);
    } catch (IOException e) {
      System.out.println("Unable to start Server: " + e.getMessage());
      e.printStackTrace();
    }
  }

  private static ServerSocketFactory getServerSocketFactory(String type) {
    if (type.equals("TLSv1.2")) {
      SSLServerSocketFactory ssf = null;
      try { // set up key manager to perform server authentication
        SSLContext ctx = SSLContext.getInstance("TLSv1.2");
        KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
        TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
        KeyStore ks = KeyStore.getInstance("JKS");
        KeyStore ts = KeyStore.getInstance("JKS");
        char[] password = "password".toCharArray();
        // keystore password (storepass)
        ks.load(new FileInputStream("keystores/serverkeystore"), password);  
        // truststore password (storepass)
        ts.load(new FileInputStream("truststores/servertruststore"), password); 
        kmf.init(ks, password); // certificate password (keypass)
        tmf.init(ts);  // possible to use keystore as truststore here
        ctx.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);
        ssf = ctx.getServerSocketFactory();
        return ssf;
      } catch (Exception e) {
        e.printStackTrace();
      }
    } else {
      return ServerSocketFactory.getDefault();
    }
    return null;
  }
}
