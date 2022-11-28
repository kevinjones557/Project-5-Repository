import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class LogInGui {

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

    public static void main(String[] args) {
        try {
            String user = null;
            boolean done;
            String[] options = new String[2];
            options[0] = "Log in";
            options[1] = "Sign up";
            int input = JOptionPane.showOptionDialog(null,
                    "Welcome! Would you like to log in or sign up?",
                    "Messaging program", 0,
                    JOptionPane.QUESTION_MESSAGE, null, options, null);
            if (input == -1) {
                return;
            }
            if (input == 0) {
                boolean userFound = false;
                done = false;
                while (!done) {
                    user = JOptionPane.showInputDialog(null,
                            "Enter your username.",
                            "Messaging program", JOptionPane.PLAIN_MESSAGE);
                    if (user == null) {
                        return;
                    }
                    if (user.equals("")) {
                        while (user.equals("")) {
                            JOptionPane.showMessageDialog(null,
                                    "Username cannot be blank!",
                                    "Messaging program", JOptionPane.ERROR_MESSAGE);
                            user = JOptionPane.showInputDialog(null,
                                    "Enter your username.",
                                    "Messaging program", JOptionPane.PLAIN_MESSAGE);
                            if (user == null) {
                                return;
                            }
                        }
                    }
                    try {
                        File f = new File("users/" + user);
                        if (f.createNewFile()) {
                            f.delete();
                            String[] userOptions = {"Yes", "No"};
                            String continueUser = (String) JOptionPane.showInputDialog(null,
                                    "User doesn't exist! Would you like to try again?",
                                    "Messaging program", JOptionPane.QUESTION_MESSAGE,
                                    null, userOptions, null);
                            if (continueUser == null) {
                                return;
                            }
                            if (continueUser.equals("No")) {
                                return;
                            }
                        } else {
                            userFound = true;
                            done = true;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                if (userFound) {
                    done = false;
                    while (!done) {
                        String passwordInput = JOptionPane.showInputDialog(null,
                                "Enter your password.",
                                "Messaging program", JOptionPane.PLAIN_MESSAGE);
                        if (passwordInput == null) {
                            return;
                        }
                        if (encrypt(passwordInput).equals(readPassword(user))) {
                            //TODO user logged in successfully
                            JOptionPane.showMessageDialog(null,
                                    "Welcome!",
                                    "Messaging program", JOptionPane.PLAIN_MESSAGE);
                            done = true;
                        } else {
                            boolean continuePassword = false;
                            while (!continuePassword) {
                                String[] userOptions = {"Yes", "No"};
                                String continueUser = (String) JOptionPane.showInputDialog(null,
                                        "Incorrect Password! Would you like to try again?",
                                        "Messaging program", JOptionPane.QUESTION_MESSAGE,
                                        null, userOptions, null);
                                if (continueUser.equals("No") || continueUser == null) {
                                    return;
                                }
                                continuePassword = true;
                            }
                        }
                    }
                }
            } else if (input == 1) {
                //user makes new account
                options[0] = "Buyer";
                options[1] = "Seller";
                input = JOptionPane.showOptionDialog(null,
                        "Please choose your account role.",
                        "Messaging program", 0,
                        JOptionPane.QUESTION_MESSAGE, null, options, null);
                if (input == -1) {
                    return;
                }
                //value to write for isSeller in file
                String isSeller = null;
                if (input == 1) {
                    isSeller = "true";
                } else if (input == 0) {
                    isSeller = "false";
                } else {
                    //user hits exit
                    return;
                }
                user = JOptionPane.showInputDialog(null,
                        "Enter your username.",
                        "Messaging program", JOptionPane.PLAIN_MESSAGE);
                File f;
                File dir = new File("users/" + user);
                try {
                    done = false;
                    while (!done) {
                        if (user == null) {
                            dir.delete();
                            return;
                        }
                        if (!dir.createNewFile()) {
                            dir.delete();
                            JOptionPane.showMessageDialog(null,
                                    "Username already exists! Please enter another username.",
                                    "Messaging program", JOptionPane.ERROR_MESSAGE);
                            user = JOptionPane.showInputDialog(null,
                                    "Enter your username.",
                                    "Messaging program", JOptionPane.PLAIN_MESSAGE);
                            if (user == null) {
                                dir.delete();
                                return;
                            }
                            dir = new File("users/" + user);
                            //TODO make sure they can't enter quotations or characters
                        } else if (user.equals("") || user.length() < 6 || user.length() > 16 || user.contains(" ")) {
                            dir.delete();
                            JOptionPane.showMessageDialog(null,
                                    "Username constraints: " +
                                            "\n- Cannot be blank " +
                                            "\n- Must be in between 6 and 16 characters inclusive " +
                                            "\n- Cannot contain spaces " +
                                            "\nPlease enter a valid username.",
                                    "Messaging program", JOptionPane.ERROR_MESSAGE);
                            user = JOptionPane.showInputDialog(null,
                                    "Enter your username.",
                                    "Messaging program", JOptionPane.PLAIN_MESSAGE);
                            if (user == null) {
                                dir.delete();
                                return;
                            }
                            dir = new File("users/" + user);
                        } else {
                            done = true;
                        }
                    }
                    dir.delete();
                    Files.createDirectory(Paths.get("users/" + user));
                    System.out.println(user);
                    f = new File("users/" + user + "/" + user);
                    writeFile(user);
                    done = false;
                    try {
                        while (!done) {
                            String password = JOptionPane.showInputDialog(null,
                                    "Please enter a password between 8 and 16 characters.",
                                    "Messaging program", JOptionPane.PLAIN_MESSAGE);
                            if (password == null) {
                                File userInfo = new File("users/" + user + "/" + user);
                                userInfo.delete();
                                File userDirectory = new File("users/" + user);
                                userDirectory.delete();
                                return;
                            }
                            if (password.length() < 8 || password.length() > 16) {
                                while (password.length() < 8 || password.length() > 16) {
                                    password = JOptionPane.showInputDialog(null,
                                            "Password length must be between 8 and 16 characters!" +
                                                    "Please enter a valid password.",
                                            "Messaging program", JOptionPane.PLAIN_MESSAGE);
                                    if (password == null) {
                                        File userInfo = new File("users/" + user + "/" + user);
                                        userInfo.delete();
                                        File userDirectory = new File("users/" + user);
                                        userDirectory.delete();
                                        return;
                                    }
                                }
                            }
                            String passwordToCheck = JOptionPane.showInputDialog(null,
                                    "Please enter your password again to confirm it.",
                                    "Messaging program", JOptionPane.PLAIN_MESSAGE);
                            if (passwordToCheck == null) {
                                File userInfo = new File("users/" + user + "/" + user);
                                userInfo.delete();
                                File userDirectory = new File("users/" + user);
                                userDirectory.delete();
                                return;
                            }
                            if (passwordToCheck.equals(password)) {
                                encryptFile(user, password);
                                done = true;
                            } else {
                                JOptionPane.showMessageDialog(null,
                                        "Passwords did not match! Please try again.",
                                        "Messaging program", JOptionPane.ERROR_MESSAGE);
                            }
                        }
                        done = false;
                        while (!done) {
                            if (isSeller.equalsIgnoreCase("true")) {
                                FileManager.generateDirectoryFromUsername(user, true);
                                boolean doneStores = false;
                                ArrayList<String> storeNames = new ArrayList<>();
                                while (!doneStores) {
                                    String storeName = JOptionPane.showInputDialog(null,
                                            "Please enter your store name.",
                                            "Messaging program", JOptionPane.PLAIN_MESSAGE);
                                    if (storeName == null) {
                                        File userInfo = new File("users/" + user + "/" + user);
                                        userInfo.delete();
                                        File userDirectory = new File("users/" + user);
                                        userDirectory.delete();
                                        return;
                                    }
                                    boolean nameChecked = checkStoreList(storeName);
                                    if (storeName.equals("") || !nameChecked ||
                                            storeName.length() < 4 || storeName.length() > 16) {
                                        while (storeName.equals("") || !nameChecked ||
                                                storeName.length() < 4 || storeName.length() > 16) {
                                            if (nameChecked || storeName.equals("")) {
                                                JOptionPane.showMessageDialog(null,
                                                        "Store name constraints: " +
                                                                "\n- Cannot be blank " +
                                                                "\n- Must be in between 4 and 16 characters inclusive " +
                                                                "\nPlease enter a valid store name.",
                                                        "Messaging program", JOptionPane.ERROR_MESSAGE);
                                            } else {
                                                JOptionPane.showMessageDialog(null,
                                                        "Name already in use! Please enter another name.",
                                                        "Messaging program", JOptionPane.ERROR_MESSAGE);
                                            }
                                            storeName = JOptionPane.showInputDialog(null,
                                                    "Please enter your store name.",
                                                    "Messaging program", JOptionPane.PLAIN_MESSAGE);
                                            if (storeName == null) {
                                                File userInfo = new File("users/" + user + "/" + user);
                                                userInfo.delete();
                                                File userDirectory = new File("users/" + user);
                                                userDirectory.delete();
                                                return;
                                            }
                                            nameChecked = checkStoreList(storeName);
                                        }
                                    }
                                    boolean storeInput = false;
                                    while (!storeInput) {
                                        input = JOptionPane.showConfirmDialog(null,
                                                "Are you sure you want to add this store to your account? " +
                                                        "This action cannot be undone. " +
                                                        "\nEnter 'yes' to confirm or 'no' to abort.", "Messaging program",
                                                JOptionPane.YES_NO_OPTION);
                                        if (input == 0 || input == 1) {
                                            storeInput = true;
                                        }
                                    }
                                    if (input == 0) {
                                        storeNames.add(storeName);
                                        updateStoreList(storeName);
                                        FileManager.generateStoreForSeller(user, storeName);
                                    } else if (input == -1) {
                                        File userInfo = new File("users/" + user + "/" + user);
                                        userInfo.delete();
                                        File userDirectory = new File("users/" + user);
                                        userDirectory.delete();
                                        return;
                                    }
                                    input = -1;
                                    boolean storeInputTaken = false;
                                    while (!storeInputTaken) {
                                        input = JOptionPane.showConfirmDialog(null,
                                                "Would you like to add another store?",
                                                "Messaging program", JOptionPane.YES_NO_OPTION);
                                        if (input == 0 || input == 1) {
                                            storeInputTaken = true;
                                        } else if (input == -1) {
                                            File userInfo = new File("users/" + user + "/" + user);
                                            userInfo.delete();
                                            File userDirectory = new File("users/" + user);
                                            userDirectory.delete();
                                            String stores = getUsersStores(user);
                                            appendStoreList(stores);
                                            return;
                                        }
                                    }
                                    if (input == 1 && !storeNames.isEmpty()) {
                                        doneStores = true;
                                    } else if (input == 1 && storeNames.isEmpty()) {
                                        JOptionPane.showMessageDialog(null,
                                                "Sellers must have at least one store! " +
                                                        "Please add a store before continuing.",
                                                "Messaging program", JOptionPane.ERROR_MESSAGE);
                                    }
                                }
                                writeFile(user, isSeller);
                                writeFile(user, storeNames.toString());
                            } else {
                                FileManager.generateDirectoryFromUsername(user, false);
                                writeFile(user, isSeller);
                            }
                            done = true;
                        }
                        done = false;
                        String email = null;
                        while (!done) {
                            email = JOptionPane.showInputDialog(null,
                                    "Please enter an email to be associated with your account.",
                                    "Messaging program", JOptionPane.PLAIN_MESSAGE);
                            if (email == null) {
                                File userInfo = new File("users/" + user + "/" + user);
                                userInfo.delete();
                                File userDirectory = new File("users/" + user);
                                userDirectory.delete();
                                String stores = getUsersStores(user);
                                appendStoreList(stores);
                                return;
                            }
                            if (email.equals("") || !email.contains("@")) {
                                JOptionPane.showMessageDialog(null,
                                        "Invalid email! Please enter a valid email.",
                                        "Messaging program", JOptionPane.ERROR_MESSAGE);
                            } else {
                                done = true;
                            }
                        }
                        writeFile(user, email);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            boolean isSeller = false;
            if (isSeller(user).equals("true")) {
                isSeller = true;
            } else if (isSeller(user).equals("false")) {
                isSeller = false;
            }
            //TODO MarketUser calls
            //MarketUser currentUser = new MarketUser(user, isSeller);
            boolean running = true;
            boolean userDeleted = false;
            while (running) {
                options[0] = "User Interaction";
                options[1] = "Account Changes";
                input = JOptionPane.showOptionDialog(null,
                        "Please choose an option.",
                        "Messaging program", 0,
                        JOptionPane.PLAIN_MESSAGE, null, options, null);
                if (input == 0) {
                    //currentUser.message();
                } else if (input == 1) {
                    String[] newOptions = new String[4];
                    newOptions[0] = "Edit your name";
                    newOptions[1] = "Delete your account";
                    newOptions[2] = "Change a store name";
                    newOptions[3] = "Exit";
                    input = JOptionPane.showOptionDialog(null,
                            "Please choose an option.",
                            "Messaging program", 0,
                            JOptionPane.PLAIN_MESSAGE, null, newOptions, null);
                    if (input == -1) {
                        return;
                    }
                    if (input == 1) {
                        options[0] = "Yes";
                        options[1] = "No";
                        int deletionInput = JOptionPane.showOptionDialog(null,
                                "Do you really want to delete your account?" +
                                        "This cannot be undone.",
                                "Messaging program", 0,
                                JOptionPane.QUESTION_MESSAGE, null, options, null);
                        if (deletionInput == 0) {
                            String stores = getUsersStores(user);
                            if (stores != null) {
                                appendStoreList(stores);
                            }
                            //TODO MarketUser calls
                            //MarketUser.deleteUsername(user);
                            File userInfo = new File("users/" + user + "/" + user);
                            userInfo.delete();
                            File userDirectory = new File("users/" + user);
                            userDirectory.delete();
                            return;
                        } else if (deletionInput == -1) {
                            return;
                        } else {
                            JOptionPane.showMessageDialog(null,
                                    "We're glad you decided to stay!",
                                    "Messaging program", JOptionPane.PLAIN_MESSAGE);
                        }
                    } else if (input == 0) {
                        String newUser = JOptionPane.showInputDialog(null,
                                "Please enter a new username.",
                                "Messaging program", JOptionPane.PLAIN_MESSAGE);
                        if (newUser == null) {
                            return;
                        }
                        File dir = new File("users/" + newUser);
                        try {
                            done = false;
                            while (!done) {
                                if (user == null) {
                                    dir.delete();
                                    return;
                                }
                                if (!dir.createNewFile()) {
                                    dir.delete();
                                    JOptionPane.showMessageDialog(null,
                                            "Username already exists! Please enter another username.",
                                            "Messaging program", JOptionPane.ERROR_MESSAGE);
                                    newUser = JOptionPane.showInputDialog(null,
                                            "Please enter your new username.",
                                            "Messaging program", JOptionPane.PLAIN_MESSAGE);
                                    if (newUser == null) {
                                        dir.delete();
                                        return;
                                    }
                                    dir = new File("users/" + newUser);
                                    //TODO make sure they can't enter quotations or characters
                                } else if (user.equals("") || user.length() < 6 || user.length() > 16 || user.contains(" ")) {
                                    dir.delete();
                                    JOptionPane.showMessageDialog(null,
                                            "Username constraints: " +
                                                    "\n- Cannot be blank " +
                                                    "\n- Must be in between 6 and 16 characters inclusive " +
                                                    "\n- Cannot contain spaces " +
                                                    "\nPlease enter a valid username.",
                                            "Messaging program", JOptionPane.ERROR_MESSAGE);
                                    newUser = JOptionPane.showInputDialog(null,
                                            "Please enter your new username.",
                                            "Messaging program", JOptionPane.PLAIN_MESSAGE);
                                    if (newUser == null) {
                                        dir.delete();
                                        return;
                                    }
                                    dir = new File("users/" + newUser);
                                } else {
                                    done = true;
                                }
                            }
                            dir.delete();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        try {
                            //this method was retrieved with help from StackOverflow user @kr37
                            Path source = Paths.get("users/" + user + "/" + user);
                            Files.move(source, source.resolveSibling(newUser));
                            source = Paths.get("users/" + user);
                            Files.move(source, source.resolveSibling(newUser));
                            //MarketUser.changeUsername(user, newUser);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        try (BufferedReader br = new BufferedReader(new FileReader("users/" + newUser + "/" + newUser))) {
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
                            JOptionPane.showMessageDialog(null,
                                    "Name change successful! " +
                                            "Enjoy your new username, " + newUser + "!",
                                    "Messaging program", JOptionPane.PLAIN_MESSAGE);
                            user = newUser;
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else if (input == 2) {
                        if (getUsersStores(user) != null) {
                            String stores = getUsersStores(user);
                            List<String> storesArray = Arrays.asList(stores.split(", "));
                            String[] storeOptions = new String[storesArray.size()];
                            int index = 0;
                            for (String s : storesArray) {
                                storeOptions[index] = s;
                                index++;
                            }
                            String storeToChange = null;
                            try {
                                storeToChange = (String) JOptionPane.showInputDialog(null,
                                        "Please choose a store to change.",
                                        "Messaging program",
                                        JOptionPane.PLAIN_MESSAGE, null, storeOptions, null);
                                if (storeToChange == null) {
                                    return;
                                }
                                String storeName = JOptionPane.showInputDialog(null,
                                        "Please enter your new store name.",
                                        "Messaging program", JOptionPane.PLAIN_MESSAGE);
                                if (storeName == null) {
                                    return;
                                }
                                boolean nameChecked = checkStoreList(storeName);
                                if (storeName.equals("") || !nameChecked ||
                                        storeName.length() < 4 || storeName.length() > 16) {
                                    while (storeName.equals("") || !nameChecked ||
                                            storeName.length() < 4 || storeName.length() > 16) {
                                        if (nameChecked || storeName.equals("")) {
                                            JOptionPane.showMessageDialog(null,
                                                    "Store name constraints: " +
                                                            "\n- Cannot be blank " +
                                                            "\n- Must be in between 4 and 16 characters inclusive " +
                                                            "\nPlease enter a valid store name.",
                                                    "Messaging program", JOptionPane.ERROR_MESSAGE);
                                        } else {
                                            JOptionPane.showMessageDialog(null,
                                                    "Name already in use! Please enter another name.",
                                                    "Messaging program", JOptionPane.ERROR_MESSAGE);
                                        }
                                        storeName = JOptionPane.showInputDialog(null,
                                                "Please enter your store name.",
                                                "Messaging program", JOptionPane.PLAIN_MESSAGE);
                                        if (storeName == null) {
                                            return;
                                        }
                                        nameChecked = checkStoreList(storeName);
                                        removeRenamedStore(storeToChange, storeName);
                                    }
                                }
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
                                    JOptionPane.showMessageDialog(null,
                                            "Name change successful!",
                                            "Messaging program", JOptionPane.PLAIN_MESSAGE);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            JOptionPane.showMessageDialog(null,
                                    "You are not a seller!",
                                    "Messaging program", JOptionPane.ERROR_MESSAGE);
                        }
                    } else {
                        return;
                    }
                }
                input = JOptionPane.showConfirmDialog(null,
                        "Would you like to continue using the program?",
                        "Messaging program", JOptionPane.YES_NO_OPTION);
                if (input != 0) {
                    return;
                }
            }
        } finally {
            JOptionPane.showMessageDialog(null,
                    "Goodbye!",
                    "Messaging program", JOptionPane.PLAIN_MESSAGE);
        }
    }
}

