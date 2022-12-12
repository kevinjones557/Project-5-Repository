import java.io.*;
import java.util.ArrayList;

public class Invisible {
    public static final Object OBJ = new Object();

    /**
     * Get a list of users that this user can see
     *
     * @return an array of available people
     * @throws IOException if error
     */
    public static String[] getAvailableUsers(String currentUser, boolean isSeller) throws IOException {
        ArrayList<String> available = new ArrayList<>();
        synchronized (OBJ) {
            if (!isSeller) {
                File sellersDir = new File("data/sellers");
                String[] sellers = sellersDir.list();
                assert sellers != null;
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
                assert buyers != null;
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
        synchronized (OBJ) {
            String[] possibleSellers = Invisible.getAvailableUsers(currentUser, false);
            if (possibleSellers.length == 0) {
                return new String[0];
            }
            File sellers = new File("data/sellers");
            ArrayList<String> possibleStores = new ArrayList<>();
            String[] sellerNames = sellers.list();
            assert sellerNames != null;
            for (String name : sellerNames) { //Loop through all sellers
                for (String seller : possibleSellers) { //Loop through available sellers
                    if (seller.equals(name)) { //If matched then break loop then add stores
                        break;
                    }
                }
                File stores = new File("data/sellers/" + name);
                String[] storeNames = stores.list();
                assert storeNames != null;
                for (String store : storeNames) {
                    File storeFile = new File("data/sellers/" + name + "/" + store);
                    if (storeFile.isDirectory()) {
                        possibleStores.add(store);
                    }
                }

            }

            String[] availableStores = new String[possibleStores.size()];
            availableStores = possibleStores.toArray(availableStores);
            return availableStores;
        }
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
        synchronized (OBJ) {
            BufferedReader bfr = new BufferedReader(new FileReader(invisibleFile));
            String line;
            while ((line = bfr.readLine()) != null) {
                if (!line.isEmpty()) {
                    victims.add(line);
                }
            }
            bfr.close();
        }
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
        synchronized (OBJ) {
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
        }
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
        synchronized (OBJ) {
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

    /**
     * a method to return if a recipient can see the user or not
     * @param currentUser
     * @param isSeller
     * @param recipient
     * @return
     * @throws IOException
     */
    public static boolean recipientCantSeeMe(String currentUser, boolean isSeller, String recipient) throws IOException {
        synchronized (OBJ) {
            String[] blockList = invisibleList(currentUser, isSeller);
            for (String victim : blockList) {
                if (victim.equals(recipient)) {
                    return true;
                }
            }
            return false;
        }
    }
}
