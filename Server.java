import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedHashMap;

/**
 * This will be a class that sets up the server for the messaging system
 * It will read data from the clients, process it and then make calls to the Message class and other static
 * classes in order to preform functionality
 *
 * @author Kevin Jones
 * @version 11/20
 */
public class Server {
    public static void main(String[] args) throws IOException {
        LinkedHashMap<String, String> storeNameMap = FileManager.mapStoresToSellers();

        ServerSocket serverSocket = new ServerSocket(2000); // port 2000
        Socket socket = serverSocket.accept();

        BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintWriter writer = new PrintWriter(socket.getOutputStream());

        while (true) {
            String line = reader.readLine();
            String instruction = line.substring(0, line.indexOf(';'));
            String contents = line.substring(line.indexOf(";") + 1);
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
            }
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
}
