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
 * A child class of Client it will use Client method calls to send and receive message from server
 *
 * @author Kevin Jones
 * @version 11/20
 */
public class MessageGui extends Client implements Runnable {
    //Gui initializations
    private final JFrame myFrame = new JFrame();
    private final JFrame glassFrame = new JFrame();
    private final JFrame metricsFrame = new JFrame();
    private final JPanel topLeft = new JPanel();
    private final JScrollPane messagePanel = new JScrollPane();
    private final JScrollPane scrollPane = new JScrollPane();
    private final JScrollPane metricsScroll = new JScrollPane();
    private final Box userPanel = Box.createVerticalBox();
    private final Box labelBox = Box.createVerticalBox();
    private final Box labelBox1 = Box.createVerticalBox();

    JScrollPane metricsPanel = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
            JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
    JScrollPane metricsPanel2 = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
            JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);


    private final JPopupMenu popupMenu1 = new JPopupMenu();
    private final JPopupMenu popupMenu2 = new JPopupMenu();
    private final JButton editOption = new JButton("Edit");
    private final JButton deleteOption = new JButton("Delete");
    private final JButton cancelOption = new JButton("Cancel");
    private final JButton invisibleOption = new JButton("Become Invisible to User");
    private final JButton becomeVisibleOption = new JButton("Become Visible to User");
    private final JButton blockOption = new JButton("Block User");
    private final JButton unblockOption = new JButton("Unblock User");
    private final JLabel connectedLabel = new JLabel();
    private final JPopupMenu popUpSetting = new JPopupMenu();
    private final JPopupMenu filterMain = new JPopupMenu();
    private int sortType = 0;
    private int sortWords = 0;
    JLabel topLabel3 = new JLabel();

    // characteristics of chat
    private String recipient;
    private final String username;
    private String storeName;
    private boolean isRecipientStore;
    private final boolean isUserSeller;

    private boolean isUserStore;
    private boolean initialSetup = true;

    /**
     * creates GUI for the filtering system, allows users to add, delete, and edit filters
     */
    public void createFilterMain() {
        popUpSetting.removeAll();
        popUpSetting.setVisible(false);
        ImageIcon i = new ImageIcon("ImageIcon/info.png");
        Image image = i.getImage();
        Image rescaled = image.getScaledInstance(20, 20, Image.SCALE_SMOOTH);
        i = new ImageIcon(rescaled);

        JButton instruction = new JButton("Instruction", i);
        instruction.setFont(new Font("Arial", Font.PLAIN, 30));
        instruction.setMaximumSize(new Dimension(400, 50));
        instruction.setFocusable(false);
        instruction.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                filterMain.setVisible(false);
                String toDisplay = "Warning: intentionally messing with the filtering system may result\n" +
                        "in messages being extremely hard to read!\n\n" +
                        "There are 3 options:\n" +
                        "1. Add filter: Add a word/phrase to censored and its replacement\n" +
                        "2. Delete filter: Remove the censoring of a word/phrase\n" +
                        "3. Edit filter: Edit a replacement of a censored word/phrase\n";
                JOptionPane.showConfirmDialog(myFrame, toDisplay, "Instructions",
                        JOptionPane.DEFAULT_OPTION);
                filterMain.setVisible(true);
            }
        });

        JButton addFilter = new JButton("Add new filter");
        addFilter.setFont(new Font("Arial", Font.PLAIN, 30));
        addFilter.setMaximumSize(new Dimension(400, 50));
        addFilter.setFocusable(false);
        addFilter.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                filterMain.setVisible(false);
                String censoredWord;
                String replacement;
                do {
                    censoredWord = JOptionPane.showInputDialog(myFrame, "Enter word/phrase for censoring",
                            "Add Filter", JOptionPane.PLAIN_MESSAGE);
                    if (censoredWord == null) {
                        break;
                    }
                    if (censoredWord.isEmpty()) {
                        JOptionPane.showMessageDialog(myFrame, "Word/Phrase cannot be empty!");
                    }
                } while (censoredWord.isEmpty());
                if (censoredWord != null) {
                    int isDefault = JOptionPane.showConfirmDialog(myFrame, "Do you want to use " +
                                    "default replacement (*)?",
                            "Default", JOptionPane.YES_NO_OPTION);
                    if (isDefault == 0) {
                        replacement = "*".repeat(censoredWord.length());
                    } else {
                        do {
                            replacement = JOptionPane.showInputDialog(myFrame, "Enter the replacement",
                                    "Add Filter", JOptionPane.PLAIN_MESSAGE);

                            if (replacement == null || replacement.isEmpty()) {
                                JOptionPane.showMessageDialog(myFrame, "Word/Phrase cannot be empty!");
                            }
                        } while (replacement == null || replacement.isEmpty());
                    }
                    boolean success = filteringSignal(0, username, censoredWord, replacement);
                    if (!success) {
                        String failMessage = "Add filter failed because either:\n\n" +
                                "1. Word/Phrase to censor already existed\n" +
                                "2. Unexpected error occurred";
                        JOptionPane.showMessageDialog(myFrame, failMessage, "Error", JOptionPane.ERROR_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(myFrame,
                                "Successfully censored " + "\"" + censoredWord + "\"", "Success",
                                JOptionPane.INFORMATION_MESSAGE);
                    }
                }
                filterMain.setVisible(true);
            }
        });


        JButton deleteFilter = new JButton("Delete a filter");
        deleteFilter.setFont(new Font("Arial", Font.PLAIN, 30));
        deleteFilter.setMaximumSize(new Dimension(400, 50));
        deleteFilter.setFocusable(false);
        deleteFilter.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ArrayList<String> censoredList = getCensoredList(username);
                String[] theList = new String[censoredList.size()];
                for (int i = 0; i < theList.length; i++) {
                    theList[i] = censoredList.get(i).split("@")[0];
                }
                filterMain.setVisible(false);
                if (censoredList.size() == 0 || censoredList == null || censoredList.get(0).isEmpty()) {
                    JOptionPane.showMessageDialog(myFrame, "This user hasn't added any words for censoring",
                            "Error", JOptionPane.ERROR_MESSAGE);
                } else {
                    String censoredWord;
                    censoredWord = (String) JOptionPane.showInputDialog(myFrame,
                            "Please choose a word to cancel censoring", "Delete Filter",
                            JOptionPane.PLAIN_MESSAGE, null, theList, theList[0]);
                    if (!(censoredWord == null)) {
                        boolean success = filteringSignal(1, username, censoredWord, "");
                        if (!success) {
                            String failMessage = "An unexpected error occurred";
                            JOptionPane.showMessageDialog(myFrame, failMessage, "Error", JOptionPane.ERROR_MESSAGE);
                        } else {
                            JOptionPane.showMessageDialog(myFrame,
                                    "Successfully uncensored " + "\"" + censoredWord + "\"", "Success",
                                    JOptionPane.INFORMATION_MESSAGE);
                        }
                    }
                }

                filterMain.setVisible(true);
            }
        });

        JButton editFilter = new JButton("Edit a filter");
        editFilter.setFont(new Font("Arial", Font.PLAIN, 30));
        editFilter.setMaximumSize(new Dimension(400, 50));
        editFilter.setFocusable(false);
        editFilter.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ArrayList<String> censoredList = getCensoredList(username);
                String[] theList = new String[censoredList.size()];
                for (int i = 0; i < theList.length; i++) {
                    theList[i] = censoredList.get(i).split("@")[0];
                }
                filterMain.setVisible(false);
                if (censoredList.size() == 0 || censoredList == null || censoredList.get(0).isEmpty()) {
                    JOptionPane.showMessageDialog(myFrame, "This user hasn't added any words for censoring",
                            "Error", JOptionPane.ERROR_MESSAGE);
                } else {
                    String censoredWord;
                    String replacement;
                    censoredWord = (String) JOptionPane.showInputDialog(myFrame,
                            "Please choose a word to edit replacement", "Edit Filter",
                            JOptionPane.PLAIN_MESSAGE, null, theList, theList[0]);
                    if (censoredWord != null) {
                        int isDefault = JOptionPane.showConfirmDialog(myFrame, "Do you want to use default" +
                                        " replacement (*)?",
                                "Default", JOptionPane.YES_NO_OPTION);
                        if (isDefault == 0) {
                            replacement = "*".repeat(censoredWord.length());
                        } else {
                            do {
                                replacement = JOptionPane.showInputDialog(myFrame, "Enter the new replacement",
                                        "Edit Filter", JOptionPane.PLAIN_MESSAGE);

                                if (replacement == null || replacement.isEmpty()) {
                                    JOptionPane.showMessageDialog(myFrame, "Word/Phrase cannot be empty!");
                                }
                            } while (replacement == null || replacement.isEmpty());
                        }
                        boolean success = filteringSignal(2, username, censoredWord, replacement);
                        if (!success) {
                            String failMessage = "Unexpected error occurred";
                            JOptionPane.showMessageDialog(myFrame, failMessage, "Error", JOptionPane.ERROR_MESSAGE);
                        } else {
                            JOptionPane.showMessageDialog(myFrame,
                                    "Successfully edit replacement to " + "\"" + replacement
                                            + "\"", "Success",
                                    JOptionPane.INFORMATION_MESSAGE);
                        }
                    }
                }
                filterMain.setVisible(true);
            }
        });

        ImageIcon bye = new ImageIcon("ImageIcon/exit.png");
        JButton exit = new JButton("Exit", bye);
        exit.setFont(new Font("Arial", Font.PLAIN, 30));
        exit.setMaximumSize(new Dimension(400, 50));
        exit.setFocusable(false);
        exit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                glassFrame.dispose();
                filterMain.removeAll();
                filterMain.setVisible(false);
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        createMessageGUI();
                        metricsFrame.revalidate();
                    }
                });
            }
        });

        filterMain.add(instruction);
        filterMain.add(addFilter);
        filterMain.add(deleteFilter);
        filterMain.add(editFilter);
        filterMain.add(exit);
        filterMain.setLocation(700, 250);
        filterMain.setVisible(true);

    }

    /**
     * creates the panel on the left side of the GUI that displays messages that have
     * already been initiated
     * also contains buttons that let you refresh chats, start new chats, and view statistics
     */
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
        System.out.println("messages " + availableMessages);

        topLeft.setLayout(null);
        topLeft.setBounds(0, 0, 165, 45);

        ImageIcon i = new ImageIcon("ImageIcon/info.png");
        Image image = i.getImage();
        Image rescaled = image.getScaledInstance(20, 20, Image.SCALE_SMOOTH);
        i = new ImageIcon(rescaled);

        topLabel3.removeAll();
        topLabel3.setText("Personal Chats:");
        topLabel3.setHorizontalTextPosition(SwingConstants.LEFT);
        topLabel3.setIcon(i);
        topLabel3.setFont(new Font("Arial", Font.BOLD, 17));
        topLabel3.setHorizontalAlignment(JLabel.CENTER);
        topLabel3.setMaximumSize(new Dimension(165, 45));
        userPanel.add(topLabel3, Component.LEFT_ALIGNMENT);

        JPanel placeholder = new JPanel();
        placeholder.setOpaque(false);
        placeholder.setBounds(140, 14, 15, 15);
        placeholder.setToolTipText("Right click to use block/invisible button");
        //placeholder.setVisible(false);
        myFrame.add(placeholder);

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
            tempButton.setMinimumSize(new Dimension(165, 50));
            tempButton.setMaximumSize(new Dimension(165, 50));
            userPanel.add(tempButton);
        }

        if (isUserSeller) {
            ArrayList<String> sellerStores = super.getStoresFromSeller(this.username);
            for (String store : sellerStores) {
                if (store.length() == 0) {
                    break;
                }
                ArrayList<String> buyerConversations = super.getConversationsFromStore(this.username, store);
                System.out.println("hi" + store + buyerConversations);
                if (buyerConversations.size() != 0) {
                    JLabel storeLabel = new JLabel(store + ":");
                    storeLabel.setFont(new Font("Arial", Font.BOLD, 25));
                    storeLabel.setMaximumSize(new Dimension(165, 30));
                    storeLabel.setHorizontalAlignment(JLabel.CENTER);
                    userPanel.add(storeLabel);
                    for (String buyer : buyerConversations) {
                        if (!availableMessages.contains(buyer) || buyer.length() == 0) {
                            continue;
                        }
                        System.out.println("this buyer name is " + buyer + "and length is " + buyer.length());
                        JButton tempButton = new JButton(buyer);
                        tempButton.setFocusable(false);
                        ActionListener tempListener = e -> {
                            if (e.getSource() == tempButton) {
                                isUserStore = true;
                                isRecipientStore = false;
                                chooseRecipient(buyer, store);
                            }
                            // TODO call client function with recipient to get message info
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
        bottomButtonPanel.setBounds(0, 590, 165, 172);
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
        ImageIcon i6 = new ImageIcon("ImageIcon/refresh.png");

        Image image6 = i6.getImage();
        Image rescaled6 = image6.getScaledInstance(20, 20, Image.SCALE_SMOOTH);
        i6 = new ImageIcon(rescaled6);

        JButton refreshButton = new JButton("Refresh Chats", i6);
        refreshButton.setFocusable(false);
        refreshButton.setMaximumSize(new Dimension(165, 56));
        refreshButton.setFocusable(false);
        bottomButtonPanel.add(refreshButton);
        refreshButton.addActionListener(e -> {
            if (e.getSource() == refreshButton) {
                myFrame.invalidate();
                createLeftPanel();
                createMessageGUI();
                myFrame.revalidate();
            }
        });

        ImageIcon i4 = new ImageIcon("ImageIcon/search.jpg");

        Image image4 = i4.getImage();
        Image rescaled4 = image4.getScaledInstance(20, 20, Image.SCALE_SMOOTH);
        i4 = new ImageIcon(rescaled4);

        JButton searchForUserButton = new JButton("Search for a " + ((isUserSeller) ? "buyer" : "seller"), i4);
        searchForUserButton.setMaximumSize(new Dimension(165, 56));
        searchForUserButton.setFocusable(false);
        bottomButtonPanel.add(searchForUserButton);
        searchForUserButton.addActionListener(e -> {
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
                    ArrayList<String> availableMessages1 = getUsersSignal(0, username, isUserSeller);
                    System.out.println("searching " + availableMessages1);
                    String newChatRecipient = JOptionPane.showInputDialog(null,
                            "Enter the name of a " + ((isUserSeller) ? "buyer:" : "seller:"),
                            "Start New Chat", JOptionPane.PLAIN_MESSAGE);
                    if (!availableMessages1.contains(newChatRecipient)) {
                        JOptionPane.showMessageDialog(null, "Sorry, no user found " +
                                "with this name!", "Error", JOptionPane.ERROR_MESSAGE);
                    } else {
                        MessageGui.super.checkIfMessageExists(newChatRecipient, isRecipientStore, isUserSeller,
                                user, isUserStore);
                        chooseRecipient(newChatRecipient, user);
                        SwingUtilities.invokeLater(() -> {
                            System.out.println("remaking");
                            myFrame.invalidate();
                            createLeftPanel();
                            myFrame.revalidate();
                        });
                    }
                }
            }
        });

        JButton seeListOfUsersButton = new JButton("See a list of " + ((isUserSeller) ? "buyers" : "stores"), i4);
        seeListOfUsersButton.setMaximumSize(new Dimension(165, 56));
        seeListOfUsersButton.setFocusable(false);
        bottomButtonPanel.add(seeListOfUsersButton);
        seeListOfUsersButton.addActionListener(e -> {
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
                    // TODO call client version of this getAvailable
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
                        SwingUtilities.invokeLater(() -> {
                            System.out.println("remaking");
                            myFrame.invalidate();
                            createLeftPanel();
                            myFrame.revalidate();
                        });
                    }
                }
            }
        });

        ImageIcon i5 = new ImageIcon("ImageIcon/statistic.png");

        Image image5 = i5.getImage();
        Image rescaled5 = image5.getScaledInstance(20, 20, Image.SCALE_SMOOTH);
        i5 = new ImageIcon(rescaled5);

        JButton metricsButton = new JButton("View Statistics", i5);
        metricsButton.setFocusable(false);
        metricsButton.setMaximumSize(new Dimension(165, 56));
        metricsButton.setFocusable(false);
        bottomButtonPanel.add(metricsButton);
        metricsButton.addActionListener(e -> {
            if (e.getSource() == metricsButton) {
                createStatisticsGUI();
            }
        });
        if (initialSetup) {
            scrollPane.setViewportView(userPanel);
            scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
            scrollPane.setBounds(0, 45, 165, 545);
            //TODO check this vvv
            scrollPane.setBounds(0, 0, 165, 590); //xyz y45  height545
            scrollPane.validate();
            c.add(scrollPane);
        }

        //Panel 4

        //top text label
        topTextPanel.setBounds(0, 0, 165, 90);

        // Panel border
        scrollPane.setBorder(BorderFactory.createLineBorder(Color.black));
        scrollPane.getVerticalScrollBar().setValue(0);
        topTextPanel.setBorder(br);
        bottomButtonPanel.setBorder(br);

        //adding the panel to the Container of the JFrame
        c.add(topTextPanel); //xyz
        c.add(bottomButtonPanel);
    }
    /**
     * creates the message box where a user can type a message and press send and clear button
     */
    public void createMessageBox() {
        popupMenu1.setVisible(false);
        Container c = myFrame.getContentPane();
        Border br = BorderFactory.createLineBorder(Color.BLACK);

        //crete panel for the text area
        JPanel textPanel = new JPanel();
        textPanel.setBounds(165, 690, 820, 71); //171
        textPanel.setBorder(br);
        textPanel.setLayout(null);

        //creating a label for the panel
        JLabel label = new JLabel("Type Message Here:");
        label.setFont(new Font("Arial", Font.BOLD, 20));
        label.setBounds(10, 10, 250, 50); //171
        textPanel.add(label);


        JTextArea textField = new JTextArea();
        //textField.setBounds(250,8,350,55); //150
        textField.setLineWrap(true);
        textField.setWrapStyleWord(true);
        JScrollPane typingSpace = new JScrollPane(textField);
        typingSpace.setBounds(220, 8, 380, 55);
        textPanel.add(typingSpace);

        ImageIcon i1 = new ImageIcon("ImageIcon/send.png");

        Image image = i1.getImage();
        Image rescaled = image.getScaledInstance(20, 20, Image.SCALE_SMOOTH);
        i1 = new ImageIcon(rescaled);

        JButton sendButton = new JButton("Send", i1);
        sendButton.setFocusable(false);
        sendButton.setLayout(null);
        sendButton.setBounds(610, 10, 100, 50); //150
        sendButton.addActionListener(e -> {
            if (e.getSource() == sendButton) {
                ArrayList<String> unblockedUsers = getUsersSignal(1, username, isUserSeller);
                System.out.println("blocked" + unblockedUsers);
                if (recipient == null) {
                    JOptionPane.showMessageDialog(null, "Please select a recipient first"
                            , "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                if (!unblockedUsers.contains(recipient)) {
                    JOptionPane.showMessageDialog(null, "Sorry, this user has blocked you"
                            , "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                if (!textField.getText().isBlank() && recipient != null) {
                    System.out.println(textField.getText().trim());
                    MessageGui.super.appendOrDeleteSignal(false, username, recipient, (storeName == null) ?
                            "nil" : storeName, !isUserSeller, textField.getText().trim());
                }
                textField.setText("");
                SwingUtilities.invokeLater(() -> {
                    myFrame.invalidate();
                    createMessageGUI();
                    myFrame.revalidate();
                });
            }
            if (!textField.getText().isBlank() && recipient != null) {
                System.out.println(textField.getText().trim());
                MessageGui.super.appendOrDeleteSignal(false, username, recipient, (storeName == null) ?
                        "nil" : storeName, !isUserSeller, textField.getText().trim());
            }
            textField.setText("");
            SwingUtilities.invokeLater(() -> {
                myFrame.invalidate();
                createMessageGUI();
                myFrame.revalidate();
            });
        });

        textPanel.add(sendButton);

        ImageIcon i = new ImageIcon("ImageIcon/clear.png");

        Image image1 = i.getImage();
        Image rescaled1 = image1.getScaledInstance(30, 30, Image.SCALE_SMOOTH);
        i = new ImageIcon(rescaled1);
        JButton clear = new JButton("Clear", i);
        clear.setIconTextGap(3);
        clear.setFocusable(false);
        clear.setLayout(null);
        clear.setBounds(710, 10, 100, 50);
        clear.addActionListener(e -> {
            if (e.getSource() == clear) {
                textField.setText("");
            }
        });
        textPanel.add(clear);
        c.add(textPanel);
    }

    /**
     * creates the top panel of the GUI that displays who has been connected with
     * also contains the button for the settings
     */
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
        connectedLabel.setFont(new Font("Arial", Font.BOLD, 25));
        connectedLabel.setBounds(0, 0, 600, 45);

        ImageIcon j = new ImageIcon("ImageIcon/settings.png");
        Image image9 = j.getImage();
        Image rescaled9 = image9.getScaledInstance(40, 40, Image.SCALE_SMOOTH);
        j = new ImageIcon(rescaled9);

        JLabel setting = new JLabel(j);
        setting.setBounds(770, 5, 40, 40);

        JButton settingButton = new JButton();
        settingButton.setFocusable(false);
        settingButton.setBounds(770, 8, 40, 40);
        settingButton.setOpaque(false);
        settingButton.setContentAreaFilled(false);
        settingButton.setBorderPainted(false);
        settingButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (e.getSource() == settingButton) {
                    createPopUpSetting();
                }
            }
        });


        ImageIcon i = new ImageIcon("ImageIcon/info.png");
        Image image = i.getImage();
        Image rescaled = image.getScaledInstance(40, 40, Image.SCALE_SMOOTH);
        i = new ImageIcon(rescaled);
        JLabel info = new JLabel(i);
        info.setToolTipText("Right click on a message to edit");
        info.setHorizontalAlignment(SwingConstants.RIGHT);
        info.setBounds(720, 8, 40, 30);
        connectedLabel.setHorizontalAlignment(JLabel.LEFT);
        connectedLabel.setBorder(new EmptyBorder(10, 10, 10, 10));
        if (initialSetup) {
            topPanel.add(connectedLabel);
        }
        topPanel.add(info);
        topPanel.add(settingButton);
        topPanel.add(setting);

        ImageIcon i2 = new ImageIcon("ImageIcon/import.png");

        Image image2 = i2.getImage();
        Image rescaled2 = image2.getScaledInstance(30, 30, Image.SCALE_SMOOTH);
        i2 = new ImageIcon(rescaled2);

        JButton importFileButton = new JButton("Import a File", i2);
        importFileButton.setLayout(null);
        importFileButton.setBounds(0, 45, 410, 45);
        importFileButton.setFocusable(false);
        importFileButton.addActionListener(e -> {
            if (recipient != null) {
                if (e.getSource() == importFileButton) {
                    String filename = getImportFile();
                    if (!(filename == null) && !filename.endsWith(".txt")) {
                        JOptionPane.showMessageDialog(null, "File must be a text file",
                                "Invalid File", JOptionPane.ERROR_MESSAGE);
                    } else if (filename != null) {
                        System.out.println("here with " + recipient + userPanel + isRecipientStore);
                        MessageGui.super.importFile(filename, isRecipientStore ? storeName : recipient,
                                isUserStore ? storeName : username, isUserSeller,
                                isUserStore, isRecipientStore);
                        SwingUtilities.invokeLater(() -> {
                            createMessageGUI();
                            myFrame.revalidate();
                        });
                    }
                }
            } else {
                JOptionPane.showMessageDialog(null, "Please choose a recipient first"
                        , "Error", JOptionPane.ERROR_MESSAGE);
            }

        });
        topPanel.add(importFileButton);

        ImageIcon i3 = new ImageIcon("ImageIcon/export.png");

        Image image3 = i3.getImage();
        Image rescaled3 = image3.getScaledInstance(20, 20, Image.SCALE_SMOOTH);
        i3 = new ImageIcon(rescaled3);

        JButton exportFileButton = new JButton("Choose directory to export conversation as CSV File", i3);
        exportFileButton.setLayout(null);
        exportFileButton.setFocusable(false);
        exportFileButton.setBounds(410, 45, 410, 45);
        exportFileButton.addActionListener(e -> {
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
                        MessageGui.super.exportFile(isRecipientStore ? storeName : recipient
                                , isUserStore ? storeName : username, isUserSeller, isUserStore, directory);
                        JOptionPane.showMessageDialog(null, "Export Successful");
                    } else {
                        JOptionPane.showMessageDialog(null, "Please choose a recipient first"
                                , "Error", JOptionPane.ERROR_MESSAGE);
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
                    (storeName == null) ? "nil" : storeName, !isUserSeller);

            for (String s : messages) {
                int numLines = 1 + s.length() / 145; // sets a factor for how many lines are needed
                JTextArea tempLabel = new JTextArea(s);
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
                            deleteOption.addActionListener(e1 -> {
                                if (e1.getSource() == deleteOption) {
                                    popupMenu1.setVisible(false);
                                    MessageGui.super.appendOrDeleteSignal(true, username, recipient,
                                            (storeName == null) ? "nil" : storeName, !isUserSeller,
                                            tempLabel.getText());
                                    SwingUtilities.invokeLater(() -> {
                                        myFrame.invalidate();
                                        createMessageGUI();
                                        myFrame.revalidate();
                                    });
                                }
                            });

                            editOption.setLayout(null);
                            editOption.setMaximumSize(new Dimension(74, 28));
                            for (ActionListener al : editOption.getActionListeners()) {
                                editOption.removeActionListener(al);
                            }
                            editOption.addActionListener(e12 -> {
                                if (e12.getSource() == editOption) {
                                    popupMenu1.setVisible(false);
                                    String newMessage = JOptionPane.showInputDialog("Current Message: " +
                                            tempLabel.getText().substring(s.indexOf("-") + 2) +
                                            "\nWhat would you like the new " +
                                            "message to say?");
                                    if (newMessage != null) {
                                        MessageGui.super.editSignal(false, username, recipient,
                                                (storeName == null) ? "nil" : storeName, !isUserSeller,
                                                tempLabel.getText(), newMessage);
                                    }
                                    SwingUtilities.invokeLater(() -> {
                                        myFrame.invalidate();
                                        createMessageGUI();
                                        myFrame.revalidate();
                                    });
                                }
                            });

                            cancelOption.setLayout(null);
                            cancelOption.setMaximumSize(new Dimension(74, 28));
                            cancelOption.addActionListener(e13 -> {
                                if (e13.getSource() == cancelOption) {
                                    popupMenu1.setVisible(false);
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
            messagePanel.setBounds(170, 90, 820, 600);
            messagePanel.setBorder(BorderFactory.createLineBorder(Color.white));
            c.add(messagePanel);
        }
    }

    /**
     * a helper method to show window to choose a path
     * @return is the path selected in String form
     */
    public String getImportFile() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
        int result = fileChooser.showOpenDialog(myFrame);
        if (result == JFileChooser.APPROVE_OPTION) {
            return fileChooser.getSelectedFile().getAbsolutePath();
        }
        return null;
    }

    /**
     * this method just sets the recipient to right value and adjusts other values
     * @param recipient the new recipient
     * @param storeName the new storename (null if store is not involved)
     */
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

        SwingUtilities.invokeLater(() -> {
            createTopPanel();
            createMessageGUI();
            myFrame.revalidate();
        });

    }

    /**
     * a constructor used to set up the message gui and the client
     * @param username the user name
     * @param isUserSeller whether or not user is a seller
     * @param socket the socket used to connect with server
     */
    public MessageGui(String username, boolean isUserSeller, Socket socket) {
        super(socket);
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
        }
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

    /**
     * a helper method to create the popup for the filter
     */
    public void createPopUpSetting() {
        glassFrame.setBounds(myFrame.getX(), myFrame.getY(), myFrame.getWidth(), myFrame.getHeight());
        glassFrame.setResizable(false);
        glassFrame.setBackground(Color.BLACK);
        JButton filter = new JButton("Message Filter");
        filter.setFont(new Font("Arial", Font.PLAIN, 30));
        filter.setMaximumSize(new Dimension(400, 50));
        filter.setFocusable(false);
        filter.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                createFilterMain();
            }
        });

        JButton vanish = new JButton("Vanish Mode");
        vanish.setFont(new Font("Arial", Font.PLAIN, 30));
        vanish.setMaximumSize(new Dimension(400, 50));
        vanish.setFocusable(false);
        vanish.setToolTipText("Feature coming in next update!");

        ImageIcon bye = new ImageIcon("ImageIcon/exit.png");
        JButton exit = new JButton("Exit", bye);
        exit.setFont(new Font("Arial", Font.PLAIN, 30));
        exit.setMaximumSize(new Dimension(400, 50));
        exit.setFocusable(false);
        exit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                glassFrame.dispose();
                popUpSetting.removeAll();
                popUpSetting.setVisible(false);
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        createMessageGUI();
                        metricsFrame.revalidate();
                    }
                });
            }
        });

        popUpSetting.add(filter);
        popUpSetting.add(vanish);
        popUpSetting.add(exit);
        popUpSetting.setLocation(myFrame.getX() + 400, myFrame.getY() + 290);
        popUpSetting.setMaximumSize(new Dimension(800, 50));
        popUpSetting.setMinimumSize(new Dimension(800, 50));
        popUpSetting.setVisible(true);

        glassFrame.dispose();
        glassFrame.setUndecorated(true);
        glassFrame.setOpacity(0.9f);
        glassFrame.setVisible(true);

        //myFrame.add(popUpSetting);
    }

    /**
     * a helper method to create a popup for blocking and invisible
     * @param e the mouse event
     * @param receiver the receiver
     */
    public void createPopUpBlockInvisible(MouseEvent e, String receiver) {
        popupMenu1.setVisible(false);
        invisibleOption.setMaximumSize(new Dimension(200, 28));

        for (ActionListener al : invisibleOption.getActionListeners()) {
            invisibleOption.removeActionListener(al);
        }
        invisibleOption.addActionListener(e1 -> {
            if (e1.getSource() == invisibleOption) {
                popupMenu2.setVisible(false);
                System.out.println("invisible");
                // TODO call make invisible
                sendBlockInvisibleSignal("invisible", username, Boolean.toString(isUserSeller), receiver);
            }
        });

        becomeVisibleOption.setMaximumSize(new Dimension(200, 28));

        for (ActionListener al : becomeVisibleOption.getActionListeners()) {
            becomeVisibleOption.removeActionListener(al);
        }
        becomeVisibleOption.addActionListener(e12 -> {
            if (e12.getSource() == becomeVisibleOption) {
                popupMenu2.setVisible(false);
                System.out.println("visible");
                // TODO call make visible again
                sendBlockInvisibleSignal("visible", username, Boolean.toString(isUserSeller), receiver);
            }
        });

        blockOption.setLayout(null);
        blockOption.setMaximumSize(new Dimension(200, 28));
        for (ActionListener al : blockOption.getActionListeners()) {
            blockOption.removeActionListener(al);
        }
        blockOption.addActionListener(e13 -> {
            if (e13.getSource() == blockOption) {
                popupMenu2.setVisible(false);
                System.out.println("blocking");
                // TODO block the user
                sendBlockInvisibleSignal("block", username, Boolean.toString(isUserSeller), receiver);
            }
        });

        unblockOption.setLayout(null);
        unblockOption.setMaximumSize(new Dimension(200, 28));
        for (ActionListener al : unblockOption.getActionListeners()) {
            unblockOption.removeActionListener(al);
        }
        unblockOption.addActionListener(e14 -> {
            if (e14.getSource() == unblockOption) {
                popupMenu2.setVisible(false);
                System.out.println("unblocking");
                // TODO unblock the user
                sendBlockInvisibleSignal("unblock", username, Boolean.toString(isUserSeller), receiver);
            }
        });

        cancelOption.setLayout(null);
        cancelOption.setMaximumSize(new Dimension(200, 28));
        cancelOption.addActionListener(e15 -> {
            if (e15.getSource() == cancelOption) {
                popupMenu2.setVisible(false);
            }
        });
        boolean isBlocked = isBlockedOrCannotSee(0, username, Boolean.toString(isUserSeller),
                Boolean.toString(isRecipientStore), receiver);
        boolean cantSeeThisUser = isBlockedOrCannotSee(1, username, Boolean.toString(isUserSeller),
                Boolean.toString(isRecipientStore), receiver);

        if (isBlocked) {
            popupMenu2.remove(blockOption);
            popupMenu2.add(unblockOption);
        } else {
            popupMenu2.remove(unblockOption);
            popupMenu2.add(blockOption);
        }
        if (cantSeeThisUser) {
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

    /**
     * Method that creates the GUI for statistics
     *
     * @author Kevin Jones, all buyers GUI and sorting part of sellers
     * @author John Brooks, design of sellers GUI
     */
    public void createStatisticsGUI() {
        metricsFrame.setTitle("Statistics");
        metricsFrame.setLayout(null);
        //setting the bounds for the JFrame
        metricsFrame.setBounds(500, 100, 600, 600);
        metricsFrame.setResizable(false);

        if (isUserSeller) {
            popupMenu1.setVisible(false);
            popupMenu2.setVisible(false);
            //TODO seller statistics gui
            JPanel textPanel = new JPanel();
            textPanel.setLayout(null);
            textPanel.setBounds(0, 0, 600, 50);

            JLabel label1 = new JLabel("Messages per Customer");
            label1.setFont(new Font("Arial", Font.BOLD, 12));
            label1.setBounds(20, 0, 250, 50);
            label1.setHorizontalAlignment(JLabel.CENTER);


            Map attributes = label1.getFont().getAttributes();
            attributes.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
            label1.setFont(label1.getFont().deriveFont(attributes));

            JButton sortNames = new JButton("▲");
            sortNames.setBounds(215, 15, 20, 20);
            sortNames.setMargin(new Insets(0, -1, 0, 0));
            sortNames.setFocusPainted(false);
            sortNames.addActionListener(e -> {
                if (e.getSource() == sortNames) {
                    if (sortNames.getText().equals("▲")) {
                        sortNames.setText("▼");
                        this.sortType = 1;
                    } else {
                        sortNames.setText("▲");
                        this.sortType = 0;
                    }
                }
                SwingUtilities.invokeLater(() -> {
                    createStatisticsGUI();
                    metricsFrame.revalidate();
                });
            });

            JLabel label2 = new JLabel("Most Common Overall Words");
            label2.setFont(new Font("Arial", Font.BOLD, 12));
            label2.setBounds(280, 0, 225, 50);
            label2.setHorizontalAlignment(JLabel.RIGHT);

            JButton sortTotal = new JButton("▲");
            sortTotal.setBounds(520, 15, 20, 20);
            sortTotal.setMargin(new Insets(0, -1, 0, 0));
            sortTotal.setFocusPainted(false);
            sortTotal.addActionListener(e -> {
                if (e.getSource() == sortTotal) {
                    if (Objects.equals(sortTotal.getText(), "▲")) {
                        sortTotal.setText("▼");
                        sortWords = 1;
                    } else {
                        sortTotal.setText("▲");
                        sortWords = 0;
                    }
                    SwingUtilities.invokeLater(() -> {
                        createStatisticsGUI();
                        metricsScroll.revalidate();
                    });
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

            Box labelBox2 = Box.createVerticalBox();

            labelBox1.removeAll();

            System.out.println(sortType);
            ArrayList<String[]> metricsData = super.sortMetricsData(this.username, this.sortType, true);

            // TODO call client for common words
            String[] data4 = super.getMostCommonWords(this.username, sortWords);
            System.out.println("common words " + Arrays.toString(data4));
            for (String[] s : metricsData) {
                JPanel tempPanel = new JPanel();
                textPanel.setLayout(null);

                JLabel labelStore = new JLabel(s[0]);
                labelStore.setMaximumSize(new Dimension(90, 50));
                labelStore.setHorizontalAlignment(JLabel.CENTER);
                labelStore.setFont(new Font("Arial", Font.BOLD, 20));

                tempPanel.add(labelStore);
                labelBox1.add(tempPanel);

                for (int i = 1; i < s.length; i++) {
                    JPanel tempPanel2 = new JPanel();
                    textPanel.setLayout(null);

                    JLabel labelCustomer = new JLabel(s[i]);
                    //labelCustomer.setMaximumSize(new Dimension(60, 20));
                    labelCustomer.setHorizontalAlignment(JLabel.CENTER);
                    //labelCustomer.setLocation(0, 0);
                    labelCustomer.setFont(new Font("Arial", Font.PLAIN, 13));

                    tempPanel2.add(labelCustomer);
                    labelBox1.add(tempPanel2);
                }
            }

            textPanel.setLayout(null);

            for (String s : data4) {
                JPanel tempPanel4 = new JPanel();
                textPanel.setLayout(null);

                JLabel labelListOfWords = new JLabel(s);
                labelListOfWords.setMaximumSize(new Dimension(90, 50));
                labelListOfWords.setHorizontalAlignment(JLabel.CENTER);
                labelListOfWords.setLocation(0, 0);
                labelListOfWords.setFont(new Font("Arial", Font.BOLD, 15));

                tempPanel4.add(labelListOfWords);
                labelBox2.add(tempPanel4);
            }

            metricsPanel.setViewportView(labelBox1);
            metricsPanel.setBounds(0, 50, 300, 510);
            metricsPanel.setBorder(BorderFactory.createLineBorder(Color.white));
            metricsPanel2.setViewportView(labelBox2);
            metricsPanel2.setBounds(300, 50, 285, 510);
            metricsPanel2.setBorder(BorderFactory.createLineBorder(Color.white));
            metricsFrame.add(metricsPanel);
            metricsFrame.add(metricsPanel2);

        } else {
            popupMenu1.setVisible(false);
            popupMenu2.setVisible(false);

            JPanel textPanel = new JPanel();
            textPanel.setLayout(null);
            textPanel.setBounds(0, 0, 600, 50);

            JLabel label1 = new JLabel("Store Name");
            label1.setFont(new Font("Arial", Font.BOLD, 12));
            label1.setBounds(0, 0, 90, 50);
            label1.setHorizontalAlignment(JLabel.CENTER);


            Map<TextAttribute, Integer> attributes = (Map<TextAttribute, Integer>) label1.getFont().getAttributes();
            attributes.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
            label1.setFont(label1.getFont().deriveFont(attributes));

            JButton sortNames = new JButton("▲");
            sortNames.setBounds(90, 15, 20, 20);
            sortNames.setMargin(new Insets(0, -1, 0, 0));
            sortNames.setFocusPainted(false);
            sortNames.addActionListener(e -> {
                if (e.getSource() == sortNames) {
                    if (sortNames.getText().equals("▲")) {
                        sortNames.setText("▼");
                        sortType = 1;
                    } else {
                        sortNames.setText("▲");
                        sortType = 0;
                    }
                    SwingUtilities.invokeLater(() -> {
                        createStatisticsGUI();
                        metricsFrame.revalidate();
                    });
                }
            });

            JLabel label2 = new JLabel("Total Messages Received by Store");
            label2.setFont(new Font("Arial", Font.BOLD, 12));
            label2.setBounds(100, 0, 225, 50);
            label2.setHorizontalAlignment(JLabel.RIGHT);

            JButton sortTotal = new JButton("▲");
            sortTotal.setBounds(330, 15, 20, 20);
            sortTotal.setMargin(new Insets(0, -1, 0, 0));
            sortTotal.setFocusPainted(false);
            sortTotal.addActionListener(e -> {
                if (e.getSource() == sortTotal) {
                    if (Objects.equals(sortTotal.getText(), "▲")) {
                        sortTotal.setText("▼");
                        sortType = 2;
                    } else {
                        sortTotal.setText("▲");
                        sortType = 3;
                    }
                    SwingUtilities.invokeLater(() -> {
                        createStatisticsGUI();
                        metricsFrame.revalidate();
                    });
                }
            });

            attributes = (Map<TextAttribute, Integer>) label2.getFont().getAttributes();
            attributes.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
            label2.setFont(label2.getFont().deriveFont(attributes));

            JLabel label3 = new JLabel("Number of Messages Sent to Store");
            label3.setFont(new Font("Arial", Font.BOLD, 12));
            label3.setBounds(350, 0, 225, 50);
            label3.setHorizontalAlignment(JLabel.CENTER);

            JButton sortPersonal = new JButton("▼");
            sortPersonal.setBounds(560, 15, 20, 20);
            sortPersonal.setMargin(new Insets(0, -1, 0, 0));
            sortPersonal.setFocusPainted(false);
            sortPersonal.addActionListener(e -> {
                if (e.getSource() == sortPersonal) {
                    if (Objects.equals(sortPersonal.getText(), "▲")) {
                        sortPersonal.setText("▼");
                        sortType = 4;
                    } else {
                        sortPersonal.setText("▲");
                        sortType = 5;
                    }
                    SwingUtilities.invokeLater(() -> {
                        metricsFrame.invalidate();
                        createStatisticsGUI();
                        metricsFrame.revalidate();
                    });
                }
            });
            attributes = (Map<TextAttribute, Integer>) label3.getFont().getAttributes();
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
            ArrayList<String[]> metricsData = MessageGui.super.sortMetricsData(this.username, sortType, false);
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
                labelStore.setFont(new Font("Arial", Font.BOLD, 20));

                JLabel labelTotal = new JLabel(s[1]);
                labelTotal.setHorizontalAlignment(JLabel.CENTER);
                labelTotal.setBounds(110, 0, 225, 50);
                labelTotal.setFont(new Font("Arial", Font.BOLD, 20));

                JLabel labelPersonal = new JLabel(s[2]);
                labelPersonal.setHorizontalAlignment(JLabel.CENTER);
                labelPersonal.setBounds(350, 0, 225, 50);
                labelPersonal.setFont(new Font("Arial", Font.BOLD, 20));


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

    /**
     * the run method that creates the initial GUI
     */
    public void run() {
        createLeftPanel();
        createMessageBox();
        createTopPanel();
        createMessageGUI();
        myFrame.setVisible(true);
        initialSetup = false;
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