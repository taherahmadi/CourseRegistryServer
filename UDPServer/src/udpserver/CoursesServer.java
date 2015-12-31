package udpserver;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CoursesServer implements Runnable {
    static URL scheduleFileAddres;
    public CoursesServer(URL scheduleFileAddress) {
        this.scheduleFileAddres = scheduleFileAddress;
    }

    @Override
    public void run() {
        CourseServerSystem[] Systems = new CourseServerSystem[2];

        try {
            Systems[0] = new CourseServerSystem(4079 , "CourseServer1" , 1);
            Systems[1] = new CourseServerSystem(5079 , "CourseServer2" , 2);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (SocketException e) {
            e.printStackTrace();
        }
        try {
            Course c = new Course(scheduleFileAddres);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        Systems[0].start();
        Systems[1].start();
    }
    
}

class CourseServerSystem extends Thread {
    private int port  ;
    private Thread t;
    private String threadName;
    private int num;

    public CourseServerSystem(int port , String name , int num) throws SocketException, UnknownHostException
    {
        InetAddress IPAddress = InetAddress.getByName("localhost");
        this.port=port;
        threadName = name;
        this.num=num;
        System.out.println("Starting CourseServer"+num + " UDP Port:"+port + " IP Address:"+IPAddress);
    }

    public void run()
    {
        try {
            DatagramSocket serverSocket = new DatagramSocket(port);
            byte[] receiveData = new byte[64];
            byte[] sendData = new byte[64];

            while(true)
            {
                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                serverSocket.receive(receivePacket);
                String sentence = new String( receivePacket.getData());
                System.out.println("CourseServer"+num+" RECEIVED: " + sentence.replace("OPTIONS", "Request for "));
                InetAddress IPAddress = receivePacket.getAddress();
                int port = receivePacket.getPort();

                String COURSEINFO = new String();

                COURSEINFO = "Course"+num+":" + Course.getCourseName(num) + " " + Course.getCourseDays(num) + " " + Course.getCourseRoom(num);

                sendData = COURSEINFO.getBytes();
                DatagramPacket sendPacket =
                        new DatagramPacket(sendData, sendData.length, IPAddress, port);
                serverSocket.send(sendPacket);
            }
        }

        catch (SocketException ex) {
            Logger.getLogger(CourseServerSystem.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(CourseServerSystem.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public void start() {
        if (t == null)
        {
            t = new Thread (this, threadName);
            t.start ();
        }
    }

}

class Course {

    private static String name1 , name2;
    private static String days1 , days2;
    private static String room1 , room2;
    private static int capacity1 , capacity2;
    private static List<String> course1takers;
    private static List<String> course2takers;
    private URL scheduleFileAddres;

    public  Course(URL scheduleFileAddres) throws FileNotFoundException{

        this.scheduleFileAddres = scheduleFileAddres;
        Scanner fileScanner = new Scanner(new FileReader(scheduleFileAddres.getPath()));
        String data = fileScanner.nextLine();
        String[] parts = data.split(" ");
        name1 = parts[1];
        days1 = parts[2];
        room1 = parts[3];
        capacity1 = Integer.parseInt(parts[4]);
        data = fileScanner.nextLine();
        parts = data.split(" ");
        name2 = parts[1];
        days2 = parts[2];
        room2 = parts[3];
        capacity2 = Integer.parseInt(parts[4]);
        course1takers = new ArrayList<String>();
        course2takers = new ArrayList<String>();

        fileScanner.close();
    }

    public static String getCourseName(int i){
        if(i==1)
            return name1;
        else
            return name2;
    }
    public static String getCourseDays(int i){
        if(i==1)
            return days1;
        else
            return days2;
    }
    public static String getCourseRoom(int i){
        if(i==1)
            return room1;
        else
            return room2;
    }
}
