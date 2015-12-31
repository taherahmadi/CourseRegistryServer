# Course Registry Client-Server

The Project main Packages:

1)TCP Server : regserver.java
    Listening on a specified Port ,
    Reading schedule File and Buffering it ,
    initializing related Courses
    and accept incoming connections then parse and process requests

2)TCP Client : students.java
    First read, parse and buffer strategy file ,
    initialize and run student client threads (student.java) according to strategy file ,
    then each client starts its attempts and submissions sending requests based on program logic.

**)UDP Server : CoursesServer.java (optional)
    initialize a datagram server socket in order to receive course data requests froms clients and send back'em
    required course information

______________________________________________________________________

for test just run TestCase.main(); from package TestCase :)

______________________________________________________________________
9231042 TaherAhmadi


Fall 2015
Network 1 Course Final project
Dr.Sadeghian
