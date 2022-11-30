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

//Creates socket, connects to server, communicate with server, call methods there

public class LogInGui {

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
                    boolean checkUser = checkUserExists(user);
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
                    createUser(user);
                    String password = JOptionPane.showInputDialog(null,
                            "Please enter a password between 8 and 16 characters.",
                            "Messaging program", JOptionPane.PLAIN_MESSAGE);
                    String passwordCheck = checkPassword(password, user);
                    try {
                        //checking the password
                        while (!passwordCheck.equals("valid")) {
                            if (passwordCheck == null) {
                                return;
                            }
                            if (passwordCheck.equals("invalid")) {
                                password = JOptionPane.showInputDialog(null,
                                        "Password length must be between 8 and 16 characters" +
                                                "Password may not contain a ';'" +
                                                "Please enter a valid password.",
                                        "Messaging program", JOptionPane.PLAIN_MESSAGE);
                            }
                            passwordCheck = checkPassword(password, user);
                        }
                        //TODO do this if time
                        /* String passwordToCheck = JOptionPane.showInputDialog(null,
                                "Please enter your password again to confirm it.",
                                "Messaging program", JOptionPane.PLAIN_MESSAGE); */
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
                                        updateStoreList(storeName);
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
                                //TODO FileManager calls
                                //FileManager.generateDirectoryFromUsername(user, false);
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
                                deleteUserInProgress(user);
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
            //boolean userDeleted = false;
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
                        moveUsername(user, newUser);
                        //TODO MarketUser calls
                        //MarketUser.changeUsername(user, newUser);
                        JOptionPane.showMessageDialog(null,
                                "Name change successful! " +
                                        "Enjoy your new username, " + newUser + "!",
                                "Messaging program", JOptionPane.PLAIN_MESSAGE);
                        user = newUser;
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
                                String storeStatus = checkName(storeName, true, user);
                                while (!storeStatus.equals("valid")) {
                                    if (storeStatus.equals("invalid")) {
                                        JOptionPane.showMessageDialog(null,
                                                "Store name constraints: " +
                                                        "\n- Cannot be blank " +
                                                        "\n- Must be in between 4 and 16 characters inclusive " +
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
                                //TODO I don't think this is needed
                                //removeRenamedStore(storeToChange, storeName);
                                changeStoreName(storesArray, storeToChange, storeName, user);
                                JOptionPane.showMessageDialog(null,
                                        "Name change successful!",
                                        "Messaging program", JOptionPane.PLAIN_MESSAGE);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
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
        } finally {
            JOptionPane.showMessageDialog(null,
                    "Goodbye!",
                    "Messaging program", JOptionPane.PLAIN_MESSAGE);
        }
    }
}