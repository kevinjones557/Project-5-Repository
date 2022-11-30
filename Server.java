import javax.swing.*;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
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
                System.out.println("all " + line);
                String instruction = line.substring(0, line.indexOf(';'));
                System.out.println("instruction " + instruction);
                String contents = "";
                try {
                    contents = line.substring(line.indexOf(";") + 1);
                } catch (IndexOutOfBoundsException e) {
                    continue;
                }
                System.out.println("contents " + contents);
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
                } else if (instruction.equals("isRecipientStore")) {
                    writer.println(FileManager.isRecipientStore(contents));
                    writer.flush();
                } else if (instruction.equals("getStoresFromSellers")) {
                    if (FileManager.getStoresFromSeller(contents).size() > 0) {
                        writer.println(String.join(";", FileManager.getStoresFromSeller(contents)));
                        writer.flush();
                    } else {
                        writer.println();
                        writer.flush();
                    }
                } else if (instruction.equals("importFile")) {
                    String path = contents.substring(0, contents.indexOf(";"));
                    contents = contents.substring(contents.indexOf(";") + 1);
                    String recipient = contents.substring(0, contents.indexOf(";"));
                    contents = contents.substring(contents.indexOf(";") + 1);
                    String username = contents.substring(0, contents.indexOf(";"));
                    contents = contents.substring(contents.indexOf(";") + 1);
                    boolean isSeller = Boolean.parseBoolean(contents.substring(0, contents.indexOf(";")));
                    contents = contents.substring(contents.indexOf(";") + 1);
                    boolean isUserStore = Boolean.parseBoolean(contents.substring(0, contents.indexOf(";")));
                    contents = contents.substring(contents.indexOf(";") + 1);
                    boolean isRecipientStore = Boolean.parseBoolean(contents);
                    importFile(path, recipient, username, isSeller, isUserStore, isRecipientStore, storeNameMap);
                } else if (instruction.equals("exportFile")) {
                    String recipient = contents.substring(0, contents.indexOf(";"));
                    contents = contents.substring(contents.indexOf(";") + 1);
                    String username = contents.substring(0, contents.indexOf(";"));
                    contents = contents.substring(contents.indexOf(";") + 1);
                    boolean isSeller = Boolean.parseBoolean(contents.substring(0, contents.indexOf(";")));
                    contents = contents.substring(contents.indexOf(";") + 1);
                    boolean isUserStore = Boolean.parseBoolean(contents.substring(0, contents.indexOf(";")));
                    contents = contents.substring(contents.indexOf(";") + 1);
                    exportFile(recipient, username, isSeller, isUserStore, contents, storeNameMap);
                } else if (instruction.equals("getConversationsFromStore")) {
                    String username = contents.substring(0, contents.indexOf(";"));
                    contents = contents.substring(contents.indexOf(";") + 1);
                    writer.println(String.join(";", FileManager.getConversationsFromStore(username,
                            contents)));
                    writer.flush();
                } else if (instruction.equals("getConversationsFromUser")) {
                    writer.println(String.join(";", FileManager.getConversationsFromUser(contents)));
                    writer.flush();
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

    public synchronized void importFile(String path, String recipient, String username, boolean isSeller,
                                        boolean isUserStore, boolean isRecipientStore,
                                        LinkedHashMap<String, String> storeNameMap) {
        // set up paths to correct files
        String fileSender;
        String fileReceiver;
        if (isUserStore) {
            fileSender = FileManager.getStoreDirectory(
                    storeNameMap.get(username), username);
            fileReceiver = "data/buyers/" + recipient + "/";
        } else if (isSeller) {
            fileReceiver = "data/buyers/" + recipient + "/";
            fileSender = "data/sellers/" + username + "/";
        } else if (isRecipientStore) {
            fileSender = "data/buyers/" + username + "/";
            fileReceiver = FileManager.getStoreDirectory(
                    storeNameMap.get(recipient), recipient);
        } else {
            fileReceiver = "data/sellers/" + recipient + "/";
            fileSender = "data/buyers/" + username + "/";
        }

        fileReceiver += recipient + username + ".txt";
        fileSender += username + recipient + ".txt";

        File senderFile = new File(fileSender);
        File receiverFile = new File(fileReceiver);
        File importFile = new File(path);
        try (BufferedReader bfr = new BufferedReader(new FileReader(importFile))) {
            PrintWriter pwReceiver = new PrintWriter(new FileWriter(receiverFile, true));
            PrintWriter pwSender = new PrintWriter(new FileWriter(senderFile, true));


            String timeStamp = new SimpleDateFormat(
                    "MM/dd HH:mm:ss").format(new java.util.Date());

            String line = bfr.readLine();
            while (line != null) {
                pwSender.print(username + " " + timeStamp + "- ");
                pwReceiver.print(username + " " + timeStamp + "- ");
                pwReceiver.println(line);
                pwSender.println(line);
                if (!isSeller) {
                    String storePath;
                    if (FileManager.checkSellerExists(recipient)) {
                        storePath = null;
                    } else {
                        storePath = FileManager.getStoreDirectory(
                                storeNameMap.get(recipient), recipient);
                    }
                    MetricManager.addDeleteMessageData(username, storePath,
                            line, false);
                }
                line = bfr.readLine();
            }
            pwReceiver.close();
            pwSender.close();
        } catch (IOException e) {
            System.out.println("Error reading file!");
        }

    }

    public void exportFile(String recipient, String username, boolean isSeller, boolean isUserStore,
                         String path, LinkedHashMap<String, String> storeNameMap) {
        if (!path.endsWith("/")) {
            path += "/";
        }
        String filepath;
        File f = new File(path + username + ".csv");
        if (isUserStore) {
            filepath = FileManager.getStoreDirectory(storeNameMap.get(username),
                    username);
        } else if (isSeller) {
            filepath = "data/sellers/" + username + "/";
        } else {
            filepath = "data/buyers/" + username + "/";
        }
        try {
            f.createNewFile();
            PrintWriter pw = new PrintWriter(new FileWriter(f, false));
            pw.println("Name:,Date:,Time:,Message:");
            BufferedReader bfr = new BufferedReader(new FileReader(filepath
                    + username + recipient + ".txt"));
            String line = bfr.readLine();
            while (line != null) {
                String name = line.substring(0, line.indexOf(" "));
                line = line.substring(line.indexOf(" ") + 1);
                String date = line.substring(0, line.indexOf(" "));
                line = line.substring(line.indexOf(" ") + 1);
                String time = line.substring(0, line.indexOf(" ") - 1);
                String message = line.substring(line.indexOf(" ") + 1);
                pw.println(name + "," + date + "," + time + "," + message);
                line = bfr.readLine();
            }
            pw.close();
            bfr.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
