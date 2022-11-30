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
    public static void removeRenamedStore(String store, String newStore) {
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
    public static String isSeller(String user) {
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
     * Returns the stores that a user has registered under their username
     *
     * @param user user's username
     * @return String representation of the user's stores
     */
    public static String getUsersStores(String user) {
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
     * Removes a deleted user's stores from the store list
     *
     * @param storesString String representation of the user's stores
     */
    public static void appendStoreList(String storesString) {
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
    public static String readPassword(String user) {
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
     * Takes a password input by a user attempting to log in and uses the key to encrypt it for comparison
     *
     * @param input the password being encrypted
     * @return String of the encrypted password
     */
    public static String encrypt(String input) {
        String finalInput = "";
        char[] inputArray = input.toCharArray();
        for (int i = 0; i < inputArray.length; i++) {
            if (i % 2 == 0) {
                inputArray[i] += 5;
            } else {
                inputArray[i] -= 5;
            }
        }
        for (char c : inputArray) {
            finalInput += c;
        }
        return (finalInput);
    }

    /**
     * Writes user's username to a file
     *
     * @param user String of the user's username
     * @return boolean of if file was successfully written or not
     */
    public static boolean writeFile(String user) {
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
    public static boolean writeFile(String user, String toAppend) {
        try (PrintWriter pw = new PrintWriter(new FileOutputStream("users/" + user + "/" + user, true))) {
            pw.println(toAppend);
            return (true);
        } catch (Exception e) {
            return (false);
        }
    }

    /**
     * Encrypts the password of the user file when an account is created
     *
     * @param user the user whose password is being encrypted
     */
    public static void encryptFile(String user, String password) {
        try {
            char[] toBeEncrypted = password.toCharArray();
            for (int i = 0; i < toBeEncrypted.length; i++) {
                if (i % 2 == 0) {
                    toBeEncrypted[i] += 5;
                } else {
                    toBeEncrypted[i] -= 5;
                }
            }
            String finalPassword = "";
            for (char c : toBeEncrypted) {
                finalPassword += c;
            }
            writeFile(user, finalPassword);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Checks to see if the given store name is already in use
     *
     * @param storeName the name of the store being checked
     * @return boolean of if the store exists or not for handling in main
     */
    public static boolean checkStoreList(String storeName) {
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

    //checks either store names or usernames to see if they're valid
    public static String checkName(String name, boolean isStore, String user) {
        if (name == null) {
            return (null);
        }
        //if the name is store name, check to see if it's in use
        if (isStore) {
            boolean nameChecked = checkStoreList(name);
            if (!nameChecked) {
                return ("inUse");
            }
            if (name == null) {
                File userInfo = new File("users/" + user + "/" + user);
                userInfo.delete();
                File userDirectory = new File("users/" + user);
                userDirectory.delete();
                return (null);
            }
            if (name.equals("") || name.length() < 4
                    || name.length() > 16) {
                return ("invalid");
            }
            //TODO finish for stores
        } else {
            //this is for if it's a username
            try {
                File f;
                File dir = new File("users/" + user);
                if (!dir.createNewFile()) {
                    dir.delete();
                    return ("inUse");
                }
                //checking other criteria
                if (user.equals("") || user.length() < 6 ||
                        user.length() > 16 || user.contains(" ")) {
                    dir.delete();
                    return ("invalid");
                }
                dir.delete();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        String notAllowed = "`~!@#$%^&*()+-=[]{}\\|;:\"\'>.<,?/";
        for (int i = 0; i < notAllowed.length(); i++) {
            if (name.indexOf(notAllowed.charAt(i)) != -1) {
                return ("invalid");
            }
        }
        return ("valid");
    }

    public static void deleteUserInProgress(String user) {
        File userInfo = new File("users/" + user + "/" + user);
        userInfo.delete();
        File userDirectory = new File("users/" + user);
        userDirectory.delete();
    }

    public static void createUser (String user) {
        try {
            Files.createDirectory(Paths.get("users/" + user));
            File f = new File("users/" + user + "/" + user);
            writeFile(user);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //checks the password that the user selects to be their password
    //TODO with time, reinstate password confirmation
    public static String checkPassword(String password, String user) {
        if (password == null) {
            File userInfo = new File("users/" + user + "/" + user);
            userInfo.delete();
            File userDirectory = new File("users/" + user);
            userDirectory.delete();
            return (null);
        }
        if (password.length() < 8 || password.length() > 16
                || password.contains(";")) {
            return ("invalid");
        }
        encryptFile(user, password);
        return ("valid");
    }

    //move username to new location after name change
    public static void moveUsername(String user, String newUser) {
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

    public static void changeStoreName(List<String> storesArray, String storeToChange, String storeName, String user) {
        storesArray.set(storesArray.indexOf(storeToChange), storeName);
        try (BufferedReader br = new BufferedReader(new FileReader("users/" + user + "/" + user))) {
            ArrayList<String> fileContents = new ArrayList<>();
            String line = br.readLine();
            while (line != null) {
                fileContents.add(line);
                line = br.readLine();
            }
            fileContents.set(3, storesArray.toString());
            writeFile(user);
            for (int i = 0; i < fileContents.size(); i++) {
                if (i != 0) {
                    writeFile(user, fileContents.get(i));
                }
            }
            removeRenamedStore(storeToChange, storeName);
            //TODO MarketUser calls
            //MarketUser.changeStoreName(storeToChange, newName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //check if the user exists
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
    public static void updateStoreList(String storeName) {
        try (PrintWriter pw = new PrintWriter(new FileOutputStream("users/storeNames", true))) {
            pw.println(storeName);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("An unknown error occurred!");
        }
    }
}