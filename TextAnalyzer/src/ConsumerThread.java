import java.util.ArrayList;
import java.util.LinkedList;

/** ConsumerThread.java
 *  Thread that takes words off the queue and keeps a tally. Then prints out the most frequent word in the file.
 *
 *  @author Beatriz Ceschin Antunes
 *  @version 1.0, Nov 23 2024
 *  @see java.util.ArrayList,java.util.LinkedList
 */
public class ConsumerThread implements Runnable {
    private LinkedList<String> buffer; // queue of words
    private final ArrayList<String> words; // array used for the tally, to store the words
    private final ArrayList<Integer> counts; // array used for the tally, to store how many times each word appears

    /**
     * Constructor that initializes the queue and array.
     * @param buffer queue shared between this and producer thread
     */
    public ConsumerThread(LinkedList<String> buffer) {
        // set values for buffer and initialize arrays
        this.buffer = buffer;
        this.words = new ArrayList<>();
        this.counts = new ArrayList<>();
    }

    /**
     * Works in synchronized manner with produced thread to take words off the buffer and keep a tally.
     */
    @Override
    public void run() {
        while (ProducerThread.isProducing || !buffer.isEmpty()) {
            synchronized (buffer) {
                while (buffer.isEmpty()) {
                    if (!ProducerThread.isProducing) {
                        break; // exit if production is complete and buffer is empty
                    }
                    try {
                        buffer.wait(); // wait for the producer to add data
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        System.out.println("Consumer interrupted.");
                        return;
                    }
                }

                if (!buffer.isEmpty()) {
                    // consume the first word in the buffer
                    String word = buffer.removeFirst();
                    // update the tally
                    updateWordTally(word);
                    buffer.notifyAll(); // notify producer
                }
            }
        }
        // display the final tally after processing all words
        displayWordTally();
    }

    /**
     * Updates the tally with words taken out of the buffer.
     * @param word word that which we are storing to keep count.
     */
    private void updateWordTally(String word) {
        int index = words.indexOf(word); // check current count
        // if already accounted for
        if (index != -1) {
            counts.set(index, counts.get(index) + 1); // increment count
        }
        // if new word
        else {
            words.add(word); // add new word
            counts.add(1);   // initialize count to 1
        }
    }

    /**
     * Displays the final tally of all words processed.
     */
    private void displayWordTally() {
        // if the array is empty, i.e. no words have been added
        if (words.isEmpty()) {
            System.out.println("No words were processed.");
            return;
        }
        // initialize maximum values with first value in array
        String maxWord = words.get(0);
        int maxCount = counts.get(0);

        // iterate through words array to find the word with the highest tally
        for (int i = 1; i < words.size(); i++) {
            if (counts.get(i) > maxCount) {
                // update max values if higher values are found
                maxCount = counts.get(i);
                maxWord = words.get(i);
            }
        }

        // display the word with the highest tally
        System.out.print("\nThe most frequent word in the file is \"" + maxWord + "\".\n");
        System.out.println("Frequency: " + maxCount);
    }

}

