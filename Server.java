import javax.swing.*;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
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
        //create folder for filter message
        File filterRoot = new File("filter");
        filterRoot.mkdir();
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
                String request = reader.readLine();
                System.out.println("all " + request);
                String instruction = request.substring(0, request.indexOf(';'));
                System.out.println("instruction " + instruction);
                String contents = "";
                try {
                    contents = request.substring(request.indexOf(";") + 1);
                } catch (IndexOutOfBoundsException e) {
                    continue;
                }
                System.out.println("contents " + contents);
                // TODO: convert this to a switch and make logic into individual methods to improve readability
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
                } else if (instruction.equals("removeNamedStore")) {
                    handleRemoveNamedStore(request, socket);
                } else if (instruction.equals("isSeller")) {
                    handleIsSeller(request, socket);
                } else if (instruction.equals("getUserStores")) {
                    handleGetUserStores(request, socket);
                } else if (instruction.equals("appendStoreList")) {
                    handleAppendStoreList(request, socket);
                } else if (instruction.equals("readPassword")) {
                    handleReadPassword(request, socket);
                } else if (instruction.equals("writeFile1")) { // without append param
                    handleWriteFileNoAppend(request, socket);
                } else if (instruction.equals("writeFile2")) { // with append param
                    handleWriteFileAppend(request, socket);
                } else if (instruction.equals("encryptFile")) {
                    handleEncryptFile(request, socket);
                } else if (instruction.equals("checkStoreList")) {
                    handleCheckStoreList(request, socket);
                } else if (instruction.equals("checkName")) {
                    handleCheckName(request, socket);
                } else if (instruction.equals("deleteUserInProgress")) {
                    handleDeleteUserInProgress(request, socket);
                } else if (instruction.equals("createUser")) {
                    handleCreateUser(request, socket);
                } else if (instruction.equals("checkPassword")) {
                    handleCheckPassword(request, socket);
                } else if (instruction.equals("moveUsername")) {
                    handleMoveUsername(request, socket);
                } else if (instruction.equals("changeStoreName")) {
                    handleChangeStoreName(request, socket);
                } else if (instruction.equals("checkUserExists")) {
                    handleCheckUserExists(request, socket);
                } else if (instruction.equals("updateStoreList")) {
                    handleUpdateStoreList(request, socket);
                } else if (instruction.equals("append")) {
                    appendReceive(reader);
                } else if (instruction.equals("delete")) {
                    deleteReceive(reader);
                } else if (instruction.equals("edit")) {
                    editReceive(reader);
                } else if (instruction.equals("display")) {
                    displayReceive(reader, writer);
                } else if (instruction.equals("invisible")) {
                    Invisible.becomeInvisibleToUser(request.split(";")[1], request.split(";")[3],
                            Boolean.parseBoolean(request.split(";")[2]));
                } else if (instruction.equals("visible")) {
                    Invisible.becomeVisibleAgain(request.split(";")[1], request.split(";")[3],
                            Boolean.parseBoolean(request.split(";")[2]));
                } else if (instruction.equals("block")) {
                    Blocking.blockUser(request.split(";")[1], request.split(";")[3],
                            Boolean.parseBoolean(request.split(";")[2]));
                } else if (instruction.equals("unblock")) {
                    Blocking.unblockUser(request.split(";")[1], request.split(";")[3],
                            Boolean.parseBoolean(request.split(";")[2]));
                } else if (instruction.equals("isRecipientBlocked")) {
                    String[] ins = request.split(";");
                    String sendBack = Boolean.toString(Blocking.isRecipientBlocked(ins[1], Boolean.parseBoolean(ins[2]),
                            Boolean.parseBoolean(ins[3])? FileManager.mapStoresToSellers().get(ins[4]): ins[4]));
                    writer.println(sendBack);
                    writer.flush();
                } else if (instruction.equals("recipientCantSeeMe")) {
                    String[] ins = request.split(";");
                    String sendBack = Boolean.toString(Invisible.recipientCantSeeMe(ins[1], Boolean.parseBoolean(ins[2]),
                            Boolean.parseBoolean(ins[3])? FileManager.mapStoresToSellers().get(ins[4]): ins[4]));
                    writer.println(sendBack);
                    writer.flush();
                } else if (instruction.equals("getAvailableUsers")){
                    String[] ins = request.split(";");
                    String sendBack = String.join(";", Invisible.getAvailableUsers(ins[1],
                            Boolean.parseBoolean(ins[2])));
                    writer.println(sendBack);
                    writer.flush();
                } else if (instruction.equals("getMessageAbleUsers")){
                    String[] ins = request.split(";");
                    String sendBack = String.join(";", Blocking.getMessageAbleUser(ins[1],
                            Boolean.parseBoolean(ins[2])));
                    writer.println(sendBack);
                    writer.flush();
                } else if (instruction.equals("getAvailableStores")){
                    String[] ins = request.split(";");
                    String sendBack = String.join(";", Invisible.getAvailableStores(ins[1]));
                    writer.println(sendBack);
                    writer.flush();
                } else if (instruction.equals("getMessageAbleStores")) {
                    String[] ins = request.split(";");
                    String sendBack = String.join(";", Blocking.getMessageAbleStores(ins[1]));
                    writer.println(sendBack);
                    writer.flush();
                } else if (instruction.equals("getBuyerMetricData")) {
                    ArrayList<String[]> data = handleGetBuyerMetricData(request, storeNameMap);
                    ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
                    outputStream.writeObject(data);
                    outputStream.flush();
                } else if (instruction.equals("getFilteringList")) {
                    String sendBack = String.join(";",(String[])
                            Filtering.censoredList(request.split(";")[1]).toArray());
                    writer.println(sendBack);
                    writer.flush();
                } else if (instruction.equals("addFilter")) {
                    String[] ins = request.split(";");
                    try {
                        Filtering.addFilter(ins[1], ins[2], ins[3]);
                    } catch(Exception e) {

                    }
                } else if (instruction.equals("deleteFilter")) {
                    String[] ins = request.split(";");
                    try {
                        Filtering.deleteFilter(ins[1], ins[2]);
                    } catch (Exception e) {

                    }
                } else if (instruction.equals("editFilter")) {
                    String[] ins = request.split(";");
                    try {
                        Filtering.editFilter(ins[1], ins[2], ins[3]);
                    } catch (Exception e) {

                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static ArrayList<String[]> handleGetBuyerMetricData(String request, LinkedHashMap<String, String> storeNameMap) throws IOException{
        // parameters:
        // request; buyer's name
        String username = request.substring(request.indexOf(";"));
        // return an arraylist of string arrays
        // each string array goes as follows
        // Store Name, Total Messages, Individual DMs
        ArrayList<String[]> metricData = new ArrayList<>();
        storeNameMap.forEach((store, seller) -> {
            File metrics = new File(String.format("data/%s/%s/metrics.txt", seller, store));
            File userMetrics = new File(String.format("data/%s/%s/%s" + "metrics.txt", seller, store, username));
            try {
                ArrayList<String> storeMetricsData = FileManager.readFile(metrics);
                ArrayList<String> userMetricsData = FileManager.readFile(userMetrics);
                String line = storeMetricsData.get(0);
                String storeMsgCount = line.substring(line.indexOf(":")+1).trim();
                line = userMetricsData.get(0);
                String userMsgCount = line.substring(line.indexOf(":")+1).trim();
                metricData.add(new String[]{store, storeMsgCount, userMsgCount});
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("An error occurred while finding the metrics of " + store);
            }
        });
        return metricData;
    }
    private static void handleUpdateStoreList(String request, Socket socket) {

    }

    private static void handleCheckUserExists(String request, Socket socket) {

    }

    private static void handleChangeStoreName(String request, Socket socket) {

    }

    private static void handleMoveUsername(String request, Socket socket) {

    }

    private static void handleCheckPassword(String request, Socket socket) {

    }

    private static void handleCreateUser(String request, Socket socket) {

    }

    private static void handleDeleteUserInProgress(String request, Socket socket) {

    }

    private static void handleCheckName(String request, Socket socket) {

    }

    private static void handleCheckStoreList(String request, Socket socket) {

    }

    private static void handleEncryptFile(String request, Socket socket) {

    }

    private static void handleWriteFileAppend(String request, Socket socket) {

    }

    private static void handleWriteFileNoAppend(String request, Socket socket) {

    }

    private static void handleReadPassword(String request, Socket socket) {

    }

    private static void handleAppendStoreList(String request, Socket socket) {

    }

    private static void handleGetUserStores(String request, Socket socket) {

    }

    private static void handleIsSeller(String request, Socket socket) {

    }

    private static void handleRemoveNamedStore(String request, Socket socket) {

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
    

    public synchronized void importFile(String path, String recipient, String username, boolean isSeller,
                                        boolean isUserStore, boolean isRecipientStore,
                                        LinkedHashMap<String, String> storeNameMap) {
        // Destin: don't synchronize this entire method later.
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
