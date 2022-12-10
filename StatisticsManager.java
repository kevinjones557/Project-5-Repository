import java.io.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;

public class StatisticsManager {
    /**
     * this method keeps count of messages sent
     * @param buyerName name of the buyer
     * @param storeName name of the store
     * @param message message sent
     */
    public synchronized static void addMessage(String buyerName, String storeName, String message) {
        File metricsFile = new File("data/buyers/" + buyerName + "/" + "metrics.txt");
        try (BufferedReader bfr = new BufferedReader(new FileReader(metricsFile))) {
            /* Example File
            Store-2
            Walmart-7
             */
            String line = bfr.readLine();
            ArrayList<String> contents = new ArrayList<>();
            boolean alreadyExists = false;
            while (line != null) {
                if (line.isBlank()) {
                    continue;
                }
                if (line.substring(0, line.indexOf("-")).equals(storeName)) {
                    contents.add(storeName + "-" + (Integer.parseInt(line.substring(line.indexOf("-") + 1)) + 1));
                    alreadyExists = true;
                } else {
                    contents.add(line);
                }
                line = bfr.readLine();
            }
            if (!alreadyExists) {
                contents.add(storeName + "-1"); //initial condition if it is the first message sent
            }
            // at this point the metric data is updated now write it back to file
            PrintWriter pw = new PrintWriter(new FileWriter(metricsFile, false));
            for (String s : contents) {
                pw.println(s);
                pw.flush();
            }
            pw.close();
            countWords(storeName, message);
        } catch (IOException io) {
            io.printStackTrace();
        }
    }

    /**
     * gets the info from the buyer's metrics file
     * @param buyerName name of buyer to generate metrics for
     * @return the metrics data
     */
    public synchronized static ArrayList<String[]> getStatisticsDataForBuyers(String buyerName) {
        try (BufferedReader bfr = new BufferedReader(new FileReader
                ("data/buyers/" + buyerName + "/metrics.txt"))) {
            ArrayList<String[]> data = new ArrayList<>();
            String line = bfr.readLine();
            while (line != null && !line.isBlank()) {
                String storeName = line.substring(0, line.indexOf("-"));
                int totalMessages = 0;
                String[] buyers = (new File("data/buyers")).list();
                assert buyers != null;
                for (String buyer : buyers) {
                    BufferedReader buyerReader = new BufferedReader(new FileReader
                            ("data/buyers/" + buyer + "/metrics.txt"));
                    String buyerLine = buyerReader.readLine();
                    while (buyerLine != null && !buyerLine.isBlank()) {
                        if (buyerLine.substring(0, buyerLine.indexOf("-")).equals(storeName)) {
                            totalMessages += Integer.parseInt(buyerLine.substring(buyerLine.indexOf("-") + 1));
                        }
                        buyerLine = buyerReader.readLine();
                    }
                }
                String[] storeMetrics = new String[3];
                storeMetrics[0] = storeName;
                storeMetrics[1] = "" + totalMessages;
                storeMetrics[2] = line.substring(line.indexOf("-") + 1);
                data.add(storeMetrics);
                line = bfr.readLine();
            }
            return data;
        } catch (IOException io) {
            io.printStackTrace();
            return new ArrayList<>();
        }
    }

    /**
     * returns data for statistics
     * @param sellerName name of seller
     * @return returns metrics data
     */
    public synchronized static ArrayList<String[]> getMetricDataForStores(String sellerName) {
        try {
            ArrayList<String> stores = FileManager.getStoresFromSeller(sellerName);
            ArrayList<String[]> allData = new ArrayList<>();
            for (String store : stores) {
                ArrayList<String> data = new ArrayList<>();
                data.add(store + "-");
                String[] buyers = (new File("data/buyers")).list();
                assert buyers != null;
                for (String buyer : buyers) {
                    BufferedReader buyerReader = new BufferedReader(new FileReader
                            ("data/buyers/" + buyer + "/metrics.txt"));
                    String buyerLine = buyerReader.readLine();
                    while (buyerLine != null && !buyerLine.isBlank()) {
                        if (buyerLine.substring(0, buyerLine.indexOf("-")).equals(store)) {
                            data.add(buyer + ": " + buyerLine.substring(buyerLine.indexOf("-") + 1));
                            break;
                        }
                        buyerLine = buyerReader.readLine();
                    }
                }
                allData.add(data.toArray(new String[0]));
            }
            return allData;
        } catch (IOException io) {
            io.printStackTrace();
            return new ArrayList<>();
        }
    }

    /**
     * this method keeps track of the instances of each word
     * @param storeName name of the store that message is sent to
     * @param message the actualy message containing words
     */
    public synchronized static void countWords(String storeName, String message) {
        LinkedHashMap<String, String> storeMap = FileManager.mapStoresToSellers();
        String seller = storeMap.get(storeName);
        File metricsFile = new File("data/sellers/" + seller + "/metrics.txt");
        String[] newWords = message.split(" ");
        try {
            metricsFile.createNewFile();
            BufferedReader bfr = new BufferedReader(new FileReader(metricsFile));
            ArrayList<String> words = new ArrayList<>();
            String line = bfr.readLine();
            while (line != null && !line.isBlank()) {
                words.add(line);
                line = bfr.readLine();
            }
            bfr.close();
            for (String w : newWords) {
                boolean alreadyRecorded = false;
                for (int i = 0; i < words.size(); i++) {
                    // check if word is already written if it is increase the count
                    if (!w.isBlank() && !words.get(i).isBlank() &&
                            w.trim().equals(words.get(i).substring(0, words.get(i).indexOf("-")))) {
                        words.set(i, words.get(i).substring(0, words.get(i).indexOf("-") + 1) + (Integer.parseInt(
                        words.get(i).substring(words.get(i).indexOf("-") + 1)) + 1));
                        alreadyRecorded = true;
                        break;
                    }
                }
                // if it's not already written into file write it now
                if (!alreadyRecorded && !w.isBlank()) {
                    words.add(w.trim() + "-" + 1);
                }
            }
            PrintWriter pw = new PrintWriter(new FileWriter(metricsFile, false));
            for (String recordedWord : words) {
                pw.println(recordedWord);
            }
            pw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * a method to return the most common ten words
     * @param sellerName name of seller to get data for
     * @return array of strings of data
     */
    public synchronized static String[] getTenMostCommonWords (String sellerName) {
        try (BufferedReader bfr = new BufferedReader(new FileReader("data/sellers/" + sellerName + "/"
                + "metrics.txt"))) {
            ArrayList<String> words = new ArrayList<>();
            String line = bfr.readLine();
            while (line != null && !line.isBlank()) {
                words.add(line);
                line = bfr.readLine();
            }
            // sort it with most common words first so that we can return those
            String temp;
            for (int i = 0; i < words.size(); i++) {
                for (int j = i + 1; j < words.size(); j++) {
                    // to compare one string with other strings
                    if (Integer.parseInt(words.get(i).substring(words.get(i).indexOf("-") + 1)) <
                            Integer.parseInt(words.get(j).substring(words.get(j).indexOf("-") + 1))) {
                        // swapping
                        temp = words.get(i);
                        words.set(i, words.get(j));
                        words.set(j, temp);
                    }
                }
            }
            // at this point the list is sorted so return the top ten
            String[] data = new String[Math.min(words.size(), 10)];
            for (int i = 0; i < data.length; i++) {
                data[i] = words.get(i).replace("-", ": ");
            }
            return data;
        } catch (IOException e) {
            e.printStackTrace();
            return new String[0];
        }
    }
}
