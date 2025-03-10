import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;

/** ProducerThread.java
 *  Thread that opens file connection, reads words and puts them onto the queue.
 *
 *  @author Beatriz Ceschin Antunes
 *  @version 1.0, Nov 23 2024
 *  @see java.io.FileReader,java.io.IOException,java.util.LinkedList
 */
public class ProducerThread implements Runnable {
    private LinkedList<String> buffer; // queue of words
    private String filePath; // file path entered by user
    public static boolean isProducing = true; // indicates if still reading from file and adding to buffer

    /**
     * Constructor that initializes the queue and file path variables
     * @param filePath path entered by user in main
     * @param buffer queue created in main
     */
    public ProducerThread(String filePath, LinkedList<String> buffer) {
        // set values for buffer and file path
        this.buffer = buffer;
        this.filePath = filePath;
    }

    /**
     * Works in synchronized manner with consumer thread to put words into the buffer.
     */
    @Override
    public void run() {
        // read file provided by user, display any exceptions
        try (FileReader fr = new FileReader(filePath)) {
            StringBuilder word = new StringBuilder(); // to create string before adding to buffer
            int c;
            while ((c = fr.read()) != -1) {
                char ch = (char) c;
                // add to string letters and digits
                if (Character.isLetterOrDigit(ch)) {
                    word.append(Character.toLowerCase(ch));
                }
                // if the character found is a punctuation, not added to string
                else {
                    if (word.length() > 0) {
                        synchronized (buffer) {
                            buffer.add(word.toString()); // add to queue
                            buffer.notifyAll(); // notify consumer
                        }
                        word.setLength(0); // clear string builder for next word
                    }
                }
            }
            // handle the last word
            if (word.length() > 0) {
                synchronized (buffer) {
                    buffer.add(word.toString()); // add to queue
                    buffer.notifyAll(); // notify consumer
                }
            }
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage()); // display message if exceptions happen when reading file
        } finally {
            synchronized (buffer) {
                isProducing = false; // reading is complete
                buffer.notifyAll(); // notify consumer
            }
        }
    }
}
