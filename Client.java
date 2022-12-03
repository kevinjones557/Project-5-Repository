import javax.swing.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * This is a class that sets up the client for each messenger, it is the parent class of the GUI and its functions
 * will be called from the GUI to send and recieve data to and from the server and hand it back to the GUI
 *
 * Its methods will be called by the MessageGUI and will in tern send information to and form server
 *
 * @author Kevin Jones
 * @version 11/20
 */
public class Client {
    private String name;
    private BufferedReader reader;
    private PrintWriter writer;

    public Client (String name, Socket socket) {
        this.name = name;
        try {
            this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.writer = new PrintWriter(socket.getOutputStream());
        } catch (IOException io) {
            io.printStackTrace();
        }

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

    public void checkIfMessageExists(String recipient, boolean isRecipientStore, boolean isSeller,
                                     String username, boolean isUserStore) {
        writer.println("CheckIfMessageExists;" + recipient + ";" + isRecipientStore + ";" + isSeller + ";" +
                username + ";" + isUserStore);
        writer.flush();
    }


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

    public void editSignal(boolean delete, String sender, String recipient, String storeName,
                                  boolean isBuyer, String messageToEdit, String edit) {
        String buyer = "false";
        if(isBuyer)
            buyer = "true";

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
        String buyer = "false";
        if(isBuyer)
            buyer = "true";

        String sendData = "display";

        String personData = sender + "," + recipient + "," +
                storeName + "," + buyer;

        writer.write(sendData);
        writer.println();
        writer.flush();

        writer.write(personData);
        writer.println();
        writer.flush();

        String messages = "";
        try {
            messages = reader.readLine();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Failed to retrieve message contents.", "Messaging System",
                    JOptionPane.ERROR_MESSAGE);
        }

        ArrayList<String> messageContents = new ArrayList<>();

        int indexOfSeparator = messages.indexOf(": : : :");

        while (indexOfSeparator != -1) {
            messageContents.add(messages.substring(0, indexOfSeparator));
            indexOfSeparator = messages.indexOf(": : : :");
            messages = messages.substring(indexOfSeparator + 7);
        }

        return messageContents;
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

    public ArrayList<String> getUsersSignal(int command, String currentUser, String isSeller) {
        String[] commands = {"getAvailableUsers", "getMessageAbleUsers", "getAvailableStores", "getMessageAbleStores"};
        writer.println(commands[command] + ";" + currentUser + ";" + isSeller);
        writer.flush();
        try {
            return new ArrayList<>(Arrays.asList((reader.readLine().split(";"))));
        } catch(IOException e) {
            return new ArrayList<String>();
        }

    }
}
