import javax.swing.*;
/**
 * This thread class will be used to create the Gui and Clients for each messenger and will be run using
 * UserThread.start()
 *
 * @author Kevin Jones
 * @version 11/20
 */
public class UserThread extends Thread {
    public static void main(String[] args) {
    }

    public UserThread(String name) {
        UserThread myThread = new UserThread("");
        myThread.start();
    }

    public synchronized void run() {
        SwingUtilities.invokeLater(new MessageGui("Buyer", false, false, false));
    }
}
