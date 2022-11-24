# Project-5-Repository

OUTLINE FOR NOW:

The LogIn Class will create an instance of the UserThread class for each new user logged in and will then start that thread. It will hand the UserThread class the name of the user and other boolean values to determine information.

The UserClass then sets up the GUI using the invokelater() method. It will hand the Gui the information passed to it. The GUI is the child class of the Client class. The Client class will set up a socket. When the GUI recieves input through text or button clicks it will call methods from the client class that send a String to the server class telling it what to do. In the Server class, it will call methods such as those in Message that will read/write to files and handle the data. It will then send back information to the client through an OutputStream. The Client will then hand that information back to the GUI.
