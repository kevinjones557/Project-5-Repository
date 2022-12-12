# Project-5-Repository

## Running
To run the program, compile and run Server.java first. This creates a server with connections on port 2000 by default. Then, compile and run LogInGui. This connects to the server on port 2000. Then, use the user interface to log in or create an account. After successfully logging in, the user can choose to edit account details or proceed to messaging.

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

###### Blocking
The Blocking class contains static methods used to get and edit data regarding Blocking features.

###### Invisible
The Invisible class contains static methods used to get and edit data regarding Invisible features.

###### Filtering
The Filtering class contains static methods used to change data in files relating to Message Filtering feature.

###### FileManager
The FileManager class contains static methods used to handle creation and changes of the project's data files.

###### StatisticsManager
/Write something here Kevin

###### UserManager
/Write something here Kevin

###### LogIn
/Write something here Aden

###### LogInGUI
/Write something here Aden

###### UserNotFoundException
/Write something here Destin or remove it if found unnecessary

