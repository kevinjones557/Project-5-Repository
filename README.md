# Project-5-Repository

## Running
To run the program, compile and run Server.java first. This creates a server with connections on port 2000 by default. Then, compile and run LogInGui. This connects to the server on port 2000. Then, use the user interface to log in or create an account.

## Submission

## Classes
###### Server
The Server class handles all server-sided processing for the program, including file creation, reading, and writing. It is threaded and creates a new thread for each client connected. This class calls Message, Filtering, Blocking, UserManager, and Invisible.

###### Client
The Client class contains various methods for communication. It is extended by MessageGUI.

###### MessageGui
The MessageGui class contains the GUI used to message and interact with the other users of the program. It extends Client.

###### Message
The Message class contains static methods used to handle messages sent by users on the server. It calls MetricManager, and is called in Server.

###### MetricManager
The MetricManager class contains static methods used to handle user metrics and writing and changing files accordingly. It calls FileManager and is called in Message.
