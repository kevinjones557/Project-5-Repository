import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Objects;

public class Blocking {
    /**
     * blockedList method
     *
     * @param currentUser
     * @param isSeller
     * @return list of names of users that
     * the current user has blocked
     */
    public static String[] blockedList(String currentUser, boolean isSeller) throws IOException {
        ArrayList<String> victims = new ArrayList<>();
        String blockedFilePath = "data/" + ((isSeller) ? "sellers/" : "buyers/") +
                currentUser + "/hasBlocked.txt";
        File blockedFile = new File(blockedFilePath);
        BufferedReader bfr = new BufferedReader(new FileReader(blockedFile));
        String line;
        while ((line = bfr.readLine()) != null) {
            if (!line.isEmpty()) {
                victims.add(line);
            }
        }
        bfr.close();
        String[] blockedList = new String[victims.size()];
        for (int i = 0; i < victims.size(); i++) {
            blockedList[i] = victims.get(i);
        }
        return blockedList;
    }

    /**
     * BlockUser method that writes the victim's
     * name into current user's hasBlocked file
     *
     * @param currentUser
     * @param victim
     * @param isSeller
     * @return true if the victim is
     * already blocked, false otherwise
     */
    public static boolean blockUser(String currentUser, String victim, boolean isSeller) throws IOException {
        String blockedFilePath = "data/" + ((isSeller) ? "sellers/" : "buyers/") +
                currentUser + "/hasBlocked.txt";
        File blockedFile = new File(blockedFilePath);
        BufferedReader bfr = new BufferedReader(new FileReader(blockedFile));
        String line;
        while ((line = bfr.readLine()) != null) {
            if (line.equals(victim)) {
                //Already blocked this user
                return true;
            }
        }
        bfr.close();
        //Write the name of the victim to hasBlocked file
        PrintWriter pw = new PrintWriter(new FileWriter(blockedFile, true));
        pw.write(victim);
        pw.println();
        pw.flush();
        pw.close();
        return false;

    }

    /**
     * UnblockUser method which removes the name of the
     * target from current user's hasBlocked file
     *
     * @param currentUser
     * @param victim
     * @param isSeller
     */
    public static void unblockUser(String currentUser, String victim, boolean isSeller) throws IOException {
        ArrayList<String> lines = new ArrayList<>();
        String blockedFilePath = "data/" + ((isSeller) ? "sellers/" : "buyers/") +
                currentUser + "/hasBlocked.txt";
        File blockedFile = new File(blockedFilePath);
        BufferedReader bfr = new BufferedReader(new FileReader(blockedFile));
        String line;
        while ((line = bfr.readLine()) != null) {
            if (!line.equals(victim) && !line.isEmpty()) {
                lines.add(line);
            }
        }
        bfr.close();
        PrintWriter pw = new PrintWriter(new FileWriter(blockedFile, false));
        for (String l : lines) {
            pw.write(l);
            pw.println();
        }
        pw.flush();
        pw.close();

    }

    /**
     * getMessageAbleUser method
     *
     * @param currentUser
     * @param isSeller
     * @return a list of users that this user can message
     * @throws IOException
     */
    public static String[] getMessageAbleUser(String currentUser, boolean isSeller) throws IOException {
        ArrayList<String> available = new ArrayList<>();
        if (!isSeller) {
            File sellersDir = new File("data/sellers");
            String[] sellers = sellersDir.list();
            for (String seller : sellers) {
                File blockedFilePath = new File("data/sellers/"
                        + seller + "/hasBlocked.txt");
                BufferedReader bfr = new BufferedReader(new FileReader(blockedFilePath));
                String line;
                boolean blocked = false;
                while ((line = bfr.readLine()) != null) {
                    if (line.equals(currentUser)) {
                        blocked = true;
                        break;
                    }
                }
                bfr.close();
                if (!blocked) {
                    for (String store : Objects.requireNonNull((new File("data/sellers/" + seller)).list())) {
                        if (Files.isDirectory(Paths.get("data/sellers/" + seller + "/" + store))) {
                            available.add(store);
                        }
                    }
                }
            }
        } else {
            File buyersDir = new File("data/buyers");
            String[] buyers = buyersDir.list();
            for (String buyer : buyers) {
                File blockedFilePath = new File("data/buyers/"
                        + buyer + "/hasBlocked.txt");
                BufferedReader bfr = new BufferedReader(new FileReader(blockedFilePath));
                String line;
                boolean blocked = false;
                while ((line = bfr.readLine()) != null) {
                    if (line.equals(currentUser)) {
                        blocked = true;
                        break;
                    }
                }
                bfr.close();
                if (!blocked) {
                    available.add(buyer);
                }
            }
        }
        //Just turn ArrayList into array classic 180 stuff
        String[] messageAble = new String[available.size()];
        for (int i = 0; i < messageAble.length; i++) {
            messageAble[i] = available.get(i);
        }
        return messageAble;
    }
}