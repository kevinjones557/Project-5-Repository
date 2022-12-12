import javax.swing.*;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * This is a class that sets up the client for each messenger, it is the parent class of the GUI and its functions
 * will be called from the GUI to send and recieve data to and from the server and hand it back to the GUI
 * Its methods will be called by the MessageGUI and will in tern send information to and form server
 *
 * @author Kevin Jones
 * @version 11/20
 */
public class Client {
    private BufferedReader reader;
    private PrintWriter writer;

    private final Socket socket;

    /**
     * a constructor to create the client and set up input/output streams
     * @param socket
     */
    public Client (Socket socket) {
        try {
            this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.writer = new PrintWriter(socket.getOutputStream());
            writer.println("deleteUser;alphabet");
            writer.flush();
        } catch (IOException io) {
            io.printStackTrace();
        }
        this.socket = socket;

    }

    /**
     * a method that writes to the server and returns a seller if given a store
     * @param store
     * @return
     */
    public String getSellerFromStore(String store) {
        try {
            writer.println("GetSeller;" + store);
            writer.flush();
            return reader.readLine();
        } catch (IOException io) {
            io.printStackTrace();
        }
        return null;
    }

    /**
     * a method that writes to the server and returns an arrayList of all stores that a seller has
     * @param seller
     * @return
     */
    public ArrayList<String> getStoresFromSeller(String seller) {
        try {
            writer.println("getStoresFromSellers;" + seller);
            writer.flush();
            return new ArrayList<>(Arrays.asList((reader.readLine().split(";"))));
        } catch (IOException io) {

            io.printStackTrace();
        }
        return null;
    }

    /**
     * a method that returns a boolean according to if a recipient is a store
     * @param storeName
     * @return
     */
    public boolean isRecipientStore(String storeName) {
        try {
            writer.println("isRecipientStore;" + storeName);
            writer.flush();
            return Boolean.parseBoolean(reader.readLine());
        } catch (IOException io) {
            io.printStackTrace();
        }
        return false;
    }

    /**
     * a method that writes to the server and gets returned an arraylist of metrics data
     * sorted according to the index
     * @param username
     * @param index
     * @param isSeller
     * @return
     */
    public ArrayList<String[]> sortMetricsData(String username, int index, boolean isSeller) {
        writer.println("sortMetrics;" + username + ";" + index + ";" + isSeller);
        writer.flush();
        try {
            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
            return (ArrayList<String[]>) ois.readObject();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * a method that writes to server and gets most common words
     * for the seller statistics GUI
     * @param sellerName
     * @param index
     * @return
     */
    public String[] getMostCommonWords(String sellerName, int index) {
        writer.println("getMostCommonWords;" + sellerName + ";" + index);
        writer.flush();
        try {
            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
            return (String[]) ois.readObject();
        } catch (Exception e) {
            e.printStackTrace();
            return new String[0];
        }
    }

    /**
     * a method to write to server to see if a message exists
     * if not it creates those files in server
     * @param recipient
     * @param isRecipientStore
     * @param isSeller
     * @param username
     * @param isUserStore
     */
    public void checkIfMessageExists(String recipient, boolean isRecipientStore, boolean isSeller,
                                     String username, boolean isUserStore) {
        writer.println("CheckIfMessageExists;" + recipient + ";" + isRecipientStore + ";" + isSeller + ";" +
                username + ";" + isUserStore);
        writer.flush();
    }

    /**
     * Method to signal appending or deleting to server and provide necessary info to do so
     *
     * @param delete if deleting
     * @param sender sender
     * @param recipient recipient
     * @param storeName store name, "nil" if none
     * @param isBuyer if buyer
     * @param message message to be either added or deleted
     *
     * @author John Brooks
     */
    public void appendOrDeleteSignal(boolean delete, String sender, String recipient, String storeName,
                                     boolean isBuyer, String message){
        String buyer = (isBuyer)? "true" : "false";

        String sendData = "delete";
        if (!delete)
            sendData = "append";

        String personData = sender + "," + recipient + "," +
                storeName + "," + buyer;

        writer.write(sendData);
        writer.println();
        writer.flush();

        writer.write(personData);
        writer.println();
        writer.flush();

        writer.write(message);
        writer.println();
        writer.flush();

    }

    /**
     * Method to signal to server for an edit and provide necessary info
     *
     * @param delete if deleting
     * @param sender sender
     * @param recipient recipient
     * @param storeName store name, "nil" if none
     * @param isBuyer if buyer
     * @param messageToEdit message to be edited
     * @param edit edit made
     *
     * @author John Brooks
     */
    public void editSignal(boolean delete, String sender, String recipient, String storeName,
                           boolean isBuyer, String messageToEdit, String edit) {
        String buyer = "false";
        if(isBuyer) {
            buyer = "true";
        }

        String sendData = "edit";

        String personData = sender + "," + recipient + "," +
                storeName + "," + buyer;

        writer.write(sendData);
        writer.println();
        writer.flush();

        writer.write(personData);
        writer.println();
        writer.flush();

        writer.write(messageToEdit);
        writer.println();
        writer.flush();

        writer.write(edit);
        writer.println();
        writer.flush();
    }

    /**
     * Method to signal retrieval of message contents and return the servers response as an
     * array list
     *
     * @param sender sender
     * @param recipient recipient
     * @param storeName storeName
     * @param isBuyer if buyer
     * @return array list of messages
     *
     * @author John Brooks
     */
    public ArrayList<String> displaySignal(String sender, String recipient, String storeName,
                                           boolean isBuyer) {
        String buyer = Boolean.toString(isBuyer);

        String sendData = "display";

        String personData = sender + "," + recipient + "," +
                storeName + "," + buyer;

        writer.write(sendData);
        writer.println();
        writer.flush();

        writer.write(personData);
        writer.println();
        writer.flush();

        try {
            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
            return (ArrayList<String>) ois.readObject();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Failed to retrieve message contents.", "Messaging System",
                    JOptionPane.ERROR_MESSAGE);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        return null;

    }

    /**
     * a method that writes data to the server to import a file
     * @param path
     * @param recipient
     * @param username
     * @param isSeller
     * @param isUserStore
     * @param isRecipientStore
     */
    public void importFile(String path, String recipient, String username, boolean isSeller,
                           boolean isUserStore, boolean isRecipientStore) {
        writer.println("importFile;" + path + ";" + recipient + ";" + username + ";" + isSeller + ";" +
                isUserStore + ";" + isRecipientStore);
        writer.flush();
    }

    /**
     * a method that writes data to a server to export a file
     * @param recipient
     * @param username
     * @param isSeller
     * @param isUserStore
     * @param path
     */
    public void exportFile(String recipient, String username, boolean isSeller, boolean isUserStore,
                           String path) {
        writer.println("exportFile;" + recipient + ";" + username + ";" + isSeller + ";" + isUserStore
                + ";" + path);
        writer.flush();
    }

    /**
     * a method that writes to server and returns an arrayList of
     * all the conversations from a store
     * @param seller
     * @param storeName
     * @return
     */
    public ArrayList<String> getConversationsFromStore(String seller, String storeName) {
        writer.println("getConversationsFromStore;" + seller + ";" + storeName);
        writer.flush();
        try {
            return new ArrayList<>(Arrays.asList((reader.readLine().split(";"))));
        } catch (IOException io) {
            io.printStackTrace();
        }
        return null;
    }

    /**
     * a method that writes to a server and returns an arrayList of all conversations
     * from a buyer or seller
     * @param username
     * @return
     */
    public ArrayList<String> getConversationsFromUser(String username) {
        writer.println("getConversationsFromUser;" + username);
        writer.flush();
        try {
            return new ArrayList<>(Arrays.asList((reader.readLine().split(";"))));
        } catch (IOException io) {
            io.printStackTrace();
        }
        return null;
    }

    /**
     * a method that sends data to server to block or make invisible
     * @param command
     * @param currentUser
     * @param isSeller
     * @param victim
     */
    public void sendBlockInvisibleSignal(String command, String currentUser, String isSeller, String victim ) {
        writer.println(command + ";" + currentUser + ";" + isSeller + ";" + victim);
        writer.flush();
    }

    /**
     * a method to write to the server to see if given user is blocked or invisible
     * @param option
     * @param currentUser
     * @param isSeller
     * @param isStore
     * @param recipient
     * @return
     */
    public boolean isBlockedOrCannotSee(int option, String currentUser, String isSeller, String isStore,
                                        String recipient) {
        String[] options = {"isRecipientBlocked", "recipientCantSeeMe"};
        writer.println(options[option] + ";" + currentUser + ";" + isSeller + ";" + isStore + ";" + recipient);
        writer.flush();
        try {
            return Boolean.parseBoolean(reader.readLine());
        } catch(IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * a method to write to the server to get an ArrayList of
     * either messagable or available stores or sellers
     * @param command
     * @param currentUser
     * @param isSeller
     * @return
     */
    public ArrayList<String> getUsersSignal(int command, String currentUser, boolean isSeller) {
        String[] commands = {"getAvailableUsers", "getMessageAbleUsers", "getAvailableStores", "getMessageAbleStores"};
        writer.println(commands[command] + ";" + currentUser + ";" + isSeller);
        writer.flush();
        try {
            return new ArrayList<>(Arrays.asList((reader.readLine().split(";"))));
        } catch(IOException e) {
            return new ArrayList<String>();
        }

    }

    /**
     * a method to get the filtering list
     * @param username
     * @return
     */
    public ArrayList<String> getFilteringList(String username) {
        writer.println("getFilteringList;" + username);
        writer.flush();
        try {
            return new ArrayList<>(Arrays.asList((reader.readLine().split(";"))));
        } catch(IOException e) {
            return new ArrayList<String>();
        }
    }

    /**
     * a method that writes to teh sever and replaces a filtered word
     * @param option
     * @param username
     * @param censoredWord
     * @param replacement
     * @return
     */
    public boolean filteringSignal(int option, String username, String censoredWord, String replacement) {
        String[] options = {"addFilter", "deleteFilter", "editFilter"};
        writer.println(options[option] + ";" + username + ";" + censoredWord + ";" + replacement);
        writer.flush();
        try {
            return Boolean.parseBoolean(reader.readLine());
        } catch (IOException e) {
            return true;
        }
    }

    /**
     * a method that writes to server and gets a censored list
     * @param username
     * @return
     */
    public ArrayList<String> getCensoredList(String username) {
        writer.println("getCensoredList;" + username);
        writer.flush();
        try {
            return new ArrayList<>(Arrays.asList((reader.readLine().split(";"))));
        } catch (IOException e) {
            return new ArrayList<>();
        }
    }

    /**
     * a method to generate message file
     * @param command
     * @param info
     */
    public void generateMessageFile(String command, String info) {
        writer.println(command + ";" + info);
        writer.flush();
    }
}
