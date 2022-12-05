import java.io.*;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Filtering class with static functions
 * to deal with filtering words
 *
 *
 * @author Vinh Pham Ngoc Thanh LC2
 * @version November 7 2022
 */
public class Filtering{

    public static ArrayList<String[]> censoredList(String username) {
        ArrayList<String[]> censoredPairs = new ArrayList<>();
        try {
            File userFilter = new File("filter/" + username);
            BufferedReader bfr = new BufferedReader(new FileReader(userFilter));
            String line;
            while ((line = bfr.readLine()) != null) {
                censoredPairs.add(line.split(";"));
            }
            bfr.close();
        } catch (IOException e) {

        }
        return censoredPairs;
    }

    public static boolean addFilter(String username, String censoredWord, String replacement) throws Exception {
        File userFilter = new File("filter/" + username);
        userFilter.createNewFile();
        BufferedReader bfr = new BufferedReader(new FileReader(userFilter));
        String line;
        while((line = bfr.readLine())!= null) {
            if(line.split(";")[0].equals(censoredWord)) {
                return false;
            }
        }
        bfr.close();
        PrintWriter wr = new PrintWriter(new FileWriter(userFilter, true));
        wr.println(censoredWord + ";" + replacement);
        wr.flush();
        wr.close();
        return true;
    }

    public static void deleteFilter(String username, String censoredWord) throws Exception {
        File userFilter = new File("filter/" + username);
        userFilter.createNewFile();
        ArrayList<String> newLines = new ArrayList<>();
        BufferedReader bfr = new BufferedReader(new FileReader(userFilter));
        String line;
        while((line = bfr.readLine())!= null) {
            if(!line.split(";")[0].equals(censoredWord)) {
                newLines.add(line);
            }
        }
        bfr.close();
        PrintWriter wr = new PrintWriter(new FileWriter(userFilter, false));
        for(String l: newLines) {
            wr.println(l);
            wr.flush();
        }
        wr.close();
    }

    public static void editFilter(String username, String censoredWord, String newReplacement) throws Exception {
        File userFilter = new File("filter/" + username);
        userFilter.createNewFile();
        ArrayList<String> newLines = new ArrayList<>();
        BufferedReader bfr = new BufferedReader(new FileReader(userFilter));
        String line;
        while((line = bfr.readLine())!= null) {
            if(line.split(";")[0].equals(censoredWord)) {
                newLines.add(censoredWord + ";" + newReplacement);
            } else {
                newLines.add(line);
            }
        }
        bfr.close();
        PrintWriter wr = new PrintWriter(new FileWriter(userFilter, false));
        for(String l: newLines) {
            wr.println(l);
            wr.flush();
        }
        wr.close();
    }

    /**
     * Check if the entered string is a legit word
     * We don't want user to enter something like
     * period, comma, semicolon or even spaces
     * to mess up our program
     * @param word
     * @return whether it's a legit word
     */
    public static boolean isLegitWord(String word) {
        char[] everyChar = word.toCharArray();
        for(char x: everyChar) {
            if(!Character.isAlphabetic(x)) {
                return false;
            }
        }
        return true;
    }

    /**
     * A helper class to replace a defined part
     * of the String
     * @param string
     * @param startEnd
     * @return new replaced String
     */
    public static String customReplace(String string, String replacement, int[] startEnd) {
        String head = string.substring(0, startEnd[0]);
        String newPart = replacement;
        String tail = string.substring(startEnd[1] + 1);
        String ans = head + newPart + tail;
        return ans;
    }

    /**
     * This is a lightFiler that only filtered
     * "shit" not "bullshit". The reason to have this
     * one is to avoid having "ate" as a censored word
     * but also displaying "concentrate" as "concentr***"
     * and "nitrate" as "nitr***". Looks sus
     *
     * @param message
     * @param
     * @return LightFiltered message
     */
    public static String lightFilter(String message, ArrayList<String[]> censoredWordsPairs) {
        //Locate where the actual words are
        ArrayList<int[]> wordPositions = new ArrayList<>();
        int start = 0;
        int end;
        while (start != message.length()) {
            if (!Character.isAlphabetic(message.charAt(start))) {
                if (start == message.length() - 1) {
                    break;
                }
                start++;
            }
            end = start;
            while (end != message.length() - 1 && Character.isAlphabetic(message.charAt(end + 1))) {
                end++;
            }
            if (Character.isAlphabetic(message.charAt(start))) {
                wordPositions.add(new int[]{start, end});
            }
            start = end + 1;
        }
        String ans = message;
        for (int[] startEnd : wordPositions) {
            for (String[] censoredWordPair : censoredWordsPairs) {
                if (censoredWordPair[0].equals(ans.substring(startEnd[0], startEnd[1] + 1).toLowerCase())) {
                    ans = customReplace(ans, censoredWordPair[1], startEnd);
                }
            }
        }
        return ans;
    }

    /**
     * A filter that basically function like
     * if you entered some letters together you
     * won't every see those together
     * @param message
     * @param
     * @return
     */
    public static String absoluteFilter(String message, ArrayList<String[]> censoredWordsPairs) {
        String ans = message;
        for (String[] censoredWordPair : censoredWordsPairs) {
            ans = ans.replace(censoredWordPair[0], censoredWordPair[1]);
        }
        return ans;
    }

}