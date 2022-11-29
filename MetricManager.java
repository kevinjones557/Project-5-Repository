import java.io.*;
import java.nio.file.Path;
import java.util.*;

/**
 * Handles various metric data for Sellers
 * To ensure files aren't files are accessed simultaneously, the class itself is used to lock.
 * @author Destin Groves
 * @version November 14th 2022
 */

public class MetricManager {
    /**
     * Takes a Seller's message and extracts frequency of words. It writes this information to metrics.txt
     * Yes, this uses maps. It's great, means I don't need to make a new class to hold 2 values.
     *
     * @param username The Seller's username
     * @param message  The Seller's message to be parsed
     * @param delete   whether this message is being deleted
     */
    public static void addDeleteMessageData(String username, String storePath, String message, boolean delete) {
        int messageCount = 0;
        String filePath;
        try {
            if (storePath != null) {
                filePath = storePath;
            } else {
                filePath = FileManager.getDirectoryFromUsername(username);
            }
        } catch (UserNotFoundException e) {
            throw new RuntimeException("User does not exist!");
        }
        filePath = filePath + "/metrics.txt";
        Map<String, Integer> fileData = new LinkedHashMap<>();
        /* The formatting for the Metrics file goes as follows:
            Message Count: n
            Word Frequency:
            20 word
            80 wordly
            10 words
         */
        synchronized (MetricManager.class) {
            try (BufferedReader bfr = new BufferedReader(new FileReader(filePath))) {
                String line = bfr.readLine();
                messageCount = Integer.parseInt(line.substring(15));
                line = bfr.readLine();
                while (line != null) {
                    String[] unmapped = line.split(" ");
                    fileData.put(unmapped[1], Integer.parseInt(unmapped[0]));
                    line = bfr.readLine();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        Map<String, Integer> data = new LinkedHashMap<>();

        String[] splitMessage = message.split(" ");
        // Split along spaces
        // renamed splitString to splitMessage for clarity
        for (String word : splitMessage) { // for every word in the string
            if (data.containsKey(word)) { // check if word exists in the map
                data.put(word, data.get(word) + 1); // if it does, increment
            } else {
                data.put(word, 1); // else, add it into the map
            }
        }

        data.forEach((word, count) -> {
            if (delete) {
                fileData.put(word, fileData.get(word) - data.get(word));
                if (count == 0) {
                    fileData.remove(word);
                }
            } else {
                if (fileData.containsKey(word)) {
                    fileData.put(word, fileData.get(word) + data.get(word));
                } else {
                    fileData.put(word, data.get(word));
                }
            }
        });
        synchronized (MetricManager.class) {
            try (BufferedWriter bfw1 = new BufferedWriter(new FileWriter(filePath, false))) {
                if (delete) {
                    bfw1.write(String.format("Message Count: %d\n", messageCount - 1));
                } else {
                    bfw1.write(String.format("Message Count: %d\n", messageCount + 1));
                }
                if (storePath != null) {
                    try (BufferedReader bfr2 = new BufferedReader(
                            new FileReader(storePath + "/" + username + "metrics.txt"))) {
                        String line = bfr2.readLine();
                        int messageCount2 = Integer.parseInt(line.substring(15));
                        bfr2.close();
                        try (BufferedWriter bfw2 = new BufferedWriter(
                                new FileWriter(storePath + "/" +
                                        username + "metrics.txt", false))) {
                            if (delete) {
                                messageCount2 -= 1;
                            } else {
                                messageCount2 += 1;
                            }
                            bfw2.write(String.format("Message Count: %d\n", messageCount2));
                            bfw2.flush();
                        }
                    }
                }
                fileData.forEach((word, count) -> {
                    try {
                        bfw1.write(String.format("%d %s\n", count, word));
                        bfw1.flush();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    /**
     * Takes a Seller's initial message, edited message,
     * and extracts the delta of the messages.
     * It writes this information to metrics.txt
     *
     * @param username        The Seller's username
     * @param previousMessage The Seller's previous message
     * @param newMessage      The Seller's new, edited message
     */
    public static void editMessageData(String username, String storePath, String previousMessage, String newMessage) {
        int messageCount = 0;
        String filePath;
        try {
            if (storePath != null) {
                filePath = storePath;
            } else {
                filePath = FileManager.getDirectoryFromUsername(username);
            }
        } catch (UserNotFoundException e) {
            throw new RuntimeException("User does not exist!");
        }
        filePath = filePath + "/metrics.txt";
        Map<String, Integer> fileData = new LinkedHashMap<>();
        /* The formatting for the Metrics file goes as follows:

            Message Count: n
            Word Frequency:
            20 word
            80 wordly
            10 words

         */

        synchronized (MetricManager.class) {
            try (BufferedReader bfr = new BufferedReader(new FileReader(filePath))) {
                String line = bfr.readLine();
                messageCount = Integer.parseInt(line.substring(15));
                line = bfr.readLine();
                while (line != null) {
                    String[] unmapped = line.split(" ");
                    fileData.put(unmapped[1], Integer.parseInt(unmapped[0]));
                    line = bfr.readLine();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        Map<String, Integer> firstMessageData = new LinkedHashMap<>();
        Map<String, Integer> newMessageData = new LinkedHashMap<>();
        Map<String, Integer> messageDelta = new LinkedHashMap<>();

        String[] splitMessage = previousMessage.split(" ");
        // Split along spaces
        // renamed splitString to splitMessage for clarity
        for (String word : splitMessage) { // for every word in the string
            if (firstMessageData.containsKey(word)) { // check if word exists in the map
                firstMessageData.put(word, firstMessageData.get(word) + 1); // if it does, increment
            } else {
                firstMessageData.put(word, 1); // else, add it into the map
            }
        }

        splitMessage = newMessage.split(" ");
        for (String word : splitMessage) { // for every word in the string
            if (newMessageData.containsKey(word)) { // check if word exists in the map
                newMessageData.put(word, newMessageData.get(word) + 1); // if it does, increment
            } else {
                newMessageData.put(word, 1); // else, add it into the map
            }
        }
        // yes this is the same code block twice

        firstMessageData.forEach((word, count) -> { // checks for commonality and words that have been removed
            if (newMessageData.containsKey(word)) {
                messageDelta.put(word, newMessageData.get(word) - firstMessageData.get(word));
                newMessageData.remove(word);
            } else {
                messageDelta.put(word, -firstMessageData.get(word));
            }
        });

        fileData.forEach((word, count) -> {
            if (messageDelta.containsKey(word)) {
                fileData.put(word, fileData.get(word) + messageDelta.get(word));
            }
        });

        fileData.putAll(newMessageData);
        synchronized (MetricManager.class) {
            try (BufferedWriter bfr = new BufferedWriter(new FileWriter(filePath, false))) {
                bfr.write(String.format("Message Count: %d\n", messageCount));
                fileData.forEach((word, count) -> {
                    try {
                        bfr.write(String.format("%d %s\n", count, word));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // removed UI since they'll need to be rewritten entirely

}