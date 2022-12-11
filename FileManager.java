import java.io.*;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedHashMap;

/**
 * Handles various file management methods for the program
 *
 * @author Destin Groves
 * @version November 14th, 2022
 */
public class FileManager {
    /**
     * Used to find a file directory for a given User. Useful when writing or reading files.
     *
     * @param username The username associated with the user directory you wish to find
     * @return the file directory found associated with the given username.
     * Throws a UserNotFoundException when it cannot find the user.
     * (Yes, this should be an @exception. IntelliJ doesn't understand that now, for some reason)
     */
    public static String getDirectoryFromUsername(String username) throws UserNotFoundException {
        if (Files.exists(Paths.get("data/buyers/" + username))) {
            return "data/buyers/" + username + "/";
        } else if (Files.exists(Paths.get("data/sellers/" + username))) {
            return "data/sellers/" + username + "/";
        } else {
            throw new UserNotFoundException("The requested User does not exist!");
        }
    }

    /**
     * Checks if a User with given username has an associated directory, and therefore exists
     *
     * @param username The username associated with the user directory you wish to find
     * @return if the directory exists
     */
    public static boolean checkUserExists(String username) {
        if (Files.exists(Paths.get("data/buyers/" + username))) {
            //if user exists
            return true;
        }
        if (Files.exists(Paths.get("data/sellers/" + username))) {
            //if user exists
            return true;
        }
        //if user doesn't exist
        return false;
    }

    public static boolean checkSellerExists(String username) {
        return (Files.exists(Paths.get("data/sellers/" + username)));
    }

    public static boolean checkBuyerExists(String username) {
        return (Files.exists(Paths.get("data/buyers/" + username)));
    }

    /**
     * @param seller name of the seller whose stores we are returning
     * @return an arraylist of Stores that the seller has
     * @author Kevin Jones
     */
    public synchronized static ArrayList<String> getStoresFromSeller(String seller) {
        File sellerDirectory = new File("data/sellers/" + seller);
        String[] possibleStores = sellerDirectory.list();
        ArrayList<String> sellerStores = new ArrayList<>();
        if (possibleStores != null) {
            for (String store : possibleStores) {
                File storeFile = new File("data/sellers/" + seller + "/" + store);
                if (storeFile.isDirectory()) {
                    sellerStores.add(store);
                }
            }
        }
        return sellerStores;
    }

    public static String getStoreDirectory(String username, String storeName) {
        return String.format("data/sellers/%s/%s/", username, storeName);
    }


    /**
     * Used to generate a directory for new Users.
     *
     * @param username The username of the new User you wish to create a directory for.
     * @param isSeller Determines whether the User is a Seller or a Customer.
     * @return true if the directory was created. Returns false if the directory exists, or some other error occurs.
     */
    public synchronized static boolean generateDirectoryFromUsername(String username, boolean isSeller) {
        try {
            Path filePath;
            if (isSeller) {
                filePath = Files.createDirectories(Paths.get("data/sellers/" + username));
            } else {
                filePath = Files.createDirectory(Paths.get("data/buyers/" + username));
            }
            Files.createFile(Paths.get(filePath + "/metrics.txt"));
            Files.createFile(Paths.get(filePath + "/hasBlocked.txt"));
            Files.createFile(Paths.get(filePath + "/isInvisible.txt"));
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * Generates a store subdirectory for a Seller user.
     *
     * @param username  the Username of the Seller
     * @param storeName the name of the Store the Seller wishes to make
     * @return true if the directory was created, false if the directory already exists or an exception occurs.
     */
    public synchronized static boolean generateStoreForSeller(String username, String storeName) {
        try {
            Files.createDirectory(Paths.get(getDirectoryFromUsername(username) + storeName));
            return true;
        } catch (UserNotFoundException e) {
            System.out.println("Seller not found.");
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean generateMetricsAboutUser(String buyer, String storePath) {
        try {
            Path filePath = Paths.get(storePath);
            Path metrics = Files.createFile(Paths.get(filePath + "/" + buyer + "metrics.txt"));
            return true;
        } catch (FileAlreadyExistsException e) {
            return false; // don't print stacktrace because this is supposed to happen
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * @return
     */
    public static LinkedHashMap<String, String> mapStoresToSellers() {
        LinkedHashMap<String, String> storesMapped = new LinkedHashMap<>();
        String[] sellerList = new File("data/sellers/").list();
        if (!(sellerList == null)) {
            for (String seller : sellerList) {
                String[] storesOwned = new File("data/sellers/" + seller).list();
                if (!(storesOwned == null)) {
                    for (String store : storesOwned) {
                        if (Files.isDirectory(Paths.get("data/sellers/" + seller + "/" + store))) {
                            storesMapped.put(store, seller);
                        }

                    }
                }
            }
        }
        return storesMapped;
    }

    /**
     * A method to check if the recipient is a store or not
     * @param storeName name of possible store
     * @return true or false if the storeName is a store
     * @author Kevin Jones
     */
    public static boolean isRecipientStore(String storeName) {
        String[] sellers = (new File("data/sellers")).list();
        assert sellers != null;
        for (String seller : sellers) {
            if ((Files.exists(Paths.get("data/sellers/" + seller + "/" + storeName)))) {
                return true;
            }
        }
        return false;
    }

    /**
     * method returns an arrayList that contains the names of buyers that a given store has messaged
     * @param sellerName is the store's seller's name
     * @param storeName is the name of the store
     * @return is the arrayList of buyer conversations
     * @author Kevin Jones
     */

    public static ArrayList<String> getConversationsFromStore(String sellerName, String storeName) {
        File storeFile = new File(getStoreDirectory(sellerName, storeName));
        String[] conversations = storeFile.list();
        ArrayList<String> buyerNames = new ArrayList<>();
        if (conversations != null) {
            for (String fileName : conversations) {
                buyerNames.add(fileName.substring(storeName.length(), fileName.indexOf(".")));
            }
        }
        return buyerNames;
    }

    /** method returns an arrayList that contains the names of buyers that a given store has messaged
     * @param username is the store's seller's name
     * @return is the arrayList of buyer conversations
     * @author Kevin Jones
     */

    public static synchronized ArrayList<String> getConversationsFromUser(String username) {
        try {
            File storeFile = new File(getDirectoryFromUsername(username));
            String[] conversations = storeFile.list();
            ArrayList<String> names = new ArrayList<>();
            if (conversations != null) {
                for (String fileName : conversations) {
                    if (!fileName.equals("hasBlocked.txt") && !fileName.equals("metrics.txt") &&
                            !fileName.equals("isInvisible.txt") && !fileName.equals(username + ".txt") &&
                            !(new  File(getStoreDirectory(username, fileName))).isDirectory()) {
                        names.add(fileName.substring(username.length(), fileName.indexOf(".")));
                    }
                }
            }
            return names;
        } catch (UserNotFoundException u) {
            return new ArrayList<>();
        }
    }

    /**
     * @return an array of strings of all stores
     * @author Kevin Jones
     */
    public static String[] getAllStores() {
        return mapStoresToSellers().keySet().toArray(new String[0]);
    }


    public static synchronized ArrayList<String> readFile(File file) throws IOException {
        ArrayList<String> fileData = new ArrayList<>();
        BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
        String line = bufferedReader.readLine();
        while (line != null) {
            fileData.add(line);
            line = bufferedReader.readLine();
        }
        bufferedReader.close();
        return fileData;
    }

    public static synchronized void writeFile(File file, String[] data, boolean append) throws IOException {
        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file, append));
        for (String datum : data) {
            bufferedWriter.write(datum);
            bufferedWriter.newLine();
        }
        bufferedWriter.close();
    }
}
