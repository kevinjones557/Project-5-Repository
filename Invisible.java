import java.io.*;
import java.util.ArrayList;

public class Invisible {
    /**
     * Get a list of users that this user can see
     *
     * @return an array of available people
     * @throws IOException if error
     */
    public static String[] getAvailableUsers(String currentUser, boolean isSeller) throws IOException {
        ArrayList<String> available = new ArrayList<>();
        if (!isSeller) {
            File sellersDir = new File("data/sellers");
            String[] sellers = sellersDir.list();
            for (String seller : sellers) {
                File sellerFolder = new File("data/sellers/" + seller);
                File invisibleFilePath = new File("data/sellers/"
                        + seller + "/isInvisible.txt");
                BufferedReader bfr = new BufferedReader(new FileReader(invisibleFilePath));
                String line;
                boolean invisible = false;
                while ((line = bfr.readLine()) != null) {
                    if (line.equals(currentUser)) {
                        invisible = true;
                        break;
                    }
                }
                bfr.close();
                if (!invisible) {
                    available.add(seller);
                }
            }
        } else {
            File buyersDir = new File("data/buyers");
            String[] buyers = buyersDir.list();
            for (String buyer : buyers) {
                File invisibleFilePath = new File("data/buyers/" +
                        buyer + "/isInvisible.txt");
                BufferedReader bfr = new BufferedReader(new FileReader(invisibleFilePath));
                String line;
                boolean invisible = false;
                while ((line = bfr.readLine()) != null) {
                    if (line.equals(currentUser)) {
                        invisible = true;
                        break;
                    }
                }
                bfr.close();
                if (!invisible) {
                    available.add(buyer);
                }
            }
        }
        //Just turn ArrayList into array classic 180 stuff
        String[] availables = new String[available.size()];
        for (int i = 0; i < availables.length; i++) {
            availables[i] = available.get(i);
        }
        return availables;
    }
    /**
     * Get a list of stores this user can see
     *
     * @return an array of available stores
     * @throws IOException in case of error
     */
    public static String[] getAvailableStores(String currentUser) throws IOException {
        boolean invisible = true;
        String[] possibleSellers = Invisible.getAvailableUsers(currentUser, false);
        File sellers = new File("data/sellers");
        ArrayList<String> possibleStores = new ArrayList<>();
        String[] sellerNames = sellers.list();
        for (String name : sellerNames) {
            for (String seller : possibleSellers) {
                if (seller.equals(name)) {
                    invisible = false;
                    break;
                }
            }
            if (!invisible) {
                File stores = new File("data/sellers/" + name);
                String[] storeNames = stores.list();
                for (String store : storeNames) {
                    File storeFile = new File("data/sellers/" + name + "/" + store);
                    if (storeFile.isDirectory()) {
                        possibleStores.add(store);
                    }
                }
            }
        }
        String[] availableStores = new String[possibleStores.size()];
        availableStores = possibleStores.toArray(availableStores);
        return availableStores;
    }

    /**
     * Return list of people that can't see this user (and his stores)
     *
     * @return array of people that can't see this user (and his stores)
     * @throws IOException
     */
    public static String[] invisibleList(String currentUser, boolean isSeller) throws IOException {

        ArrayList<String> victims = new ArrayList<>();
        String invisibleFilePath = "data/" + ((isSeller) ? "sellers/" : "buyers/") +
                currentUser + "/isInvisible.txt";
        File invisibleFile = new File(invisibleFilePath);
        BufferedReader bfr = new BufferedReader(new FileReader(invisibleFile));
        String line;
        while ((line = bfr.readLine()) != null) {
            if (!line.isEmpty()) {
                victims.add(line);
            }
        }
        bfr.close();
        String[] invisibleList = new String[victims.size()];
        for (int i = 0; i < victims.size(); i++) {
            invisibleList[i] = victims.get(i);
        }
        return invisibleList;
    }
    /**
     * Become invisible to a user if not already invisible
     *
     * @return true if already invisible, false otherwise
     * @throws IOException
     */
    public static boolean becomeInvisibleToUser(String currentUser, String victim, boolean isSeller) throws IOException {
        String invisibleFilePath = "data/" + ((isSeller) ? "sellers/" : "buyers/")
                + currentUser + "/isInvisible.txt";
        File invisibleFile = new File(invisibleFilePath);
        BufferedReader bfr = new BufferedReader(new FileReader(invisibleFile));
        String line;
        while ((line = bfr.readLine()) != null) {
            if (line.equals(victim)) {
                //Already become invisible to this user
                return true;
            }
        }
        bfr.close();
        //Write the name of the victim to hasBlocked file
        PrintWriter pw = new PrintWriter(new FileWriter(invisibleFile, true));
        pw.write(victim);
        pw.println();
        pw.flush();
        pw.close();
        return false;

    }
    /**
     * Become visible to a user from the isInvisible file
     */
    public static void becomeVisibleAgain(String currentUser, String victim, boolean isSeller) throws IOException {
        ArrayList<String> lines = new ArrayList<>();
        String invisibleFilePath = "data/" + ((isSeller) ? "sellers/" : "buyers/") +
                currentUser + "/isInvisible.txt";
        File invisibleFile = new File(invisibleFilePath);
        BufferedReader bfr = new BufferedReader(new FileReader(invisibleFile));
        String line;
        while ((line = bfr.readLine()) != null) {
            if (!line.equals(victim) && !line.isEmpty()) {
                lines.add(line);
            }
        }
        bfr.close();
        PrintWriter pw = new PrintWriter(new FileWriter(invisibleFile, false));
        for (String l : lines) {
            pw.write(l);
            pw.println();
        }
        pw.flush();
        pw.close();
    }
}
