package testcase;

import tcpclient.students;
import tcpserver.regserver;
import udpserver.CoursesServer;

import java.io.IOException;
import java.net.URL;

public class testcase {

    public static void main(String[] args) throws IOException, InterruptedException {

        testcase testcase = new testcase();
        URL strategyFileAddress = testcase.getClass().getResource("strategy.txt");
        URL scheduleFileAddress = testcase.getClass().getResource("schedule.txt");

        if (scheduleFileAddress == null && strategyFileAddress == null){
            System.err.println("\nmake sure place strategy and schedule files in dir: " +
                    testcase.getClass().getResource(""));
            System.exit(0);
        }

        new Thread(new regserver(scheduleFileAddress)).start();
        new Thread(new CoursesServer(scheduleFileAddress)).start();
        new Thread(new students(strategyFileAddress)).start();

    }

}

