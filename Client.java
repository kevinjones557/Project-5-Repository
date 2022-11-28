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

    public Client (String name) {
        this.name = name;

        try {
            Socket socket = new Socket("localhost", 2000); // Server's port is 2000

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
}
