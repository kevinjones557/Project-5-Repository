import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

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

    public void checkIfMessageExists(String recipient, boolean isRecipientStore, boolean isSeller,
                                     String username, boolean isUserStore) {
        writer.println("CheckIfMessageExists;" + recipient + ";" + isRecipientStore + ";" + isSeller + ";" +
                username + ";" + isUserStore);
        writer.flush();
    }

    /**
     * Method to signal to the server for an append or delete
     *
     * @param delete true if delete, false if append
     * @param sender sender
     * @param recipient recipient
     * @param storeName storeName is "nil" if not involved
     * @param isBuyer if buyer
     * @param message message to append, or message to delete
     * @param writer writer being used throughout client
     *
     * @author John Brooks
     */
    public static void appendOrDeleteSignal(boolean delete, String sender, String recipient, String storeName,
                                    boolean isBuyer, String message, PrintWriter writer){
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

        writer.write(message);
        writer.println();
        writer.flush();

    }

    /**
     * Method to signal server to make edit
     *
     * @param sender sender
     * @param recipient recipient
     * @param storeName storeName, "nil" if none
     * @param isBuyer if buyer
     * @param messageToEdit message to be changed
     * @param edit changed message
     * @param writer writer already being used
     *
     * @author John Brooks
     */
    public static void editSignal(String sender, String recipient, String storeName,
                                  boolean isBuyer, String messageToEdit, String edit, PrintWriter writer) {
        String buyer = "false";
        if(isBuyer)
            buyer = "true";

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
}
