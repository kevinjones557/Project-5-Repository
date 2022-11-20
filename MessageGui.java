import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.io.File;

public class MessageGui extends JFrame implements Runnable{
    private JLabel heading = new JLabel("Messaging System");
    private JTextArea messageArea = new JTextArea();
    private JTextField messageInput = new JTextField();

    private void createLeftPanel() {
        this.setTitle("Messaging System");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLayout(null);
        //setting the bounds for the JFrame
        this.setBounds(300,10,1000,800);
        Border br = BorderFactory.createLineBorder(Color.black);
        Container c = getContentPane();
        // creating box with buttons
        Box sellerPanel = Box.createVerticalBox();
        String[] allMessages = {"seller1", "seller2", "thisIsLongSllerNae", "seller2", "seller2", "seller2",
                "seller2", "seller2", "seller2", "seller2","seller2", "seller2", "seller2", "seller2",
                "seller2", "seller2", "seller2", "seller2","seller2", "seller2", "seller2", "seller2",
                "seller2", "seller2", "seller2", "seller2"};
        for (String s : allMessages) {
            JButton tempButton = new JButton(s);
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

        setVisible(true);

    }

    public void createMessageBox() {
        Container c = this.getContentPane();
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
        Container c = this.getContentPane();
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
        Container c = this.getContentPane();

        // creating box for labels

        Box labelBox = Box.createVerticalBox();

        String[] messages = {"lucas 10/23 06:15:56- hello how are you", "lucas 10/23 06:15:56- how are you doing now?",
                "lucas 10/23 06:15:56- hello how are you", "lucas 10/23 06:15:56- how are you doing now?",
                "lucas 10/23 06:15:56- hello how are you", "lucas 10/23 06:15:56- how are you doing now?",
                "lucas 10/23 06:15:56- hello how are you", "lucas 10/23 06:15:56- how are you doing now?",
                "lucas 10/23 06:15:56- hello how are you", "lucas 10/23 06:15:56- how are you doing now?",
                "lucas 10/23 06:15:56- hello how are you", "lucas 10/23 06:15:56- how are you doing now?",
                "lucas 10/23 06:15:56- hello how are you", "lucas 10/23 06:15:56- how are you doing now?",
                "lucas 10/23 06:15:56- hello how are you", "lucas 10/23 06:15:56- how are you doing now?",
                "lucas 10/23 06:15:56- hello how are you", "lucas 10/23 06:15:56- how are you doing now?",
                "lucas 10/23 06:15:56- hello how are you", "lucas 10/23 06:15:56- how are you doing now?",
                "lucas 10/23 06:15:56- hello how are you", "lucas 10/23 06:15:56- how are you doing now?",
                "lucas 10/23 06:15:56- hello how are you", "lucas 10/23 06:15:56- how are you doing now?",
                "lucas 10/23 06:15:56- hello how are you", "lucas 10/23 06:15:56- how are you doing now?",
                "lucas 10/23 06:15:56- hello how are you", "lucas 10/23 06:15:56- how are you doing now?",
                "lucas 10/23 06:15:56- hello how are you", "lucas 10/23 06:15:56- how are you doing now?",
                "lucas 10/23 06:15:56- hello how are you", "lucas 10/23 06:15:56- how are you doing now?",
                "lucas 10/23 06:15:56- hello how are you", "lucas 10/23 06:15:56- how are you doing now?",
                "lucas 10/23 06:15:56- hello how are you", "lucas 10/23 06:15:56- how are you doing now?",
                "lucas 10/23 06:15:56- hello how are you", "lucas 10/23 06:15:56- how are you doing now?",
                "lucas 10/23 06:15:56- hello how are you", "lucas 10/23 06:15:56- how are you doing now?",
                "lucas 10/23 06:15:56- hello how are you", "lucas 10/23 06:15:56- how are you doing now?",
                "lucas 10/23 06:15:56- hello how are you", "lucas 10/23 06:15:56- how are you doing now?",
                "lucas 10/23 06:15:56- hello how are you", "lucas 10/23 06:15:56- how are you doing now?",
                "lucas 10/23 06:15:56- hello how are you", "lucas 10/23 06:15:56- how are you doing now?",
                "lucas 10/23 06:15:56- hello how are you", "lucas 10/23 06:15:56- how are you doing now?",
                "lucas 10/23 06:15:56- hello how are you", "lucas 10/23 06:15:56- how are you doing now?",
                "lucas 10/23 06:15:56- hello how are you", "lucas 10/23 06:15:56- how are you doing now?",
                "lucas 10/23 06:15:56- hello how are you", "lucas 10/23 06:15:56- how are you doing now?",
                "lucas 10/23 06:15:56- hello how are you", "lucas 10/23 06:15:56- how are you doing now?",
                "lucas 10/23 06:15:56- hello how are you", "lucas 10/23 06:15:56- how are you doing now?",
                "lucas 10/23 06:15:56- hello how are you", "lucas 10/23 06:15:56- how are you doing now hello how are you hello how are you hello how are you hello how are you hello how are you hello how are you hello how are you hello how are you hello how are you?"};

        for (String s : messages) {
            JTextArea tempLabel = new JTextArea(s);
            tempLabel.setMaximumSize(new Dimension(820, 20));
            tempLabel.setEditable(false);
            tempLabel.setLineWrap(true);
            tempLabel.setLocation(10, 0);
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
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            System.out.println("Selected file: " + selectedFile.getAbsolutePath());
        }
    }

    public MessageGui() {

    }

    public void run() {
        createLeftPanel();
        createMessageBox();
        createTopPanel();
        createMessageGUI();

    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new MessageGui());
    }


}
