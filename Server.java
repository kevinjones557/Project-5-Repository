import javax.swing.*;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.LinkedHashMap;

/**
 * This will be a class that sets up the server for the messaging system
 * It will read data from the clients, process it and then make calls to the Message class and other static
 * classes in order to preform functionality
 *
 * @author Kevin Jones
 * @version 11/20
 */
public class Server extends Thread {
    private static ServerSocket serverSocket;
    private final Socket socket;

    static {
        try {
            serverSocket = new ServerSocket(2000);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Server(Socket mySocket) {
        this.socket = mySocket;
    }

    public static void main(String[] args) throws IOException {
        while (true) {
            Server server = new Server(serverSocket.accept());
            server.start();
        }
    }
    public void run() {
        try {
            LinkedHashMap<String, String> storeNameMap = FileManager.mapStoresToSellers();

            BufferedReader reader = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
            PrintWriter writer = new PrintWriter(this.socket.getOutputStream());

            while (true) {
                String line = reader.readLine();
                String instruction = line.substring(0, line.indexOf(';'));
                String contents = "";
                try {
                    contents = line.substring(line.indexOf(";") + 1);
                } catch (IndexOutOfBoundsException e) {
                    continue;
                }
                if (instruction.equals("GetSeller")) {
                    // line format: GetSeller;<StoreName> without surrounding carrots
                    writer.println(storeNameMap.get(contents));
                    writer.flush();
                } else if (instruction.equals("CheckIfMessageExists")) {
                    // line format: CheckIfMessageExists;<recipient>;<isRecipientStore>;<isSeller>;<username>;<isUserStore>
                    // without surrounding carrots
                    String recipient = contents.substring(0, contents.indexOf(";"));
                    contents = contents.substring(contents.indexOf(";") + 1);
                    boolean isRecipientStore = Boolean.parseBoolean(contents.substring(0, contents.indexOf(";")));
                    contents = contents.substring(contents.indexOf(";") + 1);
                    boolean isSeller = Boolean.parseBoolean(contents.substring(0, contents.indexOf(";")));
                    contents = contents.substring(contents.indexOf(";") + 1);
                    String username = (contents.substring(0, contents.indexOf(";")));
                    contents = contents.substring(contents.indexOf(";") + 1);
                    boolean isUserStore = Boolean.parseBoolean(contents);
                    checkIfMessageExists(recipient, isRecipientStore, isSeller, username, isUserStore, storeNameMap);
                } else if (instruction.equals("append")) {
                    appendReceive(reader);
                } else if (instruction.equals("delete")) {
                    deleteReceive(reader);
                } else if (instruction.equals("edit")) {
                    editReceive(reader);
                } else if (instruction.equals("display")) {
                    displayReceive(reader, writer);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void checkIfMessageExists(String recipient, boolean isRecipientStore, boolean isSeller,
                                            String username, boolean isUserStore, LinkedHashMap storeNameMap) {
        // check if <username><recipient>.txt exits in directory or not
        if (!isRecipientStore) {
            String path1 = "";
            String path2 = "";
            if (isSeller) {
                path1 = "data/sellers/" + username + "/";
                path2 = "data/buyers/" + recipient + "/";
                if (isUserStore) {
                    path1 = "data/sellers/" + storeNameMap.get(username) + "/" + username + "/";
                }
            } else {
                path1 = "data/buyers/" + username + "/";
                path2 = "data/sellers/" + recipient + "/";
            }
            try {
                System.out.println(path1);
                System.out.println(path2);
                File fUser = new File(path1 + username + recipient + ".txt");
                boolean didCreate = fUser.createNewFile();
                if (didCreate) {
                    File fRecipient = new File(path2 + recipient + username + ".txt");
                    fRecipient.createNewFile();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            FileManager.generateMetricsAboutUser(username,
                    "data/sellers/" + storeNameMap.get(recipient)
                            + "/" + recipient + "/");
            try {
                File fUser = new File(FileManager.getDirectoryFromUsername(username)
                        + "/" + username + recipient + ".txt");
                boolean didCreate = fUser.createNewFile();
                if (didCreate) {
                    String sellerName = (String) storeNameMap.get(recipient);
                    File fRecipient = new File("data/sellers/" + sellerName
                            + "/" + recipient + "/" + recipient + username + ".txt");
                    fRecipient.createNewFile();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Method to receive data from client for appending and call append
     *
     * @param reader buffered reader being used
     *
     * @author John Brooks
     */
    public static void appendReceive(BufferedReader reader) {
        try {
            String personData = reader.readLine();
            String sender = personData.substring(0, personData.indexOf(","));
            personData = personData.substring(personData.indexOf(",") + 1);
            String recipient = personData.substring(0, personData.indexOf(","));
            personData = personData.substring(personData.indexOf(",") + 1);
            String storeName = personData.substring(0, personData.indexOf(","));
            personData = personData.substring(personData.indexOf(",") + 1);
            String buyer = personData.substring(0, personData.indexOf(","));
            boolean isBuyer = false;
            if (buyer.equals("true"))
                isBuyer = true;

            String message = reader.readLine();

            Message.appendMessage(sender, recipient, storeName, isBuyer, message);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "The data could not be handled.", "Messaging System",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Method to receive info for deleting and call delete
     *
     * @param reader buffered reader
     *
     * @author John Brooks
     */
    public static void deleteReceive(BufferedReader reader) {
        try {
            String personData = reader.readLine();
            String sender = personData.substring(0, personData.indexOf(","));
            personData = personData.substring(personData.indexOf(",") + 1);
            String recipient = personData.substring(0, personData.indexOf(","));
            personData = personData.substring(personData.indexOf(",") + 1);
            String storeName = personData.substring(0, personData.indexOf(","));
            personData = personData.substring(personData.indexOf(",") + 1);
            String buyer = personData.substring(0, personData.indexOf(","));
            boolean isBuyer = false;
            if (buyer.equals("true"))
                isBuyer = true;

            String message = reader.readLine();

            Message.deleteMessage(sender, recipient, storeName, isBuyer, message);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "The data could not be handled.", "Messaging System",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Method to receive info for editing a message and call edit
     *
     * @param reader buffered reader
     *
     * @author John Brooks
     */
    public static void editReceive(BufferedReader reader) {
        try {
            String personData = reader.readLine();
            String sender = personData.substring(0, personData.indexOf(","));
            personData = personData.substring(personData.indexOf(",") + 1);
            String recipient = personData.substring(0, personData.indexOf(","));
            personData = personData.substring(personData.indexOf(",") + 1);
            String storeName = personData.substring(0, personData.indexOf(","));
            personData = personData.substring(personData.indexOf(",") + 1);
            String buyer = personData.substring(0, personData.indexOf(","));
            boolean isBuyer = false;
            if (buyer.equals("true"))
                isBuyer = true;

            String messageToEdit = reader.readLine();
            String edit = reader.readLine();

            Message.editMessage(sender, recipient, storeName, isBuyer, messageToEdit, edit);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "The data could not be handled.", "Messaging System",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void displayReceive(BufferedReader reader, PrintWriter writer) {
        try {
            String personData = reader.readLine();
            String sender = personData.substring(0, personData.indexOf(","));
            personData = personData.substring(personData.indexOf(",") + 1);
            String recipient = personData.substring(0, personData.indexOf(","));
            personData = personData.substring(personData.indexOf(",") + 1);
            String storeName = personData.substring(0, personData.indexOf(","));
            personData = personData.substring(personData.indexOf(",") + 1);
            String buyer = personData.substring(0, personData.indexOf(","));
            boolean isBuyer = false;
            if (buyer.equals("true"))
                isBuyer = true;

            ArrayList<String> messageContents = Message.displayMessage(sender, recipient, storeName, isBuyer);
            String returnedContents = "";
            for (int i = 0; i < messageContents.size(); i++) {
                returnedContents = returnedContents + messageContents.get(i) + ": : : :";
            }
            writer.write(returnedContents);
            writer.println();
            writer.flush();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "The data could not be handled.", "Messaging System",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

}
