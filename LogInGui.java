import javax.swing.*;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LogInGui {

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
            writer.println("writeFile;" + user + ";" + finalPassword);
            writer.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //checks either store names or usernames to see if they're valid
    public static String checkName(String name, boolean isStore, String user) {
        if (name == null) {
            return (null);
        }
        //if the name is store name, check to see if it's in use
        if (isStore) {
            writer.println("checkStoreList;" + name);
            writer.flush();
            String result = "";
            try {
                result = reader.readLine();
            } catch (Exception e) {
                e.printStackTrace();
            }
            boolean nameChecked;
            if (result.equals("true")) {
                nameChecked = true;
            } else {
                nameChecked = false;
            }
            if (name.equals("") || name.length() < 4
                    || name.length() > 16) {
                return ("invalid");
            }
            if (!nameChecked) {
                return ("inUse");
            }
            if (name == null) {
                deleteUserInProgress(user);
                return (null);
            }
        } else {
            writer.println("checkUsername;" + user);
            writer.flush();
            String result = "";
            try {
                result = reader.readLine();
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (result.equals("inUse") || result.equals("invalid")) {
                return (result);
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
        writer.println("deleteUserInProgress;" + user);
        writer.flush();
    }

    public static String checkPassword(String password, String user) {
        if (password == null) {
            deleteUserInProgress(user);
            return (null);
        }
        if (password.length() < 8 || password.length() > 16
                || password.contains(";")) {
            return ("invalid");
        }
        return ("valid");
    }

    public static void changeStoreName(List<String> storesArray, String storeToChange, String storeName, String user) {
        storesArray.set(storesArray.indexOf(storeToChange), storeName);
        writer.println("changeStoreName;" + storesArray + ";" + user);
        writer.flush();
        writer.println("removeRenamedStore;" + storeToChange + ";" + storeName);
        writer.flush();
        writer.println("changeStoreName;" + storeToChange +  ";" + storeName);
        writer.flush();
        writer.println("changeStoreName;" + storeToChange + ";" + storeName);
        writer.flush();
    }

    private static BufferedReader reader;

    private static PrintWriter writer;

    public LogInGui(Socket socket) {
        try {
            this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.writer = new PrintWriter(socket.getOutputStream());
        } catch (IOException io) {
            io.printStackTrace();
        }

    }

    public static void main(String[] args) {
        try {
            Socket s = new Socket("localhost", 2000);
            LogInGui runLogIn = new LogInGui(s);
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
                        writer.println("checkUserExists;" + user);
                        writer.flush();
                        boolean checkUser = Boolean.parseBoolean(reader.readLine());
                        if (!checkUser) {
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
                            writer.println("readPassword;" + user);
                            writer.flush();
                            String password = reader.readLine();
                            if (encrypt(passwordInput).equals(password)) {
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
                    //if the user hits the x
                    if (user == null) {
                        return;
                    }
                    try {
                        String nameCheck = checkName(user, false, user);
                        while (!nameCheck.equals("valid")) {
                            if (nameCheck == null) {
                                return;
                            }
                            if (nameCheck.equals("inUse")) {
                                JOptionPane.showMessageDialog(null,
                                        "Username already exists! Please enter another username.",
                                        "Messaging program", JOptionPane.ERROR_MESSAGE);
                                user = JOptionPane.showInputDialog(null,
                                        "Enter your username.",
                                        "Messaging program", JOptionPane.PLAIN_MESSAGE);
                            } else if (nameCheck.equals("invalid")) {
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
                            }
                            nameCheck = checkName(user, false, user);
                        }
                        writer.println("createUser;" + user);
                        String password = JOptionPane.showInputDialog(null,
                                "Please enter a password between 8 and 16 characters.",
                                "Messaging program", JOptionPane.PLAIN_MESSAGE);
                        String passwordCheck = checkPassword(password, user);
                        try {
                            //checking the password
                            boolean passwordDone = false;
                            while (!passwordDone) {
                                while (!passwordCheck.equals("valid")) {
                                    if (passwordCheck == null) {
                                        deleteUserInProgress(user);
                                        return;
                                    }
                                    if (passwordCheck.equals("invalid")) {
                                        password = JOptionPane.showInputDialog(null,
                                                "Password length must be between 8 and 16 characters\n" +
                                                        "Password may not contain a ';'\n" +
                                                        "Please enter a valid password.",
                                                "Messaging program", JOptionPane.PLAIN_MESSAGE);
                                    }
                                    if (passwordCheck.equals("tryAgain")) {
                                        password = JOptionPane.showInputDialog(null,
                                                "Please enter a password between 8 and 16 characters.",
                                                "Messaging program", JOptionPane.PLAIN_MESSAGE);
                                    }
                                    passwordCheck = checkPassword(password, user);
                                }
                                String passwordToCheck = JOptionPane.showInputDialog(null,
                                        "Please confirm your password.",
                                        "Messaging program", JOptionPane.PLAIN_MESSAGE);
                                if (passwordToCheck.equals(password)) {
                                    passwordDone = true;
                                } else {
                                    JOptionPane.showMessageDialog(null,
                                            "Passwords do not match! Please try again.",
                                            "Messaging Program", JOptionPane.ERROR_MESSAGE);
                                    passwordCheck = "tryAgain";
                                }
                            }
                            encryptFile(user, password);
                            done = false;
                            while (!done) {
                                if (isSeller.equalsIgnoreCase("true")) {
                                    //TODO FileManager calls
                                    //FileManager.generateDirectoryFromUsername(user, true);
                                    boolean doneStores = false;
                                    ArrayList<String> storeNames = new ArrayList<>();
                                    String storeName = "";
                                    while (!doneStores) {
                                        boolean storeCaptured = false;
                                        while (!storeCaptured) {
                                            storeName = JOptionPane.showInputDialog(null,
                                                    "Please enter your store name.",
                                                    "Messaging program", JOptionPane.PLAIN_MESSAGE);
                                            String storeStatus = checkName(storeName, true, user);
                                            if (storeStatus == null) {
                                                deleteUserInProgress(user);
                                                return;
                                            } else if (storeStatus.equals("inUse")) {
                                                JOptionPane.showMessageDialog(null,
                                                        "Name already in use! Please enter another name.",
                                                        "Messaging program", JOptionPane.ERROR_MESSAGE);
                                            } else if (storeStatus.equals("invalid")) {
                                                JOptionPane.showMessageDialog(null,
                                                        "Store name constraints: " +
                                                                "\n- Cannot be blank " +
                                                                "\n- Must be in between 4 and 16 characters inclusive " +
                                                                "\n- Must not include symbols " +
                                                                "\nPlease enter a valid store name.",
                                                        "Messaging program", JOptionPane.ERROR_MESSAGE);
                                            } else {
                                                storeCaptured = true;
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
                                            } else {
                                                return;
                                            }
                                        }
                                        if (input == 0) {
                                            storeNames.add(storeName);
                                            writer.println("updateStoreList;" + storeName);
                                            writer.flush();
                                            //TODO FileManager calls
                                            //FileManager.generateStoreForSeller(user, storeName);
                                        } else if (input == -1) {
                                            deleteUserInProgress(user);
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
                                                deleteUserInProgress(user);
                                                writer.println("getUsersStores;" + user);
                                                writer.flush();
                                                String stores = reader.readLine();
                                                writer.println("appendStoreList;" + stores);
                                                writer.flush();
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
                                    writer.println("writeFile;" + user + ";" + isSeller);
                                    writer.flush();
                                    writer.println("writeFile;" + user + ";" + storeNames);
                                    writer.flush();
                                } else {
                                    //TODO FileManager calls
                                    //FileManager.generateDirectoryFromUsername(user, false);
                                    writer.println("writeFile;" + user + ";" + isSeller);
                                    writer.flush();
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
                                    deleteUserInProgress(user);
                                    writer.println("getUsersStores;" + user);
                                    writer.flush();
                                    String stores = reader.readLine();
                                    writer.println("appendStoreList;" + stores);
                                    writer.flush();
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
                            writer.println("writeFile;" + user + ";" + email);
                            writer.flush();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                writer.println("isSeller;" + user);
                writer.flush();
                boolean isSeller = Boolean.parseBoolean(reader.readLine());
                boolean running = true;
                while (running) {
                    options[0] = "User Interaction";
                    options[1] = "Account Changes";
                    input = JOptionPane.showOptionDialog(null,
                            "Please choose an option.",
                            "Messaging program", 0,
                            JOptionPane.PLAIN_MESSAGE, null, options, null);
                    if (input == 0) {
                        SwingUtilities.invokeLater(new MessageGui(user, isSeller, s));
                        return;
                    } else if (input == 1) {
                        String[] newOptions = new String[4];
                        newOptions[0] = "Edit your name";
                        newOptions[1] = "Delete your account";
                        newOptions[2] = "Change a store name";
                        newOptions[3] = "Exit to main menu";
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
                                    "Do you really want to delete your account? " +
                                            "This cannot be undone.",
                                    "Messaging program", 0,
                                    JOptionPane.QUESTION_MESSAGE, null, options, null);
                            if (deletionInput == 0) {
                                writer.println("getUsersStores;" + user);
                                writer.flush();
                                String stores = reader.readLine();
                                if (stores != null) {
                                    writer.println("appendStoreList;" + stores);
                                    writer.flush();
                                }
                                writer.println("deleteUsername;" + user);
                                writer.flush();
                                deleteUserInProgress(user);
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
                            String newNameStatus = "";
                            //user hits the x
                            if (newUser == null) {
                                return;
                            } else {
                                newNameStatus = checkName(user, false, newUser);
                            }
                            while (!newNameStatus.equals("valid")) {
                                if (newNameStatus.equals("inUse")) {
                                    JOptionPane.showMessageDialog(null,
                                            "Username already exists! Please enter another username.",
                                            "Messaging program", JOptionPane.ERROR_MESSAGE);
                                } else if (newNameStatus.equals("invalid")) {
                                    JOptionPane.showMessageDialog(null,
                                            "Username constraints: " +
                                                    "\n- Cannot be blank " +
                                                    "\n- Must be in between 6 and 16 characters inclusive " +
                                                    "\n- Cannot contain spaces " +
                                                    "\nPlease enter a valid username.",
                                            "Messaging program", JOptionPane.ERROR_MESSAGE);
                                }
                                newUser = JOptionPane.showInputDialog(null,
                                        "Please enter your new username.",
                                        "Messaging program", JOptionPane.PLAIN_MESSAGE);
                                if (newUser == null) {
                                    return;
                                }
                                newNameStatus = checkName(user, false, newUser);
                            }
                            writer.println("moveUsername;" + user + ";" + newUser);
                            writer.flush();
                            writer.println("changeUsername;" + user + ";" + newUser);
                            writer.flush();
                            JOptionPane.showMessageDialog(null,
                                    "Name change successful! " +
                                            "Enjoy your new username, " + newUser + "!",
                                    "Messaging program", JOptionPane.PLAIN_MESSAGE);
                            user = newUser;
                        } else if (input == 2) {
                            writer.println("getUsersStores;" + user);
                            writer.flush();
                            String stores = reader.readLine();
                            if (!stores.equals("null")) {
                                List<String> storesArray = Arrays.asList(stores.split(", "));
                                String[] storeOptions = new String[storesArray.size()];
                                int index = 0;
                                for (String string : storesArray) {
                                    storeOptions[index] = string;
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
                                    String storeStatus = checkName(storeName, true, user);
                                    while (!storeStatus.equals("valid")) {
                                        if (storeStatus.equals("invalid")) {
                                            JOptionPane.showMessageDialog(null,
                                                    "Store name constraints: " +
                                                            "\n- Cannot be blank " +
                                                            "\n- Must be in between 4 and 16 characters inclusive " +
                                                            "\n- Must not include symbols " +
                                                            "\nPlease enter a valid store name.",
                                                    "Messaging program", JOptionPane.ERROR_MESSAGE);
                                        } else if (storeStatus.equals("inUse")) {
                                            JOptionPane.showMessageDialog(null,
                                                    "Name already in use! Please enter another name.",
                                                    "Messaging program", JOptionPane.ERROR_MESSAGE);
                                        }
                                        storeName = JOptionPane.showInputDialog(null,
                                                "Please enter your new store name.",
                                                "Messaging program", JOptionPane.PLAIN_MESSAGE);
                                        if (storeName == null) {
                                            return;
                                        }
                                        storeStatus = checkName(storeName, true, user);
                                    }
                                    changeStoreName(storesArray, storeToChange, storeName, user);
                                    JOptionPane.showMessageDialog(null,
                                            "Name change successful!",
                                            "Messaging program", JOptionPane.PLAIN_MESSAGE);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            } else {
                                JOptionPane.showMessageDialog(null,
                                        "You are not a seller!",
                                        "Messaging program", JOptionPane.ERROR_MESSAGE);
                            }
                        }
                    } else {
                        return;
                    }
                }
            } finally {
                JOptionPane.showMessageDialog(null,
                        "Goodbye!",
                        "Messaging program", JOptionPane.PLAIN_MESSAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}