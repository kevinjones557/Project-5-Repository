/**
 * This will be a class that sets up the server for the messaging system
 * It will read data from the clients, process it and then make calls to the Message class and other static
 * classes in order to preform functionality
 *
 * @author Kevin Jones
 * @version 11/20
 */
public class Server {
  
  
  
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
  
}
