package tcpserver;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class regserver implements Runnable{
    URL scheduleFileAddress;

    public regserver(URL scheduleFileAddress) {
    this.scheduleFileAddress = scheduleFileAddress;
    }

    @Override
    public void run() {
        System.out.println("RegServer Listening on Port: 2079");
        Socket socket = null;
        ServerSocket welcomeSocket = null;
        try {
            Course c = new Course(scheduleFileAddress);
            welcomeSocket = new ServerSocket(2079);
        } catch (IOException e) {
            e.printStackTrace();
        }

        while (true) {
            try {
                socket = welcomeSocket.accept();
            } catch (IOException e) {
                System.out.println("I/O error: " + e);
            }
            new ServerThread(socket).start();
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



    public  Course(URL scheduleFileAddress) throws FileNotFoundException{


        Scanner fileScanner = new Scanner(new FileReader(scheduleFileAddress.getPath()));

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


    public static String getCourse1Name(){
        return name1;
    }

    public static String getCourse1Days(){
        return days1;
    }

    public static String getCourse1Room(){
        return room1;
    }

    synchronized public static int getCourse1Capacity(){
        return capacity1;
    }

    synchronized public static void addCourse1Capacity(){
        capacity1++;
    }

    synchronized public static void takeCourse1Capacity(){
        capacity1--;
    }

    public static String getCourse2Name(){
        return name2;
    }

    public static String getCourse2Days(){
        return days2;
    }

    public static String getCourse2Room(){
        return room2;
    }

    synchronized public static int getCourse2Capacity(){
        return capacity2;
    }

    synchronized public static void addCourse2Capacity(){
        capacity2++;
    }

    synchronized public static void takeCourse2Capacity(){
        capacity2--;
    }

    synchronized public static void addCourse1Takers(String s)
    {
        course1takers.add(s);
    }


    synchronized public static void addCourse2Takers(String s)
    {
        course2takers.add(s);
    }

    synchronized public static void deleteCourse1Takers(String s)
    {
        for(int i = 0 ; i < course1takers.size() ; i++)
            if(course1takers.get(i).equals(s))
                course1takers.remove(i);
    }


    synchronized public static void deleteCourse2Takers(String s)
    {
        for(int i = 0 ; i < course2takers.size() ; i++)
            if(course2takers.get(i).equals(s))
                course2takers.remove(i);
    }

    public static List getCourseTakersList(int i)
    {
        if (i==1)
            return course1takers;
        else
            return course2takers;
    }

}

class ServerThread extends Thread {
    protected Socket socket;
    PrintWriter outputter ;


    public ServerThread(Socket clientSocket) {
        this.socket = clientSocket;
    }


    public void run() {


        InputStream inputStream = null;
        BufferedReader bufferedReader = null;
        DataOutputStream outputStream = null;
        String serverName = new String("RegistrationServer");
        String message = new String();
        String reply = new String();
        String part1 = new String();
        String part2 = new String();
        String part3 = new String();
        String part4 = new String();

        try {
            inputStream = socket.getInputStream();
            bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            outputStream = new DataOutputStream(socket.getOutputStream());
            outputter = new PrintWriter(socket.getOutputStream(),true);
        } catch (IOException e) {
            return;
        }

        String line;
        while (true) {
            try {
                line = bufferedReader.readLine();
                String[] parts = line.split(" ");
                if (parts[1].equals("SUBMIT")) {

                    String options = new String("");
                    String ports = new String("");
                    for(int i = 0 ; i < Course.getCourseTakersList(1).size() ; i++)
                    {
                        if (Course.getCourseTakersList(1).get(i).equals(parts[0]))
                        {
                            options+=("Course1");
                            ports+=("4079,");
                        }
                    }
                    for(int i = 0 ; i < Course.getCourseTakersList(2).size() ; i++)
                    {
                        if (Course.getCourseTakersList(2).get(i).equals(parts[0]))
                        {
                            options+=("Course2");
                            ports+=("5079");
                        }

                    }
                    if(options.equals(""))
                    {
                        options+="FailedToGetAnyCourses";
                        ports+="Null";
                    }
                    System.out.println(parts[0]+" submitted the courses : " +options);
                    outputter.println(serverName+ " " + "PORT" + " " + options  + " " + ports);
                    socket.close();

                    return;
                }
                else if(parts[1].equals("HELLO")){
                    outputter.println(serverName+ " WELCOME" + " OPTIONS" + " PAYLOAD");
                    System.out.println(parts[0]+" is online");
                    outputStream.flush();
                }
                else if(parts[1].equals("ADD"))
                {
                    if(parts[3].equals("COURSE1")){
                        System.out.println(parts[0]+" is trying to add Course1");
                        if(Course.getCourse1Capacity()==0)
                        {
                            outputter.println(serverName +" " + "NACK" + " " + "OPTIONS"+ " "+ "PAYLOAD");
                            System.out.println(  "Course1 cannot be added for " + parts[0]);
                        }
                        if(Course.getCourse1Capacity()!=0)
                        {
                            outputter.println(serverName +" " + "ACK" + " " + "OPTIONS"+ " "+ "PAYLOAD");
                            System.out.println("Course1 is added for " + parts[0]);
                            Course.takeCourse1Capacity();
                            Course.addCourse1Takers(parts[0]);
                        }
                    }
                    if(parts[3].equals("COURSE2")){
                        System.out.println(parts[0]+" is trying to add Course2");
                        if(Course.getCourse2Capacity()==0)
                        {
                            outputter.println(serverName +" " + "NACK" + " " + "OPTIONS"+ " "+ "PAYLOAD");
                            System.out.println("Course2 cannot be added for " + parts[0]);
                        }
                        if(Course.getCourse2Capacity()!=0)
                        {
                            outputter.println(serverName +" " + "ACK" + " " + "OPTIONS"+ " "+ "PAYLOAD");
                            System.out.println("Course2 is added for " + parts[0]);
                            Course.takeCourse2Capacity();
                            Course.addCourse2Takers(parts[0]);
                        }
                    }
                }
                else if(parts[1].equals("DROP"))
                {
                    if(parts[3].equals("COURSE1")){
                        System.out.println(parts[0]+" has droped the Course1");
                        outputter.println(serverName + " " + "ACK" + " " + "OPTIONS" + " " + "PAYLOAD");
                        Course.addCourse1Capacity();
                        Course.deleteCourse1Takers(parts[0]);
                    }
                    if(parts[3].equals("COURSE2")){
                        System.out.println(parts[0]+" has droped the Course2");
                        outputter.println(serverName + " " + "ACK" + " " + "OPTIONS" + " " + "PAYLOAD");
                        Course.addCourse2Capacity();
                        Course.deleteCourse2Takers(parts[0]);
                    }

                }
            }catch (IOException e) {
                e.printStackTrace();
                return;
            }

        }


    }

}
