import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.*;
import java.awt.font.TextAttribute;
import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;

/**
 * This class sets up the GUI for the Client
 * A child class of Client it will use Client method calls to send and recieve message from server
 *
 * @author Kevin Jones
 * @version 11/20
 */
public class MessageGui extends Client implements Runnable{
    //Gui initializations
    JFrame myFrame = new JFrame();
    private final JPopupMenu popupMenu1 = new JPopupMenu();
    private final JPopupMenu popupMenu2 =  new JPopupMenu();
    private final JButton editOption = new JButton("Edit");
    private final JButton deleteOption = new JButton("Delete");
    private final JButton cancelOption = new JButton("Cancel");
    private final JButton invisibleOption = new JButton("Become Invisible to User");
    private final JButton becomeVisibleOption = new JButton("Become Visible to User");
    private final JButton blockOption = new JButton("Block User");
    private final JButton unblockOption = new JButton("Unblock User");

    // characteristics of chat
    private String recipient;
    private final String username;
    private String storeName;
    private boolean isRecipientStore;
    private final boolean isUserSeller;

    private boolean isUserStore;



    private void createLeftPanel() {
        popupMenu1.setVisible(false);
        popupMenu2.setVisible(false);
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
        sellerPanel.removeAll();
        JLabel topLabel3 = new JLabel("Personal Chats:");
        topLabel3.setFont(new Font("Times New Roman",Font.BOLD,22));
        topLabel3.setHorizontalAlignment(JLabel.CENTER);
        topLabel3.setMaximumSize(new Dimension(165, 45));
        sellerPanel.add(topLabel3);
        ArrayList<String> allMessages = super.getConversationsFromUser(this.username);
        // TODO change to vinh
        // this is run for buyers and sellers, gets personal conversations
        for (String user : allMessages) {
            if (user.length() == 0) {
                break;
            }
            JButton tempButton = new JButton(user);
            MouseListener tempListener = new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (e.getSource() == tempButton) {
                        if (SwingUtilities.isLeftMouseButton(e)) {
                            // user could be a buyer, seller, or a store
                            if (!isUserSeller) {
                                isRecipientStore = MessageGui.super.isRecipientStore(recipient);
                            }
                            isUserStore = false;
                            chooseRecipient(user, null);
                            sendMessage();
                        } else if (SwingUtilities.isRightMouseButton(e)) {
                            createPopUpBlockInvisible(e, user);
                        }
                    }
                    // TODO call client function with recipient to get message info
                }
            };
            tempButton.addMouseListener(tempListener);
            tempButton.setMinimumSize(new Dimension(165,50));
            tempButton.setMaximumSize(new Dimension(165,50));
            sellerPanel.add(tempButton);
        }

        if (isUserSeller) {
            ArrayList<String> sellerStores = super.getStoresFromSeller(this.username);
            for (String store : sellerStores) {
                if (store.length() == 0) {
                    break;
                }
                ArrayList<String> buyerConversations = super.getConversationsFromStore(this.username, store);
                System.out.println("hi"+store + buyerConversations);
                if (buyerConversations.size() != 0) {
                    JLabel storeLabel = new JLabel(store + ":");
                    storeLabel.setFont(new Font("Times New Roman", Font.BOLD, 25));
                    storeLabel.setMaximumSize(new Dimension(165, 30));
                    storeLabel.setHorizontalAlignment(JLabel.CENTER);
                    sellerPanel.add(storeLabel);
                    for (String buyer : buyerConversations) {
                        JButton tempButton = new JButton(buyer);
                        ActionListener tempListener = new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                if (e.getSource() == tempButton) {
                                    isUserStore = true;
                                    isRecipientStore = false;
                                    chooseRecipient(buyer, store);
                                }
                                // TODO call client function with recipient to get message info
                            }
                        };
                        tempButton.addActionListener(tempListener);
                        tempButton.setMinimumSize(new Dimension(165, 50));
                        tempButton.setMaximumSize(new Dimension(165, 50));
                        sellerPanel.add(tempButton);
                    }
                }
            }
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

        topLabel1.setBounds(12, 0, 165, 25);
        topLabel2.setBounds(12, 20, 165, 20);

        topTextPanel.add(topLabel1);
        topTextPanel.add(topLabel2);
        // creating buttons for bottom panel
        JButton searchForUserButton = new JButton("Search for a " + ((isUserSeller)? "buyer" : "seller"));
        searchForUserButton.setMaximumSize(new Dimension(165,74));
        bottomButtonPanel.add(searchForUserButton);
        searchForUserButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    if (e.getSource() == searchForUserButton) {
                        String user = username;
                        // if user is a seller allow option to choose account to message from
                        if (isUserSeller) {
                            //first choose which account to start message from
                            ArrayList<String> sellerStores = MessageGui.super.getStoresFromSeller(username);
                            sellerStores.add(0, username);
                            String[] accounts = sellerStores.toArray(new String[0]);
                            user = (String) JOptionPane.showInputDialog(null, "Choose account " +
                                            "to message from", "Select Account", JOptionPane.PLAIN_MESSAGE, null,
                                    accounts, null);
                            isUserStore = user != null && !user.equals(username);
                        }
                        if (user != null) {
                            String[] options = Blocking.getMessageAbleUser(username, isUserSeller);
                            // TODO call client version of this^^^^
                            String newChatRecipient = (String) JOptionPane.showInputDialog(null,
                                    "Enter the name of a " + ((isUserSeller) ? "buyer:" : "seller:"),
                                    "Start New Chat", JOptionPane.PLAIN_MESSAGE);
                            if (!Arrays.asList(options).contains(newChatRecipient)) {
                                JOptionPane.showMessageDialog(null, "Sorry, no user found " +
                                        "with this name!","Error",JOptionPane.ERROR_MESSAGE);
                            } else {
                                MessageGui.super.checkIfMessageExists(newChatRecipient, isRecipientStore, isUserSeller,
                                        user, isUserStore);
                                chooseRecipient(newChatRecipient, user);
                                SwingUtilities.invokeLater(new Runnable() {
                                    @Override
                                    public void run() {
                                        System.out.println("remaking");
                                        createLeftPanel();
                                        myFrame.revalidate();
                                    }
                                });
                            }
                        }
                    }
                } catch (IOException io) {
                    io.printStackTrace();
                }
            }
        });

        JButton seeListOfUsersButton = new JButton("See a list of " + ((isUserSeller)? "buyers" : "stores"));
        seeListOfUsersButton.setMaximumSize(new Dimension(165,74));
        bottomButtonPanel.add(seeListOfUsersButton);
        seeListOfUsersButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (e.getSource() == seeListOfUsersButton) {
                    try {
                        String user = username;
                        // if user is a seller allow option to choose account to message from
                        if (isUserSeller) {
                            //first choose which account to start message from
                            ArrayList<String> sellerStores = MessageGui.super.getStoresFromSeller(username);
                            sellerStores.add(0, username);
                            String[] accounts = sellerStores.toArray(new String[0]);
                            user = (String) JOptionPane.showInputDialog(null, "Choose account " +
                                            "to message from", "Select Account", JOptionPane.PLAIN_MESSAGE, null,
                                    accounts, null);
                            isUserStore = user != null && !user.equals(username);
                        }
                        if (user != null) {
                            String[] options = Blocking.getMessageAbleUser(username, isUserSeller);
                            // TODO call client version of this
                            if (!isUserSeller) {
                                // if it is a buyer get all the stores from the sellers
                                ArrayList<String> allStores = new ArrayList<>();
                                for (String seller : options) {
                                    allStores.addAll(MessageGui.super.getStoresFromSeller(seller));
                                }
                                options = allStores.toArray(new String[0]);
                            }
                            String newChatRecipient = (String) JOptionPane.showInputDialog(null,
                                    "Choose a " + ((isUserSeller) ? "buyer:" : "store:"), "Start New Chat",
                                    JOptionPane.PLAIN_MESSAGE, null, options, null);
                            if (!isUserSeller) {
                                // here we know that the user is a buyer, so they must choose a store
                                isRecipientStore = true;
                            }
                            if (newChatRecipient != null) {
                                MessageGui.super.checkIfMessageExists(newChatRecipient, isRecipientStore, isUserSeller,
                                        user, isUserStore);
                                chooseRecipient(newChatRecipient, user);
                                SwingUtilities.invokeLater(new Runnable() {
                                    @Override
                                    public void run() {
                                        System.out.println("remaking");
                                        createLeftPanel();
                                    }
                                });
                            }
                        }
                    } catch (IOException io) {
                        io.printStackTrace();
                        JOptionPane.showMessageDialog(null, "Unable to collect info!");
                    }
                }
            }
        });

        JButton metricsButton = new JButton("View Statistics");
        metricsButton.setMaximumSize(new Dimension(165,74));
        bottomButtonPanel.add(metricsButton);
        metricsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (e.getSource() == metricsButton) {
                    createStatisticsGUI();
                }
            }
        });

        scrollPane.setBounds(0,45,165,545);
        scrollPane.validate();

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
    }

    public void createMessageBox() {
        popupMenu1.setVisible(false);
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
        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (e.getSource() == sendButton) {
                    if (!textField.getText().isBlank() && recipient != null) {
                        System.out.println(textField.getText().trim());
                        // TODO call client function to append message
                    }
                    textField.setText("");
                }
            }
        });

        textPanel.add(sendButton);

        c.add(textPanel);
    }

    public void createTopPanel() {
        popupMenu1.setVisible(false);
        popupMenu2.setVisible(false);
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
        importFileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (recipient != null) {
                    if (e.getSource() == importFileButton) {
                        String filename = getImportFile();
                        if (!(filename == null) && !filename.endsWith(".txt")) {
                            JOptionPane.showMessageDialog(null, "File must be a text file",
                                    "Invalid File", JOptionPane.ERROR_MESSAGE);
                        } else if (filename != null) {
                            MessageGui.super.importFile(filename, recipient, username, isUserSeller,
                                    isUserStore, isRecipientStore);
                            SwingUtilities.invokeLater(new Runnable() {
                                @Override
                                public void run() {
                                    createMessageGUI();
                                    myFrame.revalidate();
                                }
                            });
                        }
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Please choose a recipient first"
                            , "Error", JOptionPane.ERROR_MESSAGE);
                }

            }
        });
        topPanel.add(importFileButton);

        JButton exportFileButton = new JButton("Choose directory to export conversation as CSV File");
        exportFileButton.setLayout(null);
        exportFileButton.setBounds(410, 45, 410, 45);
        exportFileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (e.getSource() == exportFileButton) {
                    JFileChooser directoryChooser = new JFileChooser();
                    directoryChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                    directoryChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
                    int result = directoryChooser.showOpenDialog(myFrame);
                    if (result == JFileChooser.APPROVE_OPTION) {
                        String directory = directoryChooser.getSelectedFile().getAbsolutePath();
                        if (recipient != null) {
                            MessageGui.super.exportFile(recipient, username, isUserSeller, isUserStore, directory);
                            JOptionPane.showMessageDialog(null, "Export Successful");
                        } else {
                            JOptionPane.showMessageDialog(null, "Please choose a recipient first"
                                    , "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    }

                }
            }
        });
        topPanel.add(exportFileButton);

        c.add(topPanel);
    }

    public void createMessageGUI() {
        popupMenu1.setVisible(false);
        popupMenu2.setVisible(false);
        Container c = myFrame.getContentPane();

        if (recipient != null) {
            Box labelBox = Box.createVerticalBox();

            ArrayList<String> messages = Message.displayMessage(username, recipient, storeName, !isUserSeller);
            // TODO call client version of this
            System.out.println(messages);

            for (String s : messages) {
                int numLines = 1 + s.length() / 160; // sets a factor for how many lines are needed
                JTextArea tempLabel = new JTextArea(s);
                tempLabel.setMaximumSize(new Dimension(820, numLines * 18));
                tempLabel.setEditable(false);
                tempLabel.setLineWrap(true);
                tempLabel.setLocation(10, 0);
                tempLabel.setBackground(myFrame.getBackground());
                tempLabel.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        boolean rightClick = SwingUtilities.isRightMouseButton(e);
                        if (rightClick) {
                            deleteOption.setMaximumSize(new Dimension(74, 28));

                            for (ActionListener al : deleteOption.getActionListeners()) {
                                deleteOption.removeActionListener(al);
                            }
                            deleteOption.addActionListener(new ActionListener() {
                                @Override
                                public void actionPerformed(ActionEvent e) {
                                    if (e.getSource() == deleteOption) {
                                        popupMenu1.setVisible(false);
                                        // TODO call delete message on the selected message
                                    }
                                }
                            });

                            editOption.setLayout(null);
                            editOption.setMaximumSize(new Dimension(74, 28));
                            for (ActionListener al : editOption.getActionListeners()) {
                                editOption.removeActionListener(al);
                            }
                            editOption.addActionListener(new ActionListener() {
                                @Override
                                public void actionPerformed(ActionEvent e) {
                                    if (e.getSource() == editOption) {
                                        popupMenu1.setVisible(false);
                                        String newMessage = JOptionPane.showInputDialog("Current Message: " +
                                                s.substring(s.indexOf("-") + 2) +
                                                "\nWhat would you like the new " +
                                                "message to say?");
                                        if (newMessage != null) {
                                            // TODO call edit message with new message and original and recipient
                                        }
                                    }
                                }
                            });

                            cancelOption.setLayout(null);
                            cancelOption.setMaximumSize(new Dimension(74, 28));
                            cancelOption.addActionListener(new ActionListener() {
                                @Override
                                public void actionPerformed(ActionEvent e) {
                                    if (e.getSource() == cancelOption) {
                                        popupMenu1.setVisible(false);
                                    }
                                }
                            });
                            popupMenu1.add(deleteOption);
                            popupMenu1.add(editOption);
                            popupMenu1.add(cancelOption);
                            popupMenu1.setVisible(true);
                            popupMenu1.setLocation(e.getLocationOnScreen());
                        }
                    }
                });
                labelBox.add(tempLabel);
            }
            JScrollPane messagePanel = new JScrollPane(labelBox);
            messagePanel.setBounds(170, 90, 820, 500);
            messagePanel.setBorder(BorderFactory.createLineBorder(Color.white));
            c.add(messagePanel);
        }
    }

    public String getImportFile() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
        int result = fileChooser.showOpenDialog(myFrame);
        if (result == JFileChooser.APPROVE_OPTION) {
            return fileChooser.getSelectedFile().getAbsolutePath();
        }
        return null;
    }

    public void chooseRecipient(String recipient, String storeName) {
        if (isUserSeller) {
            this.storeName = null;
            if (isUserStore) {
                this.storeName = storeName;
            }
            this.recipient = recipient;
        } else {
            if (isRecipientStore) {
                this.recipient = super.getSellerFromStore(storeName);
                this.storeName = recipient;
            } else {
                this.storeName = null;
                this.recipient = recipient;
            }
        }
        System.out.println(recipient);
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                createMessageGUI();
                myFrame.revalidate();
            }
        });

    }

    public MessageGui(String username, boolean isUserSeller, Socket socket) {
        super(username, socket);
        this.username = username;
        this.isUserSeller = isUserSeller;
        if (isUserSeller) {
            isRecipientStore = false;
        } else {
            isUserStore = false;
        }
        this.recipient = null;
        run();
        // at end of constructor, if the recipient is a seller, recipient is seller name and store is null
        // if recipient is a store, recipient is seller name and storeName is store's name
        // if user is store, then username is seller name and storeName is store's name
        // if not username is seller's name and storeName = null
    }

    public void createPopUpBlockInvisible(MouseEvent e, String receiver) {
        popupMenu1.setVisible(false);
        invisibleOption.setMaximumSize(new Dimension(200, 28));

        for (ActionListener al : invisibleOption.getActionListeners()) {
            invisibleOption.removeActionListener(al);
        }
        invisibleOption.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (e.getSource() == invisibleOption) {
                    popupMenu2.setVisible(false);
                    System.out.println("invisible");
                    // TODO call make invisible
                    sendBlockInvisibleSignal("invisible", username, Boolean.toString(isUserSeller), receiver);
                }
            }
        });

        becomeVisibleOption.setMaximumSize(new Dimension(200, 28));

        for (ActionListener al : becomeVisibleOption.getActionListeners()) {
            becomeVisibleOption.removeActionListener(al);
        }
        becomeVisibleOption.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (e.getSource() == becomeVisibleOption) {
                    popupMenu2.setVisible(false);
                    System.out.println("visible");
                    // TODO call make visible again
                    sendBlockInvisibleSignal("visible", username, Boolean.toString(isUserSeller), receiver);
                }
            }
        });

        blockOption.setLayout(null);
        blockOption.setMaximumSize(new Dimension(200, 28));
        for (ActionListener al : blockOption.getActionListeners()) {
            blockOption.removeActionListener(al);
        }
        blockOption.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (e.getSource() == blockOption) {
                    popupMenu2.setVisible(false);
                    System.out.println("blocking");
                    // TODO block the user
                    sendBlockInvisibleSignal("block", username, Boolean.toString(isUserSeller), receiver);
                }
            }
        });

        unblockOption.setLayout(null);
        unblockOption.setMaximumSize(new Dimension(200, 28));
        for (ActionListener al : unblockOption.getActionListeners()) {
            unblockOption.removeActionListener(al);
        }
        unblockOption.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (e.getSource() == unblockOption) {
                    popupMenu2.setVisible(false);
                    System.out.println("unblocking");
                    // TODO unblock the user
                    sendBlockInvisibleSignal("unblock", username, Boolean.toString(isUserSeller), receiver);
                }
            }
        });

        cancelOption.setLayout(null);
        cancelOption.setMaximumSize(new Dimension(200, 28));
        cancelOption.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (e.getSource() == cancelOption) {
                    popupMenu2.setVisible(false);
                }
            }
        });
        boolean isBlocked = isBlockedOrCannotSee(0, username, Boolean.toString(isUserSeller),
                Boolean.toString(isRecipientStore), receiver);
        boolean cantSeeThisUser = isBlockedOrCannotSee(1, username, Boolean.toString(isUserSeller),
                Boolean.toString(isRecipientStore), receiver);

        if(isBlocked) {
            popupMenu2.remove(blockOption);
            popupMenu2.add(unblockOption);
        } else {
            popupMenu2.remove(unblockOption);
            popupMenu2.add(blockOption);
        }
        if(cantSeeThisUser) {
            popupMenu2.add(becomeVisibleOption);
            popupMenu2.remove(invisibleOption);
        } else {
            popupMenu2.remove(becomeVisibleOption);
            popupMenu2.add(invisibleOption);
        }
        popupMenu2.add(cancelOption);
        popupMenu2.setVisible(true);
        popupMenu2.setLocation(e.getLocationOnScreen());
    }

    public void createStatisticsGUI() {
        JFrame metricsFrame = new JFrame();
        metricsFrame.setTitle("Statistics");
        metricsFrame.setLayout(null);
        //setting the bounds for the JFrame
        metricsFrame.setBounds(500,100,600,600);
        metricsFrame.setResizable(false);

        if (isUserSeller) {

        } else {
            popupMenu1.setVisible(false);
            popupMenu2.setVisible(false);

            JPanel textPanel = new JPanel();
            textPanel.setLayout(null);
            textPanel.setBounds(0,0,600,50);

            JLabel label1 = new JLabel("Store Name");
            label1.setFont(new Font("Times New Roman", Font.BOLD, 12));
            label1.setBounds(0,0, 90, 50);
            label1.setHorizontalAlignment(JLabel.CENTER);


            Map attributes = label1.getFont().getAttributes();
            attributes.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
            label1.setFont(label1.getFont().deriveFont(attributes));

            JButton sortNames = new JButton("▲");
            sortNames.setBounds(90, 15, 20, 20);
            sortNames.setMargin(new Insets(0,-1,0,0));
            sortNames.setFocusPainted(false);
            sortNames.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (e.getSource() == sortNames) {
                        if (sortNames.getText().equals("▲")) {
                            sortNames.setText("▼");
                        } else {
                            sortNames.setText("▲");
                        }
                    }
                    sortNames.revalidate();
                    textPanel.revalidate();
                }
            });

            JLabel label2 = new JLabel("Total Messages Received by Store");
            label2.setFont(new Font("Times New Roman", Font.BOLD, 12));
            label2.setBounds(100,0, 225, 50);
            label2.setHorizontalAlignment(JLabel.RIGHT);

            JButton sortTotal = new JButton("▲");
            sortTotal.setBounds(330, 15, 20, 20);
            sortTotal.setMargin(new Insets(0,-1,0,0));
            sortTotal.setFocusPainted(false);
            sortTotal.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (e.getSource() == sortTotal) {
                        if (Objects.equals(sortTotal.getText(), "▲")) {
                            sortTotal.setText("▼");
                        } else {
                            sortTotal.setText("▲");
                        }
                    }
                }
            });

            attributes = label2.getFont().getAttributes();
            attributes.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
            label2.setFont(label2.getFont().deriveFont(attributes));

            JLabel label3 = new JLabel("Number of Messages Sent to Store");
            label3.setFont(new Font("Times New Roman", Font.BOLD, 12));
            label3.setBounds(350,0, 225, 50);
            label3.setHorizontalAlignment(JLabel.CENTER);

            JButton sortPersonal = new JButton("▼");
            sortPersonal.setBounds(560, 15, 20, 20);
            sortPersonal.setMargin(new Insets(0,-1,0,0));
            sortPersonal.setFocusPainted(false);
            sortPersonal.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (e.getSource() == sortPersonal) {
                        if (Objects.equals(sortPersonal.getText(), "▲")) {
                            sortPersonal.setText("▼");
                        } else {
                            sortPersonal.setText("▲");
                        }
                    }
                }
            });

            attributes = label3.getFont().getAttributes();
            attributes.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
            label3.setFont(label3.getFont().deriveFont(attributes));

            textPanel.add(label1);
            textPanel.add(label2);
            textPanel.add(label3);
            textPanel.add(sortTotal);
            textPanel.add(sortNames);
            textPanel.add(sortPersonal);

            metricsFrame.add(textPanel);


            if (true) {
                Box labelBox = Box.createVerticalBox();

                ArrayList<String[]> metricsData = new ArrayList<>();
                // TODO call client version of this
                String[] data1 = {"Walmart","478","7"};
                String[] data2 = {"Target","700","15"};
                String[] data3 = {"GameStop","50","30"};
                metricsData.add(data1);
                metricsData.add(data2);
                metricsData.add(data3);

                for (String[] s : metricsData) {
                    JPanel tempPanel = new JPanel();
                    textPanel.setLayout(null);

                    JLabel labelStore = new JLabel(s[0]);
                    labelStore.setBounds(0,0, 90, 50);
                    labelStore.setHorizontalAlignment(JLabel.CENTER);

                    JLabel labelTotal = new JLabel(s[1]);
                    labelTotal.setBounds(100,0, 225, 50);
                    labelTotal.setHorizontalAlignment(JLabel.CENTER);

                    JLabel labelPersonal = new JLabel(s[2]);
                    labelPersonal.setBounds(350,0, 225, 50);
                    labelPersonal.setHorizontalAlignment(JLabel.CENTER);

                    tempPanel.add(labelStore);
                    tempPanel.add(labelTotal);
                    tempPanel.add(labelPersonal);

                    labelBox.add(tempPanel);
                }
                JScrollPane messagePanel = new JScrollPane(labelBox);
                messagePanel.setBounds(0, 50, 600, 550);
                messagePanel.setBorder(BorderFactory.createLineBorder(Color.white));
                metricsFrame.getContentPane().add(messagePanel);
            }
        }

        metricsFrame.setVisible(true);
    }

    public void run() {
        createLeftPanel();
        createMessageBox();
        createTopPanel();
        createMessageGUI();
        myFrame.setVisible(true);
    }


    public static void main(String[] args) throws IOException {
        SwingUtilities.invokeLater(new MessageGui("dan", false, new Socket("localhost", 2000)));
    }

}
/*
Possible Interactions:
buyer -> store          for both of these, isUserSeller is false, isUserStore is false, isRecipientStore could be
buyer -> seller         false or true. Username is always itself, recipient is always a seller name, storeName
                        could be null or a storeName if the recipient is store

store -> buyer          for both of these, isRecipientStore is false, isUserSeller is true, isUserStore could be
seller -> buyer         false or true. Username is always itself, recipient is always a buyer name, storeName
                        could be null or a storeName if seller is a store
 */
