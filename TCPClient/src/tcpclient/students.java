package tcpclient;
import java.io.*;
import java.net.URL;
import java.util.Scanner;

public class students implements Runnable{

    URL strategyFileAddress;

    public students(URL strategyFileAddress) {
        this.strategyFileAddress = strategyFileAddress;
    }

    @Override
    public void run() {

        Student[] student = new Student[4];
        Scanner fileScanner = null;
        try {
            fileScanner = new Scanner(new FileReader(strategyFileAddress.getPath()));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        for(int i = 0 ; i < 4 ; i ++)
        {
            String data = fileScanner.nextLine();
            String[] parts = data.split(" ");
            student[i] = new Student(parts[0] , Double.parseDouble(parts[1]),
            Double.parseDouble(parts[2]),Integer.parseInt(parts[3]),Double.parseDouble(parts[4]));

        }
        fileScanner.close();


    for(int i = 0 ; i < 4 ; i ++)
    {
        System.out.println(student[i].getStudentName() +
                " TCP Port: " + student[i].regServerPort+
                " IP Address : " + student[i].regServerIP );
        System.out.println("Course1 choose prob : " +
                student[i].getStudentProbability() +
                "   Course2 choose prob :" +
                (float)(1-student[i].getStudentProbability())+"\n");

    }
        student[0].start();
        student[1].start();
        student[2].start();
        student[3].start();
    }
    
}


