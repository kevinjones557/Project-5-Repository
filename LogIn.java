import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LogIn {

    /**
     * Removes the name of a store that gets renamed
     *
     * @param store    the store being deleted
     * @param newStore the new store name overwriting the old store name
     */
    public synchronized static void removeRenamedStore(String store, String newStore) {
        try (BufferedReader br = new BufferedReader(new FileReader("users/storeNames"))) {
            List<String> fileContents = new ArrayList<>();
            String line = br.readLine();
            while (line != null) {
                fileContents.add(line);
                line = br.readLine();
            }
            fileContents.set(fileContents.indexOf(store), newStore);
            try (PrintWriter pw = new PrintWriter(new FileOutputStream("users/storeNames", false))) {
                for (String s : fileContents) {
                    pw.println(s);
                }
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("An unknown error occurred!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("An unknown error occurred!");
        }
    }

    /**
     * Returns if the user is a seller
     *
     * @param user user's username
     * @return String representation of the user's isSeller status
     */
    public synchronized static String isSeller(String user) {
        try (BufferedReader br = new BufferedReader(new FileReader("users/" + user + "/" + user))) {
            int lineIndex = 0;
            String line = br.readLine();
            while (line != null) {
                if (lineIndex == 2) {
                    return (line);
                }
                line = br.readLine();
                lineIndex++;
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("An unknown error occurred!");
        }
        return (null);
    }

    /**
     * Removes a deleted user's stores from the store list
     *
     * @param storesString String representation of the user's stores
     */
    public synchronized static void appendStoreList(String storesString) {
        try (BufferedReader br = new BufferedReader(new FileReader("users/storeNames"))) {
            java.util.List<String> stores = Arrays.asList(storesString.split(", "));
            List<String> fileContents = new ArrayList<>();
            String line = br.readLine();
            while (line != null) {
                fileContents.add(line);
                line = br.readLine();
            }
            for (String s : stores) {
                if (fileContents.contains(s)) {
                    fileContents.remove(s);
                }
            }
            try (PrintWriter pw = new PrintWriter(new FileOutputStream("users/storeNames", false))) {
                for (String s : fileContents) {
                    pw.println(s);
                }
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("An unknown error occurred!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("An unknown error occurred!");
        }
    }

    /**
     * Reads the password of the file for comparison
     *
     * @param user the user whose password is being read
     * @return String of the encrypted password
     */
    public synchronized static String readPassword(String user) {
        try (BufferedReader br = new BufferedReader(new FileReader("users/" + user + "/" + user))) {
            String password = "";
            int index = 0;
            String line = br.readLine();
            while (line != null) {
                index++;
                if (index == 2) {
                    password = line;
                }
                line = br.readLine();
            }
            return (password);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("An unknown error occurred!");
        }
        return (null);
    }

    /**
     * Writes user's username to a file
     *
     * @param user String of the user's username
     * @return boolean of if file was successfully written or not
     */
    public synchronized static boolean writeFile(String user) {
        try (PrintWriter pw = new PrintWriter(new FileOutputStream("users/" + user + "/" + user, false))) {
            pw.println(user);
            return (true);
        } catch (Exception e) {
            e.printStackTrace();
            return (false);
        }
    }

    /**
     * Appends an additional line to a given user's file
     *
     * @param user     the user whose file is being appended
     * @param toAppend the parameter that is being appended to the file
     * @return boolean of if the file was successfully written or not
     */
    public synchronized static boolean writeFile(String user, String toAppend) {
        try (PrintWriter pw = new PrintWriter(new FileOutputStream("users/" + user + "/" + user, true))) {
            pw.println(toAppend);
            return (true);
        } catch (Exception e) {
            return (false);
        }
    }

    /**
     * Returns the stores that a user has registered under their username
     *
     * @param user user's username
     * @return String representation of the user's stores
     */
    public synchronized static String getUsersStores(String user) {
        try (BufferedReader br = new BufferedReader(new FileReader("users/" + user + "/" + user))) {
            String stores = "";
            int lineIndex = 0;
            String line = br.readLine();
            while (line != null) {
                if (lineIndex == 2 && line.equals("false")) {
                    return (null);
                }
                if (lineIndex == 3) {
                    stores = line;
                }
                line = br.readLine();
                lineIndex++;
            }
            stores = stores.substring(1, stores.length() - 1);
            return (stores);
        } catch (Exception e) {
            e.printStackTrace();
            return (null);
        }
    }

    /**
     * Checks to see if the given store name is already in use
     *
     * @param storeName the name of the store being checked
     * @return boolean of if the store exists or not for handling in main
     */
    public synchronized static boolean checkStoreList(String storeName) {
        try (BufferedReader br = new BufferedReader(new FileReader("users/storeNames"))) {
            ArrayList<String> fileContents = new ArrayList<>();
            String line = br.readLine();
            if (line == null) {
                return (true);
            }
            while (line != null) {
                fileContents.add(line);
                line = br.readLine();
            }
            for (String fileContent : fileContents) {
                if (fileContent.equals(storeName)) {
                    return (false);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("An unknown error occurred!");
        }
        return (true);
    }

    public synchronized static void deleteUserInProgress(String user) {
        File userInfo = new File("users/" + user + "/" + user);
        userInfo.delete();
        File userDirectory = new File("users/" + user);
        userDirectory.delete();
    }

    /**
     * Creates the user with the given username
     *
     * @param user the user being created
     */
    public synchronized static void createUser (String user) {
        try {
            Files.createDirectory(Paths.get("users/" + user));
            File f = new File("users/" + user + "/" + user);
            writeFile(user);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Moves a user to their new username after they change it
     *
     * @param user old username
     * @param newUser new username
     */
    public synchronized static void moveUsername(String user, String newUser) {
        try {
            //this method was retrieved with help from StackOverflow user @kr37
            Path source = Paths.get("users/" + user + "/" + user);
            Files.move(source, source.resolveSibling(newUser));
            source = Paths.get("users/" + user);
            Files.move(source, source.resolveSibling(newUser));
            BufferedReader br = new BufferedReader(new FileReader("users/" + newUser + "/" + newUser));
            ArrayList<String> fileContents = new ArrayList<>();
            String line = br.readLine();
            while (line != null) {
                fileContents.add(line);
                line = br.readLine();
            }
            writeFile(newUser);
            fileContents.remove(0);
            for (String s : fileContents) {
                writeFile(newUser, s);
            }
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Checks to see if the given user exists
     *
     * @param user the username being checked
     * @return boolean of if the user exists or not
     */
    public static boolean checkUserExists(String user) {
        File f = new File("users/" + user);
        try {
            if (f.createNewFile()) {
                f.delete();
                return (false);
            } else {
                return (true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return (false);
    }

    /**
     * Updates the store list by adding a store name that has been confirmed to not be in use already
     *
     * @param storeName the store name being appended to the file
     */
    public synchronized static void updateStoreList(String storeName) {
        try (PrintWriter pw = new PrintWriter(new FileOutputStream("users/storeNames", true))) {
            pw.println(storeName);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("An unknown error occurred!");
        }
    }
}