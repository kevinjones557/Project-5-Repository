import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

public class LogInGui {

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
        String[] options = new String[2];
        options[0] = "Log in";
        options[1] = "Sign up";
        int input = JOptionPane.showOptionDialog(null,
                "Welcome! Would you like to log in or sign up?",
                "Messaging program", 0,
                JOptionPane.QUESTION_MESSAGE, null, options, null);
        if (input == 0) {
            //user logs in
        } else if (input == 1) {
            //user makes new account
            options[0] = "Buyer";
            options[1] = "Seller";
            input = JOptionPane.showOptionDialog(null,
                    "Please choose your account role.",
                    "Messaging program", 0,
                    JOptionPane.QUESTION_MESSAGE, null, options, null);
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
            String user = JOptionPane.showInputDialog(null,
                    "Enter your username.",
                    "Messaging program", JOptionPane.PLAIN_MESSAGE);
            File f;
            File dir = new File("users/" + user);
            try {
                boolean done = false;
                while (!done) {
                    if (!dir.createNewFile()) {
                        dir.delete();
                        JOptionPane.showMessageDialog(null,
                                "Username already exists! Please enter another username.",
                                "Messaging program", JOptionPane.ERROR_MESSAGE);
                        user = JOptionPane.showInputDialog(null,
                                "Enter your username.",
                                "Messaging program", JOptionPane.PLAIN_MESSAGE);
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
                        if (password.length() < 8 || password.length() > 16) {
                            while (password.length() < 8 || password.length() > 16) {
                                password = JOptionPane.showInputDialog(null,
                                        "Password length must be between 8 and 16 characters! " +
                                                "Please enter a valid password.",
                                        "Messaging program", JOptionPane.PLAIN_MESSAGE);
                            }
                        }
                        String passwordToCheck = JOptionPane.showInputDialog(null,
                                "Please enter your password again to confirm it.",
                                "Messaging program", JOptionPane.PLAIN_MESSAGE);
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
                                }
                                input = -1;
                                boolean storeInputTaken = false;
                                while (!storeInputTaken) {
                                    input = JOptionPane.showConfirmDialog(null,
                                            "Would you like to add another store?",
                                            "Messaging program", JOptionPane.YES_NO_OPTION);
                                    if (input == 0 || input == 1) {
                                        storeInputTaken = true;
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
            }
        } else {
            //user hits exit
            return;
        }
    }
}

