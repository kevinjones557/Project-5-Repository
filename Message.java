import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class Message {

    /**
     * Method to set up appending with filepaths
     *
     * @param sender sender
     * @param recipient recipient
     * @param storeName if there is no store involved, it is "nil", otherwise just the store name
     * @param isBuyer if they are buyer
     * @param message message user inputs in GUI
     *
     * @author John Brooks
     */
    public static void appendMessage(String sender, String recipient, String storeName, boolean isBuyer, String message) {
        String fileRecipient = "";
        String fileSender = "";

        if (storeName.equals("nil")) {
            if (isBuyer) {
                fileSender = "data/buyers/" + sender + "/" + sender + recipient + ".txt";
                fileRecipient = "data/sellers/" + recipient + "/" + recipient + sender + ".txt";
            } else {
                fileSender = "data/sellers/" + sender + "/" + sender + recipient + ".txt";
                fileRecipient = "data/buyers/" + recipient + "/" + recipient + sender + ".txt";
            }
        } else {
            if (isBuyer) {
                fileSender = "data/buyers/" + sender + "/" + sender + storeName + ".txt";
                fileRecipient = "data/sellers/" + recipient + "/" + storeName + "/" + storeName + sender + ".txt";
            } else {
                fileSender = "data/sellers/" + sender + "/" + storeName + "/" + storeName + recipient + ".txt";
                fileRecipient = "data/buyers/" + recipient + "/" + recipient + storeName + ".txt";
            }
        }
        appendMessageExecute(sender, recipient, isBuyer, fileSender, fileRecipient, message);
    }

    /**
     * Methods that writes message to both sender and recipient files
     *
     * @param sender sender
     * @param recipient recipient
     * @param isBuyer buyer or not
     * @param fileSender filepath for sender
     * @param fileRecipient filepath for receiver
     * @param message message
     *
     * @author John Brooks
     */
    public static void appendMessageExecute(String sender, String recipient, boolean isBuyer, String fileSender,
                                     String fileRecipient, String message) {
        String printFile;

        File senderF = new File(fileSender);
        File recipientF = new File(fileRecipient);
        if (senderF.exists() && recipientF.exists()) {
            try {
                FileOutputStream fosSend = new FileOutputStream(senderF, true);
                PrintWriter messageSenderWriter = new PrintWriter(fosSend);
                FileOutputStream fosReceive = new FileOutputStream(recipientF, true);
                PrintWriter messageReceiveWriter = new PrintWriter(fosReceive);
                //write it on the end of each person's file
                String timeStamp = new SimpleDateFormat(
                        "MM/dd HH:mm:ss").format(new java.util.Date());
                messageSenderWriter.println(sender + " " + timeStamp + "- " + message);
                messageReceiveWriter.println(sender + " " + timeStamp + "- " + message);
                messageSenderWriter.close();
                messageReceiveWriter.close();
                if (isBuyer) {
                    String storePath;
                    if (FileManager.checkSellerExists(recipient)) {
                        storePath = null;
                    } else {
                        storePath = fileRecipient;
                    }
                    MetricManager.addDeleteMessageData(
                            sender, storePath, message, false);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Method to set up edit with filepaths
     *
     * @param sender sender
     * @param recipient recipient
     * @param storeName if there is no store involved, it is "nil", otherwise just the store name
     * @param isBuyer if they are buyer
     * @param message message user wants to edit (including timestamp and everything)
     * @param edit change the user made
     *
     * @author John Brooks
     */
    public static void editMessage(String sender, String recipient, String storeName, boolean isBuyer, String message, String edit) {
        String fileRecipient = "";
        String fileSender = "";

        if (storeName.equals("nil")) {
            if (isBuyer) {
                fileSender = "data/buyers/" + sender + "/" + sender + recipient + ".txt";
                fileRecipient = "data/sellers/" + recipient + "/" + recipient + sender + ".txt";
            } else {
                fileSender = "data/sellers/" + sender + "/" + sender + recipient + ".txt";
                fileRecipient = "data/buyers/" + recipient + "/" + recipient + sender + ".txt";
            }
        } else {
            if (isBuyer) {
                fileSender = "data/buyers/" + sender + "/" + sender + storeName + ".txt";
                fileRecipient = "data/sellers/" + recipient + "/" + storeName + "/" + storeName + sender + ".txt";
            } else {
                fileSender = "data/sellers/" + sender + "/" + storeName + "/" + storeName + recipient + ".txt";
                fileRecipient = "data/buyers/" + recipient + "/" + recipient + storeName + ".txt";
            }
        }
        editMessageExecute(sender, recipient, isBuyer, fileSender, fileRecipient, message, edit);
    }

    /**
     * Searches file for index that matches one given by the user and changes that line and
     * writes it back to the file
     *
     * @param recipient     receives file
     * @param fileSender    sender file path
     * @param fileRecipient recipient file path
     * @author John Brooks
     */
    public static void editMessageExecute(String sender, String recipient, boolean isBuyer, String fileSender,
                                   String fileRecipient, String message, String edit) {
        String printFile;
        String extractNameAndTime = message.substring(0, message.indexOf("-") + 2);
        String editedMessage;

        ArrayList<String> readSenderFile = new ArrayList<>();
        ArrayList<String> readReceiverFile = new ArrayList<>();

        File senderF = new File(fileSender);
        File recipientF = new File(fileRecipient);

        if (senderF.exists() && recipientF.exists()) {
            try {
                //reading both files to list
                BufferedReader buffSender = new BufferedReader(
                        new FileReader(senderF));
                BufferedReader buffReceiver = new BufferedReader(
                        new FileReader(recipientF));
                printFile = buffSender.readLine();
                while (printFile != null) {
                    readSenderFile.add(printFile);
                    printFile = buffSender.readLine();
                }
                String line2 = buffReceiver.readLine();
                while (line2 != null) {
                    readReceiverFile.add(line2);
                    line2 = buffReceiver.readLine();
                }

                //writers
                FileOutputStream fosSend = new FileOutputStream(senderF, false);
                PrintWriter messageSenderWriter = new PrintWriter(fosSend);
                FileOutputStream fosReceive = new FileOutputStream(recipientF, false);
                PrintWriter messageReceiveWriter = new PrintWriter(fosReceive);

                //read through list, when the message matches the index of the list, changes that index to the edit
                for (int i = 0; i < readSenderFile.size(); i++) {
                    if (readSenderFile.get(i).equals(message)) {
                        editedMessage = extractNameAndTime + edit;
                        readSenderFile.set(i, editedMessage);
                    }
                }
                for (int i = 0; i < readReceiverFile.size(); i++) {
                    if (readReceiverFile.get(i).equals(message)) {
                        editedMessage = extractNameAndTime + edit;
                        readReceiverFile.set(i, editedMessage);
                    }
                }

                //write back to files
                for (int i = 0; i < readSenderFile.size(); i++) {
                    messageSenderWriter.println(readSenderFile.get(i));
                }
                for (int i = 0; i < readReceiverFile.size(); i++) {
                    messageReceiveWriter.println(readReceiverFile.get(i));
                }

                buffReceiver.close();
                messageSenderWriter.close();
                messageReceiveWriter.close();
                if (isBuyer) {
                    String storePath;
                    if (FileManager.checkSellerExists(recipient)) {
                        storePath = null;
                    } else {
                        storePath = fileRecipient;
                    }
                    message = message.substring(
                            message.indexOf("-") + 1);
                    MetricManager.editMessageData(
                            sender, storePath, message, edit);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Method to set up appending with filepaths
     *
     * @param sender sender
     * @param recipient recipient
     * @param storeName if there is no store involved, it is "nil", otherwise just the store name
     * @param isBuyer if they are buyer
     * @param message message user inputs to delete
     *
     * @author John Brooks
     */
    public static void deleteMessage(String sender, String recipient, String storeName, boolean isBuyer, String message) {
        String fileRecipient = "";
        String fileSender = "";

        if (storeName.equals("nil")) {
            if (isBuyer) {
                fileSender = "data/buyers/" + sender + "/" + sender + recipient + ".txt";
                fileRecipient = "data/sellers/" + recipient + "/" + recipient + sender + ".txt";
            } else {
                fileSender = "data/sellers/" + sender + "/" + sender + recipient + ".txt";
                fileRecipient = "data/buyers/" + recipient + "/" + recipient + sender + ".txt";
            }
        } else {
            if (isBuyer) {
                fileSender = "data/buyers/" + sender + "/" + sender + storeName + ".txt";
                fileRecipient = "data/sellers/" + recipient + "/" + storeName + "/" + storeName + sender + ".txt";
            } else {
                fileSender = "data/sellers/" + sender + "/" + storeName + "/" + storeName + recipient + ".txt";
                fileRecipient = "data/buyers/" + recipient + "/" + recipient + storeName + ".txt";
            }
        }
        deleteMessageExecute(sender, recipient, isBuyer, fileSender, fileRecipient, message);
    }

    /**
     * Method that deletes message
     *
     * @param sender sender
     * @param recipient recipient
     * @param isBuyer if buyer or not
     * @param fileSender file path of sender
     * @param fileRecipient file path of recipient
     * @param message message to be deleted
     *
     * @author John Brooks
     */
    public static void deleteMessageExecute(String sender, String recipient, boolean isBuyer,
                                            String fileSender, String fileRecipient, String message) {
        String printFile;

        File senderF = new File(fileSender);
        File recipientF = new File(fileRecipient);

        ArrayList<String> readSenderFile = new ArrayList<>();

        if (senderF.exists() && recipientF.exists()) {
            try {
                //read file to list
                BufferedReader readSend = new BufferedReader(new FileReader(senderF));
                printFile = readSend.readLine();
                while (printFile != null) {
                    readSenderFile.add(printFile);
                    printFile = readSend.readLine();
                }

                //writers
                FileOutputStream fosSend = new FileOutputStream(senderF, false);
                PrintWriter messageSenderWriter = new PrintWriter(fosSend);

                // read through list, write to file if it is not the deleted message
                for (int i = 0; i < readSenderFile.size(); i++) {
                    if (!(readSenderFile.get(i)).equals(message))
                        messageSenderWriter.println(readSenderFile.get(i));
                }
                readSend.close();
                messageSenderWriter.close();
                if (isBuyer) {
                    String storePath;
                    if (FileManager.checkSellerExists(recipient)) {
                        storePath = null;
                    } else {
                        storePath = fileRecipient;
                    }
                    MetricManager.addDeleteMessageData(sender, storePath,
                            message, true);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Method to read message file and return contents in arrayList to display
     *
     * @param sender user
     * @param recipient person their message file is with
     * @param storeName potential store name, same "nil" rule
     * @param isBuyer if buyer or not
     * @return returnContents arrayList that contains file contents
     *
     * @author John Brooks
     */
    public static ArrayList<String> displayMessage(String sender, String recipient, String storeName, boolean isBuyer) {
        String path = "";
        String line = "";
        ArrayList<String> returnContents = new ArrayList<>();

        if (storeName.equals("nil")) {
            if (isBuyer)
                path = "data/buyers/" + sender + "/" + sender + recipient + ".txt";
            else
                path = "data/sellers/" + sender + "/" + sender + recipient + ".txt";
        } else {
            if (isBuyer)
                path = "data/buyers/" + sender + "/" + sender + storeName + ".txt";
            else
                path = "data/sellers/" + sender + "/" + storeName + "/" + storeName + recipient + ".txt";
        }
        try {
            File toDisplay = new File(path);
            BufferedReader readFile = new BufferedReader(new FileReader(toDisplay));
            line = readFile.readLine();
            while (line != null) {
                returnContents.add(line);
                line = readFile.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Unable to load message");
        }
        return returnContents;
    }
}
