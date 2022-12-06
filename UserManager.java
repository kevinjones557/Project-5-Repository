import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

/**
 * A class that deals with user account management on data side, editing/deleting information
 * @author Kevin Jones
 * @version 12/05/2022
 */
public class UserManager {
    /**
     * A static method that will change the names of files and directories to match username
     *
     * @param username the username that is currently stored everywhere
     * @author Kevin Jones
     */
    public synchronized static void deleteUsername(String username) {
        try {
            File f = new File(FileManager.getDirectoryFromUsername(username));
            String[] allFiles = f.list();
            for (String file : allFiles) {
                if (Files.isDirectory(Paths.get(FileManager.getDirectoryFromUsername(username)
                        + "/" + file))) {
                    File fs = new File(FileManager.getDirectoryFromUsername(username)
                            + "/" + file);
                    String[] storeFiles = fs.list();
                    assert storeFiles != null;
                    for (String storeFile : storeFiles) {
                        Files.delete(Paths.get((FileManager.getDirectoryFromUsername(username) + "/" + file +
                                "/" + storeFile)));
                    }
                }
                Files.delete(Paths.get((FileManager.getDirectoryFromUsername(username)
                        + "/" + file)));
            }
            Files.delete(Paths.get(FileManager.getDirectoryFromUsername(username)));
        } catch (UserNotFoundException e) {
            System.out.println("Sorry, couldn't delete this user.");
        } catch (IOException io) {
            io.printStackTrace();
        }
    }

    /**
     * A static method that will change the names of files and directories to match username
     *
     * @param oldUsername the username that is currently stored everywhere
     * @param newUsername the new username that everything will be changed to
     * @author Kevin Jones
     */
    public synchronized static void changeUsername(String oldUsername, String newUsername) {
        File sellerDirectories = new File("data/sellers/");
        File buyerDirectories = new File("data/buyers/");
        String[] sellers = sellerDirectories.list();
        String[] buyers = buyerDirectories.list();
        assert sellers != null;
        for (String seller : sellers) {
            File currentSeller = new File("data/sellers/" + seller);
            String[] allFiles = currentSeller.list();
            if (!(allFiles == null)) {
                for (String filename : allFiles) {
                    File possibleStore = new File("data/sellers/" + seller + "/" + filename);
                    if (possibleStore.isDirectory()) {
                        String[] storeFiles = possibleStore.list();
                        if (!(storeFiles == null)) {
                            for (String storeFile : storeFiles) {
                                int indexOldUsername = storeFile.indexOf(oldUsername);
                                if (indexOldUsername >= 0) {
                                    changeNameInFile(oldUsername, newUsername, "data/sellers/" + seller + "/"
                                            + filename + "/" + storeFile);
                                    String newFilename;
                                    newFilename = filename.substring(0, indexOldUsername) + newUsername + ".txt";
                                    try {
                                        Files.move(Paths.get("data/sellers/" + seller + "/"
                                                + filename + "/" + storeFile), Paths.get("data/sellers/" + seller + "/"
                                                + filename + "/" + newFilename));
                                    } catch (IOException e) {
                                        System.out.println("Sorry, failed to rename user!");
                                    }
                                }
                            }
                        }
                    } else {
                        int indexOldUsername = filename.indexOf(oldUsername);
                        if (indexOldUsername >= 0) {
                            changeNameInFile(oldUsername, newUsername, "data/sellers/" + seller + "/" + filename);
                            String newFilename;
                            if (indexOldUsername == 0) {
                                newFilename = newUsername + filename.substring(oldUsername.length());
                            } else {
                                newFilename = filename.substring(0, indexOldUsername) + newUsername + ".txt";
                            }
                            try {
                                Files.move(Paths.get("data/sellers/" + seller + "/" + filename),
                                        Paths.get("data/sellers/"
                                                + seller + "/" + newFilename));
                            } catch (IOException e) {
                                System.out.println("Sorry, failed to rename user!");
                            }
                        }
                    }
                }
            }
            if (seller.equals(oldUsername)) {
                try {
                    Files.move(Paths.get("data/sellers/" + seller),
                            Paths.get("data/sellers/" + newUsername));
                } catch (IOException e) {
                    System.out.println("Sorry, failed to rename user!");
                }

            }
        }
        assert buyers != null;
        for (String buyer : buyers) {
            File currentBuyer = new File("data/buyers/" + buyer);
            String[] allFiles = currentBuyer.list();
            if (!(allFiles == null)) {
                for (String filename : allFiles) {
                    int indexOldUsername = filename.indexOf(oldUsername);
                    if (indexOldUsername >= 0) {
                        changeNameInFile(oldUsername, newUsername, "data/buyers/" + buyer + "/" + filename);
                        String newFilename;
                        if (indexOldUsername == 0) {
                            newFilename = newUsername + filename.substring(oldUsername.length());
                        } else {
                            newFilename = filename.substring(0, indexOldUsername) + newUsername + ".txt";
                        }
                        try {
                            Files.move(Paths.get("data/buyers/" + buyer + "/" + filename),
                                    Paths.get("data/buyers/" + buyer + "/" + newFilename));
                        } catch (IOException e) {
                            System.out.println("Sorry, failed to rename user!");
                        }
                    }
                }
            }
            if (buyer.equals(oldUsername)) {
                try {
                    Files.move(Paths.get("data/buyers/" + buyer),
                            Paths.get("data/buyers/" + newUsername));
                } catch (IOException e) {
                    System.out.println("Sorry, failed to rename user!");
                }
            }
        }
        /* at this point the directory has been renamed,
        now go through all directories, find files that contain old
        username, go through files and change all the names and rename files */
    }

    public static void changeStoreName(String oldStoreName, String newStoreName) {
        File sellerDirectories = new File("data/sellers/");
        File buyerDirectories = new File("data/buyers/");
        String[] sellers = sellerDirectories.list();
        String[] buyers = buyerDirectories.list();
        assert buyers != null;
        for (String buyer : buyers) {
            File currentBuyer = new File("data/buyers/" + buyer);
            String[] allFiles = currentBuyer.list();
            if (!(allFiles == null)) {
                for (String filename : allFiles) {
                    int indexOldStoreName = filename.indexOf(oldStoreName);
                    if (indexOldStoreName >= 0) {
                        changeNameInFile(oldStoreName, newStoreName,
                                "data/buyers/" + buyer + "/" + filename);
                        String newFilename;
                        newFilename = filename.substring(0, indexOldStoreName) + newStoreName + ".txt";
                        try {
                            Files.move(Paths.get("data/buyers/" + buyer + "/" + filename),
                                    Paths.get("data/buyers/" + buyer + "/" + newFilename));
                        } catch (IOException e) {
                            System.out.println("Sorry, failed to rename user!");
                        }
                    }
                }
            }
        }
        assert sellers != null;
        for (String seller : sellers) {
            File currentSeller = new File("data/sellers/" + seller);
            String[] allFiles = currentSeller.list();
            if (!(allFiles == null)) {
                for (String filename : allFiles) {
                    File possibleStore = new File("data/sellers/" +
                            seller + "/" + filename);
                    if (possibleStore.isDirectory() && filename.equals(oldStoreName)) {
                        String[] storeFiles = possibleStore.list();
                        if (!(storeFiles == null)) {
                            for (String storeFile : storeFiles) {
                                int indexOldUsername = storeFile.indexOf(oldStoreName);
                                if (indexOldUsername >= 0) {
                                    changeNameInFile(oldStoreName, newStoreName, "data/sellers/" +
                                            seller + "/" + filename + "/" + storeFile);
                                    String newFilename;
                                    newFilename = newStoreName + storeFile.substring(oldStoreName.length());
                                    try {
                                        Files.move(Paths.get("data/sellers/" + seller + "/"
                                                        + filename + "/" + storeFile),
                                                Paths.get("data/sellers/" + seller + "/"
                                                        + filename + "/" + newFilename));
                                    } catch (IOException e) {
                                        System.out.println("Sorry, failed to rename user!");
                                    }
                                }
                            }
                        }
                        try {
                            Files.move(Paths.get("data/sellers/" + seller + "/" + oldStoreName),
                                    Paths.get("data/sellers/" + seller + "/" + newStoreName));
                        } catch (Exception e) {
                            System.out.println("Sorry, unable to rename store!");
                        }
                    }
                }
            }
        }
    }
    /**
     * A static method that will go through given file and replace old username with new username
     *
     * @param oldUsername the username that is currently stored everywhere
     * @param newUsername the new username that everything will be changed to
     * @param filepath    the file path of the file being modified
     * @author Kevin Jones
     */

    public synchronized static void changeNameInFile(String oldUsername, String newUsername, String filepath) {
        try (BufferedReader bfr = new BufferedReader(new FileReader(filepath))) {
            String line = bfr.readLine();
            ArrayList<String> fileContents = new ArrayList<>();
            while (line != null && !line.isBlank()) {
                if (line.substring(0, oldUsername.length()).equals(oldUsername)) {
                    line = newUsername + line.substring(oldUsername.length());
                }
                fileContents.add(line);
                line = bfr.readLine();
            }
            PrintWriter pw = new PrintWriter(new FileOutputStream(filepath, false));
            for (String fileLine : fileContents) {
                pw.println(fileLine);
            }
            pw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
