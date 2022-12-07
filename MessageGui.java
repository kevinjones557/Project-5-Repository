import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.font.TextAttribute;
import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.util.*;

/**
 * This class sets up the GUI for the Client
 * A child class of Client it will use Client method calls to send and recieve message from server
 *
 * @author Kevin Jones
 * @version 11/20
 */
public class MessageGui extends Client implements Runnable{
    //Gui initializations
    private final JFrame myFrame = new JFrame();
    private final JFrame metricsFrame = new JFrame();
    private final JPanel topLeft = new JPanel();
    private final JScrollPane messagePanel = new JScrollPane();
    private final JScrollPane scrollPane = new JScrollPane();
    private final JScrollPane metricsScroll = new JScrollPane();
    private final Box userPanel = Box.createVerticalBox();
    private final Box labelBox = Box.createVerticalBox();

    private final JPopupMenu popupMenu1 = new JPopupMenu();
    private final JPopupMenu popupMenu2 =  new JPopupMenu();
    private final JButton editOption = new JButton("Edit");
    private final JButton deleteOption = new JButton("Delete");
    private final JButton cancelOption = new JButton("Cancel");
    private final JButton invisibleOption = new JButton("Become Invisible to User");
    private final JButton becomeVisibleOption = new JButton("Become Visible to User");
    private final JButton blockOption = new JButton("Block User");
    private final JButton unblockOption = new JButton("Unblock User");
    private final JLabel connectedLabel = new JLabel();
    private int sortType = 0;
    JLabel info = new JLabel();
    JLabel topLabel3 = new JLabel();

    // characteristics of chat
    private String recipient;
    private final String username;
    private String storeName;
    private boolean isRecipientStore;
    private final boolean isUserSeller;

    private boolean isUserStore;
    private boolean initialSetup = true;

    private void createLeftPanel() {
        ToolTipManager.sharedInstance().setInitialDelay(0);
        ToolTipManager.sharedInstance().setLightWeightPopupEnabled(true);
        popupMenu1.setVisible(false);
        popupMenu2.setVisible(false);
        if (initialSetup) {
            myFrame.setTitle("Messaging System");
            myFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            myFrame.setLayout(null);
            //setting the bounds for the JFrame
            myFrame.setBounds(300, 10, 1000, 800);
            myFrame.setResizable(false);
        }
        Border br = BorderFactory.createLineBorder(Color.black);
        Container c = myFrame.getContentPane();
        // creating box with buttons
        userPanel.removeAll();
        ArrayList<String> availableMessages = super.getUsersSignal(0, this.username, this.isUserSeller);
        if (!isUserSeller) {
            availableMessages.addAll(super.getUsersSignal(2, this.username, false));
        }
        ArrayList<String> allMessages = super.getConversationsFromUser(this.username);
        System.out.println(allMessages);
        System.out.println("as;dflj " + availableMessages);

        topLeft.setLayout(null);
        topLeft.setBounds(0, 0, 165, 45);

        ImageIcon i = new ImageIcon("info.png");
        Image image = i.getImage();
        Image rescaled = image.getScaledInstance(20, 20, Image.SCALE_SMOOTH);
        i = new ImageIcon(rescaled);

        topLabel3.removeAll();
        topLabel3.setText("Personal Chats ");
        topLabel3.setHorizontalTextPosition(SwingConstants.LEFT);
        topLabel3.setIcon(i);
        topLabel3.setFont(new Font("Times New Roman", Font.BOLD, 18));
        topLabel3.setHorizontalAlignment(JLabel.CENTER);
        topLabel3.setMaximumSize(new Dimension(165, 45));
        userPanel.add(topLabel3, Component.LEFT_ALIGNMENT);

        JPanel placeholder = new JPanel();
        placeholder.setOpaque(false);
        placeholder.setBounds(140, 14, 15, 15);
        placeholder.setToolTipText("Right click to use block/invisible button");
        //placeholder.setVisible(false);
        myFrame.add(placeholder);

        //info.setIcon(i);
        //topLeft.add(info, Component.RIGHT_ALIGNMENT);
        //userPanel.add(topLeft);

        // this is run for buyers and sellers, gets personal conversations
        for (String user : allMessages) {
            if (user.length() == 0) {
                break;
            }
            if (!availableMessages.contains(user)) {
                continue;
            }
            JButton tempButton = new JButton(user);
            tempButton.setFocusable(false);
            MouseListener tempListener = new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (e.getSource() == tempButton) {
                        if (SwingUtilities.isLeftMouseButton(e)) {
                            // user could be a buyer, seller, or a store
                            if (!isUserSeller) {
                                isRecipientStore = MessageGui.super.isRecipientStore(user);
                                System.out.println(isRecipientStore);
                                chooseRecipient(user, user);
                            } else {
                                chooseRecipient(user, null);
                            }
                            isUserStore = false;
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
            userPanel.add(tempButton);
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
                    userPanel.add(storeLabel);
                    for (String buyer : buyerConversations) {
                        if (!availableMessages.contains(buyer)) {
                            continue;
                        }
                        JButton tempButton = new JButton(buyer);
                        tempButton.setFocusable(false);
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
                        userPanel.add(tempButton);
                    }
                }
            }
        }

        //Creating a JPanel for the JFrame

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
        searchForUserButton.setFocusable(false);
        bottomButtonPanel.add(searchForUserButton);
        searchForUserButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
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
                        ArrayList<String> availableMessages = getUsersSignal(0, username, isUserSeller);
                        System.out.println("searching " + availableMessages);
                        String newChatRecipient = (String) JOptionPane.showInputDialog(null,
                                "Enter the name of a " + ((isUserSeller) ? "buyer:" : "seller:"),
                                "Start New Chat", JOptionPane.PLAIN_MESSAGE);
                        if (!availableMessages.contains(newChatRecipient)) {
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
                                    myFrame.invalidate();
                                    createLeftPanel();
                                    myFrame.revalidate();
                                }
                            });
                        }
                    }
                }
            }
        });

        JButton seeListOfUsersButton = new JButton("See a list of " + ((isUserSeller)? "buyers" : "stores"));
        seeListOfUsersButton.setMaximumSize(new Dimension(165,74));
        seeListOfUsersButton.setFocusable(false);
        bottomButtonPanel.add(seeListOfUsersButton);
        seeListOfUsersButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (e.getSource() == seeListOfUsersButton) {
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
                        ArrayList<String> options;
                        if (isUserSeller) {
                            options = MessageGui.super.getUsersSignal(0, username, true);
                        } else {
                            options = MessageGui.super.getUsersSignal(2, username, false);
                        }
                        // TODO call client version of this getavailable
                        String newChatRecipient;
                        if (options.get(0).length() > 0) {
                            newChatRecipient = (String) JOptionPane.showInputDialog(null,
                                    "Choose a " + ((isUserSeller) ? "buyer:" : "store:"), "Start New Chat",
                                    JOptionPane.PLAIN_MESSAGE, null, options.toArray(), null);
                        } else {
                            newChatRecipient = null;
                            JOptionPane.showMessageDialog(null, "No options available!");
                        }

                        if (newChatRecipient != null) {
                            // here we know that the user is a buyer, so they must choose a store
                            isRecipientStore = !isUserSeller;
                            MessageGui.super.checkIfMessageExists(newChatRecipient, isRecipientStore, isUserSeller,
                                    user, isUserStore);
                            chooseRecipient(newChatRecipient, user);
                            SwingUtilities.invokeLater(new Runnable() {
                                @Override
                                public void run() {
                                    System.out.println("remaking");
                                    myFrame.invalidate();
                                    createLeftPanel();
                                    myFrame.revalidate();
                                }
                            });
                        }
                    }
                }
            }
        });

        JButton metricsButton = new JButton("View Statistics");
        metricsButton.setMaximumSize(new Dimension(165,74));
        metricsButton.setFocusable(false);
        bottomButtonPanel.add(metricsButton);
        metricsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (e.getSource() == metricsButton) {
                    createStatisticsGUI();
                }
            }
        });
        if (initialSetup) {
            scrollPane.setViewportView(userPanel);
            scrollPane.setBounds(0, 45, 165, 545);
            //TODO check this vvv
            scrollPane.setBounds(0, 0, 165, 600); //xyz y45  height545
            scrollPane.validate();
            c.add(scrollPane);
        }

        //Panel 4

        //top text label
        topTextPanel.setBounds(0,0,165,90);

        // Panel border
        scrollPane.setBorder(BorderFactory.createLineBorder(Color.black));
        scrollPane.getVerticalScrollBar().setValue(0);
        topTextPanel.setBorder(br);
        bottomButtonPanel.setBorder(br);

        //adding the panel to the Container of the JFrame
        c.add(topTextPanel); //xyz
        c.add(bottomButtonPanel);
    }

    public void createMessageBox() {
        popupMenu1.setVisible(false);
        Container c = myFrame.getContentPane();
        Border br = BorderFactory.createLineBorder(Color.BLACK);

        //crete panel for the text area
        JPanel textPanel = new JPanel();
        textPanel.setBounds(165, 590, 820, 171); //171
        textPanel.setBorder(br);
        textPanel.setLayout(null);

        //creating a label for the panel
        JLabel label = new JLabel("Type Message Here:");
        label.setFont(new Font("Times New Roman", Font.BOLD, 25));
        label.setBounds(10,0,250,171); //171
        textPanel.add(label);

        JTextArea textField = new JTextArea(9,50);
        textField.setBounds(250,10,350,150); //150
        textField.setLineWrap(true);
        textPanel.add(textField);

        JButton sendButton = new JButton("Send Message");
        sendButton.setLayout(null);
        sendButton.setBounds(610, 10, 200, 150); //150
        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (e.getSource() == sendButton) {
                    ArrayList<String> unblockedUsers = getUsersSignal(1, username, isUserSeller);
                    System.out.println("blocked" + unblockedUsers);
                    if (!unblockedUsers.contains(recipient)) {
                        JOptionPane.showMessageDialog(null, "Sorry, this user has blocked you"
                        , "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    if (!textField.getText().isBlank() && recipient != null) {
                        System.out.println(textField.getText().trim());
                        MessageGui.super.appendOrDeleteSignal(false, username, recipient, (storeName == null)?
                                "nil" : storeName, !isUserSeller, textField.getText().trim());
                    }
                    textField.setText("");
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            myFrame.invalidate();
                            createMessageGUI();
                            myFrame.revalidate();
                        }
                    });
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

        connectedLabel.removeAll();
        connectedLabel.setText("");
        connectedLabel.setText("Connected with " + this.recipient);
        if (this.recipient == null) {
            connectedLabel.setText("You are not connected with anyone");
        } else if (this.storeName != null && !isUserSeller) {
            connectedLabel.setText("Connected with " + this.storeName);
        } else if (this.storeName != null) {
            connectedLabel.setText("Connected with " + this.recipient + " through " + this.storeName);
        }
        connectedLabel.setFont(new Font("Times New Roman", Font.BOLD, 25));
        connectedLabel.setBounds(0, 0, 600, 45);
        ImageIcon i = new ImageIcon("info.png");

        Image image = i.getImage();
        Image rescaled = image.getScaledInstance(40, 40, Image.SCALE_SMOOTH);
        i = new ImageIcon(rescaled);
        JLabel info = new JLabel(i);
        info.setToolTipText("Right click on a message to edit");
        info.setHorizontalAlignment(SwingConstants.RIGHT);
        info.setBounds(780, 4, 40, 30);
        connectedLabel.setHorizontalAlignment(JLabel.LEFT);
        connectedLabel.setBorder(new EmptyBorder(10, 30, 10, 10));
        if (initialSetup) {
            topPanel.add(connectedLabel);
        }
        topPanel.add(info);

        JButton importFileButton = new JButton("Import a File");
        importFileButton.setLayout(null);
        importFileButton.setBounds(0, 45, 410, 45);
        importFileButton.setFocusable(false);
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
        exportFileButton.setFocusable(false);
        exportFileButton.setBounds(410, 45, 410, 45);
        exportFileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (e.getSource() == exportFileButton) {
                    if (recipient == null) {
                        JOptionPane.showMessageDialog(null, "Please select a recipient first",
                                "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
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

            labelBox.removeAll();
            System.out.println("username " + this.username + "recipient " + this.recipient + "storeName " + this.storeName);
            ArrayList<String> messages = super.displaySignal(username, recipient,
                    (storeName == null)? "nil" : storeName, !isUserSeller);

            for (String s : messages) {
                int numLines = 1 + s.length() / 160; // sets a factor for how many lines are needed
                JTextArea tempLabel = new JTextArea(s);;
                tempLabel.setMaximumSize(new Dimension(820, numLines * 18));
                tempLabel.setEditable(false);
                tempLabel.setLineWrap(true);
                tempLabel.setLocation(10, 0);
                tempLabel.setBackground(myFrame.getBackground());
                System.out.println("here" + s);
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
                                        MessageGui.super.appendOrDeleteSignal(true, username, recipient,
                                                (storeName == null)? "nil" : storeName, !isUserSeller,
                                                tempLabel.getText());
                                        SwingUtilities.invokeLater(new Runnable() {
                                            @Override
                                            public void run() {
                                                myFrame.invalidate();
                                                createMessageGUI();
                                                myFrame.revalidate();
                                            }
                                        });
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
                                                tempLabel.getText().substring(s.indexOf("-") + 2) +
                                                "\nWhat would you like the new " +
                                                "message to say?");
                                        if (newMessage != null) {
                                            MessageGui.super.editSignal(false, username, recipient,
                                                    (storeName == null)? "nil":storeName, !isUserSeller,
                                                    tempLabel.getText(), newMessage);
                                        }
                                        SwingUtilities.invokeLater(new Runnable() {
                                            @Override
                                            public void run() {
                                                myFrame.invalidate();
                                                createMessageGUI();
                                                myFrame.revalidate();
                                            }
                                        });
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
            messagePanel.setViewportView(labelBox);
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
                this.recipient = super.getSellerFromStore(recipient);
                this.storeName = recipient;
            } else {
                this.storeName = null;
                this.recipient = recipient;
            }
        }
        System.out.println("recipient " + this.recipient);
        System.out.println("store " + this.storeName);

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                createTopPanel();
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
        metricsFrame.setTitle("Statistics");
        metricsFrame.setLayout(null);
        //setting the bounds for the JFrame
        metricsFrame.setBounds(500,100,600,600);
        metricsFrame.setResizable(false);

        if (isUserSeller) {
            popupMenu1.setVisible(false);
            popupMenu2.setVisible(false);
            //TODO seller statistics gui
            JPanel textPanel = new JPanel();
            textPanel.setLayout(null);
            textPanel.setBounds(0,0,600,50);

            JLabel label1 = new JLabel("Stores and Customers");
            label1.setFont(new Font("Times New Roman", Font.BOLD, 12));
            label1.setBounds(20,0, 250, 50);
            label1.setHorizontalAlignment(JLabel.CENTER);


            Map attributes = label1.getFont().getAttributes();
            attributes.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
            label1.setFont(label1.getFont().deriveFont(attributes));

            JButton sortNames = new JButton("▲");
            sortNames.setBounds(215, 15, 20, 20);
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

            JLabel label2 = new JLabel("Most Common Overall Words");
            label2.setFont(new Font("Times New Roman", Font.BOLD, 12));
            label2.setBounds(280,0, 225, 50);
            label2.setHorizontalAlignment(JLabel.RIGHT);

            JButton sortTotal = new JButton("▲");
            sortTotal.setBounds(520, 15, 20, 20);
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


            textPanel.add(label1);
            textPanel.add(label2);

            textPanel.add(sortTotal);
            textPanel.add(sortNames);


            metricsFrame.add(textPanel);

            Box labelBox = Box.createVerticalBox();
            Box labelBox2 = Box.createVerticalBox();

            ArrayList<String[]> metricsData = new ArrayList<>();
            // TODO call client version of this
            String[] data1 = {"Walmart-","Bob: 478","Jim: 7"};
            String[] data2 = {"Target-","Billy: 700","Bob: 15"};
            String[] data3 = {"GameStop-","Jimmy: 50","Todd: 30","William: 40"};
            String[] dataOther = {"Aldi-","Jordan: 500"};
            String[] dataOther2 = {"GameStop-","Jimmy: 50","Todd: 30","William: 40"};
            String[] dataOther3 = {"GameStop-","Jimmy: 50","Todd: 30","William: 40"};
            metricsData.add(data1);
            metricsData.add(data2);
            metricsData.add(data3);
            metricsData.add(dataOther);
            metricsData.add(dataOther2);
            metricsData.add(dataOther3);

            // TODO call client for common words
            String[] data4 = {"the: 221", "a: 195", "product: 137", "sell: 122", "because: 96", "stock: 94", "we: 80", "sale: 73", "cost: 60", "discount: 50"};

            for (String[] s : metricsData) {
                JPanel tempPanel = new JPanel();
                textPanel.setLayout(null);

                JLabel labelStore = new JLabel(s[0]);
                labelStore.setMaximumSize(new Dimension(90, 50));
                labelStore.setHorizontalAlignment(JLabel.CENTER);
                labelStore.setLocation(0, 0);
                labelStore.setFont(new Font("Times New Roman", Font.BOLD, 20));

                tempPanel.add(labelStore);
                labelBox.add(tempPanel);

                for (int i = 1; i < s.length; i++) {
                    JPanel tempPanel2 = new JPanel();
                    textPanel.setLayout(null);

                    JLabel labelCustomer = new JLabel(s[i]);
                    //labelCustomer.setMaximumSize(new Dimension(60, 20));
                    labelCustomer.setHorizontalAlignment(JLabel.CENTER);
                    //labelCustomer.setLocation(0, 0);
                    labelCustomer.setFont(new Font("Times New Roman", Font.PLAIN, 13));

                    tempPanel2.add(labelCustomer);
                    labelBox.add(tempPanel2);
                }
            }

            JPanel tempPanel3 = new JPanel();
            textPanel.setLayout(null);

            for (int i = 0; i < data4.length; i++) {
                JPanel tempPanel4 = new JPanel();
                textPanel.setLayout(null);

                JLabel labelListOfWords = new JLabel(data4[i]);
                labelListOfWords.setMaximumSize(new Dimension(90, 50));
                labelListOfWords.setHorizontalAlignment(JLabel.CENTER);
                labelListOfWords.setLocation(0, 0);
                labelListOfWords.setFont(new Font("Times New Roman", Font.BOLD, 15));

                tempPanel4.add(labelListOfWords);
                labelBox2.add(tempPanel4);
            }

            JScrollPane metricsPanel = new JScrollPane(labelBox, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                    JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
            JScrollPane metricsPanel2 = new JScrollPane(labelBox2, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                    JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
            metricsPanel.setBounds(0, 50, 300, 510);
            metricsPanel.setBorder(BorderFactory.createLineBorder(Color.white));
            metricsPanel2.setBounds(300, 50, 285, 510);
            metricsPanel2.setBorder(BorderFactory.createLineBorder(Color.white));
            metricsFrame.add(metricsPanel);
            metricsFrame.add(metricsPanel2);

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
                            sortType = 1;
                        } else {
                            sortNames.setText("▲");
                            sortType = 0;
                        }
                        SwingUtilities.invokeLater(new Runnable() {
                            @Override
                            public void run() {
                                createStatisticsGUI();
                                metricsFrame.revalidate();
                            }
                        });
                    }
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
                            sortType = 2;
                        } else {
                            sortTotal.setText("▲");
                            sortType = 3;
                        }
                        SwingUtilities.invokeLater(new Runnable() {
                            @Override
                            public void run() {
                                createStatisticsGUI();
                                metricsFrame.revalidate();
                            }
                        });
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
                            sortType = 4;
                        } else {
                            sortPersonal.setText("▲");
                            sortType = 5;
                        }
                        SwingUtilities.invokeLater(new Runnable() {
                            @Override
                            public void run() {
                                metricsFrame.invalidate();
                                createStatisticsGUI();
                                metricsFrame.revalidate();
                            }
                        });
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

            labelBox.removeAll();

            System.out.println(sortType);
            ArrayList<String[]> metricsData = MessageGui.super.sortMetricsData(this.username, sortType);
            for (String[] s : metricsData) {
                System.out.print(Arrays.toString(s));
            }
            System.out.println();

            for (String[] s : metricsData) {
                JPanel tempPanel = new JPanel();
                tempPanel.setLayout(null);
                tempPanel.setMaximumSize(new Dimension(7000, 50));

                JLabel labelStore = new JLabel(s[0]);
                labelStore.setHorizontalAlignment(JLabel.CENTER);
                labelStore.setBounds(0, 0, 110, 50);
                labelStore.setFont(new Font("Times New Roman", Font.BOLD, 20));

                JLabel labelTotal = new JLabel(s[1]);
                labelTotal.setHorizontalAlignment(JLabel.CENTER);
                labelTotal.setBounds(110, 0, 225, 50);
                labelTotal.setFont(new Font("Times New Roman", Font.BOLD, 20));

                JLabel labelPersonal = new JLabel(s[2]);
                labelPersonal.setHorizontalAlignment(JLabel.CENTER);
                labelPersonal.setBounds(350,0, 225, 50);
                labelPersonal.setFont(new Font("Times New Roman", Font.BOLD, 20));


                tempPanel.add(labelStore);
                tempPanel.add(labelTotal);
                tempPanel.add(labelPersonal);

                labelBox.add(tempPanel);
            }
            metricsScroll.setViewportView(labelBox);
            metricsScroll.setBounds(0, 50, 600, 550);
            metricsScroll.setBorder(BorderFactory.createLineBorder(Color.white));
            metricsFrame.getContentPane().add(metricsScroll);
        }

        metricsFrame.setVisible(true);
    }

    public void run() {
        createLeftPanel();
        createMessageBox();
        createTopPanel();
        createMessageGUI();
        myFrame.setVisible(true);
        initialSetup = false;
    }


    public static void main(String[] args) throws IOException {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {}
        SwingUtilities.invokeLater(new MessageGui("mulan", false, new Socket("localhost", 2000)));
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
//TODO bugs
//todo metrics manager issue when deleting
//todo update the buttons when a user adds a new chat
//todo write test cases and record video and write readme
//todo edit storeName