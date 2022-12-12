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
    public void sendMessage() {
        //System.out.println(name);
    }
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

    public void importFile(String path, String recipient, String username, boolean isSeller,
                           boolean isUserStore, boolean isRecipientStore) {
        writer.println("importFile;" + path + ";" + recipient + ";" + username + ";" + isSeller + ";" +
                isUserStore + ";" + isRecipientStore);
        writer.flush();
    }

    public void exportFile(String recipient, String username, boolean isSeller, boolean isUserStore,
                           String path) {
        writer.println("exportFile;" + recipient + ";" + username + ";" + isSeller + ";" + isUserStore
                + ";" + path);
        writer.flush();
    }

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

    public void sendBlockInvisibleSignal(String command, String currentUser, String isSeller, String victim ) {
        writer.println(command + ";" + currentUser + ";" + isSeller + ";" + victim);
        writer.flush();
    }

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

    public ArrayList<String> getFilteringList(String username) {
        writer.println("getFilteringList;" + username);
        writer.flush();
        try {
            return new ArrayList<>(Arrays.asList((reader.readLine().split(";"))));
        } catch(IOException e) {
            return new ArrayList<String>();
        }
    }

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

    public ArrayList<String> getCensoredList(String username) {
        writer.println("getCensoredList;" + username);
        writer.flush();
        try {
            return new ArrayList<>(Arrays.asList((reader.readLine().split(";"))));
        } catch (IOException e) {
            return new ArrayList<>();
        }
    }

   public ArrayList<String[]> parseMetricData() {
        try {
            ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
            ArrayList<String[]> recievedData = (ArrayList<String[]>) objectInputStream.readObject();
            return recievedData;
        } catch (Exception e) {
            e.printStackTrace();
        }
        throw new RuntimeException("An error occurred parsing metric data");
    }

}
