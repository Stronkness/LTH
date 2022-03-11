package hospital;


//import java.net.*;
import java.io.*;
import java.math.BigInteger;

import javax.net.ssl.*;
import java.security.cert.X509Certificate;
import java.security.KeyStore;
import java.security.cert.*;

/*
 * This example shows how to set up a key manager to perform client
 * authentication.
 *
 * This program assumes that the client is not inside a firewall.
 * The application can be modified to connect to a server outside
 * the firewall by following SSLSocketClientWithTunneling.java.
 */

public class Client {
  public static void main(String[] args) throws Exception {
    String host = null;
    int port = -1;
    for (int i = 0; i < args.length; i++) {
      System.out.println("args[" + i + "] = " + args[i]);
    }
    if (args.length < 2) {
      System.out.println("USAGE: java client host port");
      System.exit(-1);
    }
    try { /* get input parameters */
      host = args[0];
      port = Integer.parseInt(args[1]);
    } catch (IllegalArgumentException e) {
      System.out.println("USAGE: java client host port");
      System.exit(-1);
    }

    try {
      SSLSocketFactory factory = null;
      try {
        BufferedReader read = new BufferedReader(new InputStreamReader(System.in));
        //char[] password = "password".toCharArray();
        KeyStore ks = KeyStore.getInstance("JKS");
        KeyStore ts = KeyStore.getInstance("JKS");
        KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
        TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
        SSLContext ctx = SSLContext.getInstance("TLSv1.2");
        // keystore password (storepass)
        System.out.println("What is your keystore called?");
        String name = read.readLine();
        System.out.println("Password");
        char[] pass = read.readLine().toCharArray();
        ks.load(new FileInputStream("keystores/" + name +"keystore"), pass);  
        // truststore password (storepass);
        ts.load(new FileInputStream("truststores/" + name + "truststore"), pass); 
        kmf.init(ks, pass); // user password (keypass)
        tmf.init(ts); // keystore can be used as truststore here
        ctx.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);
        factory = ctx.getSocketFactory();
      } catch (Exception e) {
        throw new IOException(e.getMessage());
      }
      SSLSocket socket = (SSLSocket)factory.createSocket(host, port);
      System.out.println("\nsocket before handshake:\n" + socket + "\n");

      /*
       * send http request
       *
       * See SSLSocketClient.java for more information about why
       * there is a forced handshake here when using PrintWriters.
       */

      socket.startHandshake();
      SSLSession session = socket.getSession();
      Certificate[] cert = session.getPeerCertificates();
      String subject = ((X509Certificate) cert[0]).getSubjectX500Principal().getName();
      String issuer = ((X509Certificate) cert[0]).getIssuerX500Principal().getName();
      BigInteger serialNumb = ((X509Certificate) cert[0]).getSerialNumber();
      System.out.println("certificate name (subject DN field) on certificate received from server:\n" + subject + "\n");
      System.out.println("Issuer of cert is : " + issuer);
      System.out.println("Serialnumber of cert is: " +  serialNumb);
      System.out.println("socket after handshake:\n" + socket + "\n");

      System.out.println("secure connection established\n\n");

      BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
      PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
      BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
      String incomingMsg = ""; 
      while(true) {

        //getIncomingMsg();

        System.out.print(">");
        
        incomingMsg = in.readLine();
        // incomingMsg = getIncomingMsg(in, out);
        // if (incomingMsg != "") {
        //   System.out.println(incomingMsg);
        // }
        
        if (incomingMsg == null) {
          break;
        }
        System.out.println(incomingMsg);
        if(!in.ready()){
          if(sendMessageToServer(reader, out).equalsIgnoreCase("exit"))
            break;
        }
      } 
      System.out.println("logging out... bye :)");
      in.close();
      out.close();
      reader.close();
      socket.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private static String getIncomingMsg(BufferedReader in, PrintWriter out) {
    StringBuilder sb = new StringBuilder();
    try {
      while(in.ready()) {
        sb.append(in.readLine());
      }
    } catch (IOException e) {
      System.out.println("u fuked up");
    }
    return sb.toString();
  }

  private static String sendMessageToServer(BufferedReader reader, PrintWriter out) throws IOException{
    System.out.print(">");
    String msg; 
    msg = reader.readLine();
    if(!msg.equals("")) { 
      out.println(msg);
      out.flush();
      return msg;
    }
    return "";
  }
}
