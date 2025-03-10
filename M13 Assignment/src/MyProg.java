import java.util.LinkedList;
import java.util.Scanner;

/** MyProg.java
 *  Initiates the program which takes a file path from the user and analyzes the text with the help of two threads.
 *
 *  @author Beatriz Ceschin Antunes
 *  @version 1.0, Nov 23 2024
 *  @see java.util.Scanner,java.util.LinkedList
 */
public class MyProg {
    /**
     * Takes a file path from the user, instantiates and runs the producer and the consumer threads.
     * @param args
     */
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in); // for taking user input
        LinkedList<String> buffer = new LinkedList<>(); // queue that will be shared between the threads

        // ask for file path and save it
        System.out.println("Please enter the path to the file containing the text to be analyzed: ");
        String filePath = scanner.nextLine();

        // instantiate the threads
        Thread producerThread = new Thread(new ProducerThread(filePath, buffer));
        Thread consumerThread = new Thread(new ConsumerThread(buffer));

        // run the threads
        producerThread.start();
        consumerThread.start();
    }
}
