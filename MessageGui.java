import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
/**
 * This class sets up the GUI for the Client
 * A child class of Client it will use Client method calls to send and recieve message from server
 *
 * @author Kevin Jones
 * @version 11/20
 */
public class MessageGui extends Client implements Runnable{
    JFrame myFrame = new JFrame();
    private String recipient;
    private String username;
    private String storeName;
    private boolean isRecipientStore;
    private boolean isUserSeller;
    private boolean isUserStore;
    LinkedHashMap<String, String> storeMap;

    private void createLeftPanel() {
        myFrame.setTitle("Messaging System");
        myFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        myFrame.setLayout(null);
        //setting the bounds for the JFrame
        myFrame.setBounds(300,10,1000,800);
        myFrame.setResizable(false);
        Border br = BorderFactory.createLineBorder(Color.black);
        Container c = myFrame.getContentPane();
        // creating box with buttons
        Box sellerPanel = Box.createVerticalBox();
        String[] allMessages = {"seller1", "seller2", "thisIsLongSllerNae", "seller2", "seller2", "seller2",
                "seller2", "seller2", "seller2", "seller2","seller2", "seller2", "seller2", "seller2",
                "seller2", "seller2", "seller2", "seller2","seller2", "seller2", "seller2", "seller2",
                "seller2", "seller2", "seller2", "seller2"};
        for (String s : allMessages) {
            JButton tempButton = new JButton(s);
            ActionListener tempListener = new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (e.getSource() == tempButton) {
                        chooseRecipient(s);
                        sendMessage();
                    }
                }
            };
            tempButton.addActionListener(tempListener);
            tempButton.setMinimumSize(new Dimension(150,50));
            tempButton.setMaximumSize(new Dimension(150,50));
            sellerPanel.add(tempButton);
        }


        //Creating a JPanel for the JFrame
        JScrollPane scrollPane = new JScrollPane(sellerPanel);
        JPanel topTextPanel = new JPanel();
        Box bottomButtonPanel = Box.createVerticalBox();
        bottomButtonPanel.setBounds(0,590,165,172);
        //setting the panel layout as null
        topTextPanel.setLayout(null);
        //adding a label element to the panel

        JLabel topLabel1 = new JLabel("Right click on seller for");
        JLabel topLabel2 = new JLabel("block/invisible features.");
        JLabel topLabel3 = new JLabel("Current Chats:");
        topLabel3.setFont(new Font("Times New Roman",Font.BOLD,24));

        topLabel1.setBounds(12, 0, 165, 25);
        topLabel2.setBounds(12, 20, 165, 20);
        topLabel3.setBounds(5, 45, 165, 45);


        topTextPanel.add(topLabel1);
        topTextPanel.add(topLabel2);
        topTextPanel.add(topLabel3);
        // creating buttons for bottom panel
        JButton createNewChatButton = new JButton("Start New Chat");
        createNewChatButton.setMaximumSize(new Dimension(165,57));
        bottomButtonPanel.add(createNewChatButton);

        JButton invisibleUserButton = new JButton("Invisible Users");
        invisibleUserButton.setMaximumSize(new Dimension(165,57));
        bottomButtonPanel.add(invisibleUserButton);

        JButton metricsButton = new JButton("View Statistics");
        metricsButton.setMaximumSize(new Dimension(165,57));
        bottomButtonPanel.add(metricsButton);
        //Panel 1

        scrollPane.setBounds(0,90,165,500);
        //Panel 4

        //top text label
        topTextPanel.setBounds(0,0,165,90);

        // Panel border
        scrollPane.setBorder(br);
        topTextPanel.setBorder(br);
        bottomButtonPanel.setBorder(br);

        //adding the panel to the Container of the JFrame
        c.add(scrollPane);
        c.add(topTextPanel);
        c.add(bottomButtonPanel);

        myFrame.setVisible(true);

    }

    public void createMessageBox() {
        Container c = myFrame.getContentPane();
        Border br = BorderFactory.createLineBorder(Color.BLACK);

        //crete panel for the text area
        JPanel textPanel = new JPanel();
        textPanel.setBounds(165, 590, 820, 171);
        textPanel.setBorder(br);
        textPanel.setLayout(null);

        //creating a label for the panel
        JLabel label = new JLabel("Type Message Here:");
        label.setFont(new Font("Times New Roman", Font.BOLD, 25));
        label.setBounds(10,0,250,171);
        textPanel.add(label);

        JTextArea textField = new JTextArea(9,50);
        textField.setBounds(250,10,350,150);
        textField.setLineWrap(true);
        textPanel.add(textField);

        JButton sendButton = new JButton("Send Message");
        sendButton.setLayout(null);
        sendButton.setBounds(610, 10, 200, 150);

        textPanel.add(sendButton);

        c.add(textPanel);
    }

    public void createTopPanel() {
        Container c = myFrame.getContentPane();
        Border br = BorderFactory.createLineBorder(Color.BLACK);

        JPanel topPanel = new JPanel();
        topPanel.setLayout(null);
        topPanel.setBounds(165, 0, 820, 90);
        topPanel.setBorder(br);

        JLabel label = new JLabel("Right click on message to edit or delete it.");
        label.setFont(new Font("Times New Roman", Font.BOLD, 30));
        label.setBounds(0, 0, 820, 45);
        label.setHorizontalAlignment(JLabel.CENTER);
        topPanel.add(label);

        JButton importFileButton = new JButton("Import a File");
        importFileButton.setLayout(null);
        importFileButton.setBounds(0, 45, 410, 45);
        topPanel.add(importFileButton);

        JButton exportFileButton = new JButton("Export Entire Conversation as CSV File");
        exportFileButton.setLayout(null);
        exportFileButton.setBounds(410, 45, 410, 45);
        topPanel.add(exportFileButton);

        c.add(topPanel);
    }

    public void createMessageGUI() {
        Container c = myFrame.getContentPane();
        chooseRecipient("Seller");

        // creating box for labels

        Box labelBox = Box.createVerticalBox();

        String storeName = null;
        ArrayList<String> messages = Message.displayMessage(username, recipient, storeName, !isUserSeller);

        for (String s : messages) {
            JTextArea tempLabel = new JTextArea(s);
            tempLabel.setMaximumSize(new Dimension(820, 20));
            tempLabel.setEditable(false);
            tempLabel.setLineWrap(true);
            tempLabel.setLocation(10, 0);
            tempLabel.setBackground(myFrame.getBackground());
            labelBox.add(tempLabel);
        }

        JScrollPane messagePanel = new JScrollPane(labelBox);
        messagePanel.setBounds(170, 90, 820, 500);
        messagePanel.setBorder(BorderFactory.createLineBorder(Color.white));
        c.add(messagePanel);

    }

    public void getImportFile() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
        int result = fileChooser.showOpenDialog(myFrame);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            System.out.println("Selected file: " + selectedFile.getAbsolutePath());
        }
    }

    public void chooseRecipient(String recipient) {
        if (isRecipientStore) {
            this.recipient = storeMap.get(recipient);
            storeName = recipient;
        } else {
            this.recipient = recipient;
        }
    }

    public MessageGui(String username, boolean isRecipientStore, boolean isUserSeller, boolean isUserStore) {
        super(username);
        if (isUserStore) {
            this.username = storeMap.get(username);
            this.storeName = username;
        } else {
            this.username = username;
        }
        this.isRecipientStore = isRecipientStore;
        this.isUserSeller = isUserSeller;
        this.isUserStore = isUserStore;
        storeMap = FileManager.mapStoresToSellers();
        // at end of constructor, if the recipient is a seller, recipient is seller name and store is null
        // if recipient is a store, recipient is seller name and storeName is store's name
        // if user is store, then username is seller name and storeName is store's name
        // if not username is seller's name and storeName = null
    }

    public void run() {
        createLeftPanel();
        createMessageBox();
        createTopPanel();
        createMessageGUI();

    }

}
