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

    public Client (String name) {
        this.name = name;

    }


    public void sendMessage() {
        System.out.println(name);
    }
    
    
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
    
    public static void editSignal(boolean delete, String sender, String recipient, String storeName,
                                  boolean isBuyer, String messageToEdit, String edit, PrintWriter writer) {
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
}
