import javax.swing.*;
/**
 * This thread class will be used to create the Gui and Clients for each messenger and will be run using
 * UserThread.start()
 *
 * @author Kevin Jones
 * @version 11/20
 */
public class UserThread extends Thread {
    private String name;
    public static void main(String[] args) {
        UserThread myThread = new UserThread("Seller");
        myThread.start();
    }

    public UserThread(String name) {
        this.name = name;

    }

    public synchronized void run() {
        SwingUtilities.invokeLater(new MessageGui(this.name, true));
    }
}
