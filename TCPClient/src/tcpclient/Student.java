package tcpclient;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;


public class Student extends Thread  {
    private Thread t;
    private String name;
    private double probability;
    private double begin;
    private int attempts;
    boolean hasCourse1 , hasCourse2;
    private double RTT;
    public String regServerIP;
    public int regServerPort;
    private String threadName;
    Socket clientSocket;
    String[] CS;
    
    
    public Student(String name , double probability , double begin , int attempts , double RTT)
    {
        this.name=name;
        this.probability=probability;
        this.begin = begin*1000;
        this.attempts=attempts;
        this.RTT=RTT*1000; 
        
        regServerIP = "127.0.0.1";
        regServerPort = 2079;
        threadName = name;
       
    }

    
    public void run(){
       
        try {
            String message;
            String reply;

            clientSocket = new Socket(regServerIP , regServerPort);
            clientSocket.setKeepAlive(true);
            DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
            BufferedReader inFromServer =new BufferedReader(new InputStreamReader( clientSocket.getInputStream()));
            double random;

            message = getStudentName() + " " + "HELLO" + " " + "OPTIONS" + " " + "PAYLOAD";
            outToServer.writeBytes(message + "\n");             
            reply = inFromServer.readLine();
            String[] parts = reply.split(" ");
            if(parts[1].equals("WELCOME"))
                System.out.println(getStudentName() + " is connected to server");
            else
                System.out.println(getStudentName() + " is failed to connect the server");
            Thread.sleep((long) (getStudentBegin()));           

            for(int i = 0 ; i  < getStudentAttempts() ; i++)
            {
 
                random=Math.random();
                
                if(random<=getStudentProbability())
                {
                    if(hasCourse1==true)
                    {
                        message = getStudentName() + " " + "DROP" + " " + "OPTIONS" + " "+ "COURSE1" ;
                        System.err.println(getStudentName() + " Requested to drop course1");
                    }    
                    else
                    {    
                        message = getStudentName() + " " + "ADD" + " " + "OPTIONS" + " " + "COURSE1";
                       System.err.println(getStudentName() + " Requested to add course1");
                    }    
                    outToServer.writeBytes(message + "\n");
                    reply = inFromServer.readLine();
                    parts = reply.split(" ");  
                    
                    if(message.contains("ADD")&&parts[1].equals("ACK"))
                    {
                        hasCourse1=true;
                        System.err.println(getStudentName()+" has added the course1");
                    }
                    if(message.contains("ADD")&&parts[1].equals("NACK"))
                        System.err.println(getStudentName()+" has failed to add the course1");
                    if(message.contains("DROP")&&parts[1].equals("ACK"))
                    {
                        hasCourse1=false;
                        System.err.println(getStudentName()+" has dropped the course1");
                    }
                    
                    
                }
                else
                {
                    if(hasCourse2==true)
                    {    
                        message = getStudentName() + " " + "DROP" + " " + "OPTIONS" + " "+ "COURSE2" ;
                        System.err.println(getStudentName() + " Requested to drop course2");
                    }    
                    else
                    {    
                        message = getStudentName() + " " + "ADD" + " " + "OPTIONS" + " " + "COURSE2";
                        System.err.println(getStudentName() + " Requested to add course2");
                    }    
                    outToServer.writeBytes(message + "\n");
                    reply = inFromServer.readLine();
                    parts = reply.split(" "); 
                    if(message.contains("ADD")&&parts[1].equals("ACK"))
                    {
                        hasCourse2=true;
                        System.err.println(getStudentName()+" has added the course2");
                    }
                    if(message.contains("ADD")&&parts[1].equals("NACK"))
                       System.err.println(getStudentName()+" has failed to add the course2");
                    if(message.contains("DROP")&&parts[1].equals("ACK"))
                    {
                        hasCourse2=false;
                         System.err.println(getStudentName()+" has dropped the course2");
                    }                    
                }  
                
            Thread.sleep((long) getStudentRTT());    
            }

            message = getStudentName() + " " + "SUBMIT" + " " + "OPTIONS" + " " + "PAYLOAD";
            outToServer.writeBytes(message + "\n");
               reply = inFromServer.readLine();
               parts = reply.split(" ");
               String takenCourses = new String(parts[2]);
               if(!parts[3].isEmpty())
               CS=parts[3].split(",");
               if(CS.length==2)
               System.err.println(getStudentName()+ " " + "PORTS " + takenCourses+ " " + CS[0]+" " + CS[1] );
               if((CS.length==1)&&!(CS[0].equals("")))
               System.err.println(getStudentName()+ " " + "PORTS " + takenCourses+ " " + CS[0] +" " );
               if(parts[3].isEmpty())
               System.err.println(getStudentName()+ " has failed to get any courses");
           clientSocket.close();
           
            
        } catch (IOException ex) {
            Logger.getLogger(Student.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InterruptedException ex) {
            Logger.getLogger(Student.class.getName()).log(Level.SEVERE, null, ex);
        }

        {
            try {
              
                DatagramSocket clientSocket = new DatagramSocket();
                InetAddress IPAddress = InetAddress.getByName("localhost");
                byte[] sendData = new byte[64];
                byte[] receiveData = new byte[64];
                String sentence1 = getStudentName() + " " +"INFO" + " " + "OPTIONS" + "COURSE1";
                String sentence2 = getStudentName() + " " +"INFO" + " " + "OPTIONS" + "COURSE2";    
                DatagramPacket sendPacket;
                if(hasCourse1==true)
                {
                sendData = sentence1.getBytes();
                sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, Integer.parseInt(CS[0]));
                clientSocket.send(sendPacket);
                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                clientSocket.receive(receivePacket);
                String COURSE1INFO = new String(receivePacket.getData());
                System.out.println(getStudentName() + " " + COURSE1INFO);
                }
                if(hasCourse2==true&&hasCourse1==false)
                {
                sendData = sentence2.getBytes();
                sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, Integer.parseInt(CS[0]));
                clientSocket.send(sendPacket);
                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                clientSocket.receive(receivePacket);
                String COURSE2INFO = new String(receivePacket.getData());
                System.out.println(getStudentName() + " " + COURSE2INFO);            
                }
                if(hasCourse2==true&&hasCourse1==true)
                {
                sendData = sentence2.getBytes();
                sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, Integer.parseInt(CS[0]));
                clientSocket.send(sendPacket);
                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                clientSocket.receive(receivePacket);
                String COURSE2INFO = new String(receivePacket.getData());
                System.out.println(getStudentName() + " " + COURSE2INFO);            
                }                
                clientSocket.close();
            }
            catch (SocketException ex) {
                Logger.getLogger(Student.class.getName()).log(Level.SEVERE, null, ex);
            } catch (UnknownHostException ex) {
                Logger.getLogger(Student.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(Student.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        System.out.println(getStudentName() + " has done the submission");
        
    }
    public String getStudentName()
    {
        return name;
    }
    public double getStudentProbability()
    {
        return probability;
    }
    public double getStudentRTT()
    {
        return RTT;
    }
    public double getStudentBegin()
    {
        return begin;
    }
    public int getStudentAttempts()
    {
        return attempts;
    }

    public void start(){
      if (t == null)
      {
         t = new Thread (this, threadName);
         t.start ();
      }
   }
    
}
