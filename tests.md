Project 5 Test Cases
All of these tests assume the server is running.
Test 1: User sign up
Steps:
User launches application.
User selects the sign up button.
User selects the buyer button.
User selects the username textbox.
User enters username that does not meet username requirements such as “user one” or “user1” with the keyboard and selects ok.
User presses ok on warning.
User selects the reprompt box and enters a valid username.
User selects password textbox.
User enters their password and selects ok.
User selects the password confirmation textbox.
User enters a password that does not match the previously entered one and selects ok.
User selects the password textbox.
User enters their password that doesn’t meet the requirements such as “pw” or “Pw1” and selects ok.
User presses ok on warning.
User is reprompted and enters a valid password and selects ok.
User selects the password confirmation textbox and reenters their password correctly.
User selects email textbox and enters an invalid email like “email” or “email.com”
User presses ok on warning.
User is prompted again and enters a valid email.
User selects the user interaction button.
Expected result: Application prompts user logging in for credentials that match the requirements until suitable ones are entered, and the application successfully opens their messaging interface. 
Test Status: Passed. 
Test 2: Other User Sign Up (Seller)
(features that have already been tested will be condensed)
Steps:
Sign up as seller, hit seller button, create username and password.
Select store textbox.
Enter invalid store name such as “st” or “st1”.
Press ok on warning.
Enter valid store name into text box such as “store” or “store1”.
Select no button to not add the current store.
Select the yes button to proceed to adding another store.
Enter another valid store name into the text box and select ok.
Select yes to add another store. 
Enter in a valid store name, select ok, and select yes to add.
Select yes to add another store.
In store text box, use same name as previously successfully added store and select ok.
Select ok on warning.
Select the button to add another store.
Enter a valid store name into the text box and select ok.
Confirm adding the store and select yes.
Select the no button on the adding another store prompt.
Enter a valid email.
Select User Interaction.
Expected Result: Application handles any wrong store input and allows a seller to proceed to the messaging interface.
Test Status: passed

Test 3: log in and message
Steps:
Launch Program.
Select log in button.
Enter username that does not exist in either of the tests into the box.
Select ok on warning.
Enter previously test created buyer into box.
Enter the corresponding password.
Select button to proceed to user interaction.
Open up a separate instance of the application.
Log in as the previously created seller and proceed to user interaction.
In the buyer’s message window, select the button to search for a seller.
Search for the seller that is currently in the other instance.
Type a message into the message box.
Select the clear button next to the send button.
Type another message into the box, and select the send button.
On the seller’s message window, select the refresh chats button.
Select the chat with the buyer on the sellers window.
Send the buyer a message.
Click the refresh chats button in the buyer window.
Expected Result: Both users are able to successfully login and reach the messaging window. The buyer searching for the seller results in the top panel saying they are connected with the seller and their chat appearing on the buyer’s left panel. The message appears on the buyer’s window after they click send, and refreshing the chats in the seller window causes the chat to appear, and the message the buyer sent is there.
Test Status: passed

Test 4: Editing, Deleting, and Store interaction
Steps:
Log in to the program as the buyer in one instance.
Log in as the seller in another instance.
Select the see a list of stores button as the buyer.
Select one of the stores belonging to the seller in the other instance.
Select its button on the left panel.
Send a message.
Refresh chats for the seller’s window, and send a message back to the buyer as the store.
Refresh chats in the buyer window.
Right click the buyer’s first message in the buyer window.
Select edit and change the message to something else.
Refresh chats in the seller window.
Right click the buyer’s new first message in the seller window and delete it.
Refresh chats in the buyer window.
In the seller window, select the personal chat with the buyer from seller to buyer.
Edit a message and delete that message.
Refresh chats in the buyer window.
Expected Result: Both users successfully login. The buyer is successfully connected with the seller’s store. The buyer’s message is available on both windows after refreshing. The store’s following message is also available on both windows after refreshing. The buyer’s edit appears in both windows after refreshing. The deleted message is still present in the buyer window after refreshing, but gone for the store that deleted it. The seller to buyer chat has the edited message in the buyer window after refreshing, but the message is still there. In the seller’s window, it is deleted.
Test Status: passed

Test 5: Blocking/Invisible features (GUI)
Steps:
User launches Program and uses a valid pair of credentials to log in. Make sure if user logs in as a buyer at least a seller exists, and vice versa.
User selects “User Interaction”
User moves the mouse on a button with another user’s name on the left panel
User right-clicks the mouse
Expected result:  A small menu pops up where the mouse is. The menu consists of 3 buttons: the top one either says “Block User” or “Unblock User”, the second one either says “Become Invisible to User” or “Become Visible to User”, and the last one says “Cancel”. If User clicks on the “Block User” button, the next time the user right-clicks on the same user button, the button on top will appear as “Unblock User”. Similarly, if User clicks on the “Become Invisible to User” option, the next time the second button will appear as “Become Visible to User”.
Test status: Passed.

Test 6: Blocking and Unblocking
Steps:
User creates a new buyer account named “BlockingBuyer” then exits the program.
User creates a new seller account named “BlockingSeller” with a store named “BlockingStore” and continues as either “BlockingSeller” or “BlockingStore” to the user interaction. User searches for “BlockingBuyer” and sends some chat.
User right-clicks on “BlockingBuyer” and clicks on the “Block User” button then exits the program.
User logs back in as “BlockingBuyer”, clicks on “BlockingSeller” and sees that on the top it says “You’re connected to BlockingSeller”. User then types something like “Hi” and clicks on “Send”. 
User connects to “BlockingStore” and sees that on the top it says “You’re connected to BlockingStore”. User then types something like “Hi” and clicks on “Send”. 
Expected result: In both step 4 and 5, User sees a pop up error message that says: "Sorry, this user has blocked you".
Test status: Passed.

User exits “BlockingBuyer” and logs back in as “BlockingSeller”. 
User right-clicks on “BlockingBuyer” and clicks “Unblock User”
User exits “BlockingSeller” and repeat step 4 and 5
Expected result: User sees the message appear on the screen like normal messaging again.
Test status: Passed.
User logs in as “BlockingBuyer” and blocks either “BlockingSeller” or “BlockingStore”
Expected result: In either case, when User logs in as “BlockingSeller”, User will see the error message when trying to message “BlockingBuyer”.
User logs in as “BlockingBuyer” and unblocks either “BlockingSeller” or “BlockingStore”.
Expected result: In either case, both “BlockingSeller” and “BlockingStore” will be able to message “BlockingBuyer” as normal.
Test status: Passed.

Test 7: Invisible Test
Steps:
User creates a new buyer account named “InvisibleBuyer” then exits the program.
User creates a new seller account named “InvisibleSeller” with a store named “InvisibleStore” and continues the user interaction. User connects to “InvisibleBuyer” and sends some chats.
User right-clicks on “InvisibleBuyer” and clicks on the “Become Invisible to User” button then exits the program.
User logs back in as “InvisibleBuyer” and tries to search for a seller with the name “InvisibleSeller” and clicks “See a list of stores”.
Expected result: when searching for “InvisibleSeller” there’s an error message saying “Sorry, no user found with this name”. When trying to see a list of stores, either an error message pops up saying “No options available” or InvisibleStore is not in the list of stores presented. Both “InvisibleSeller” and “InvisibleStore” cannot be seen on the left panel.
Test status: Passed
User logs back in as “InvisibleSeller”, right-clicks on “InvisibleBuyer” and choose “Become Visible to User” then exits the program.
User logs back in as “InvisibleBuyer” and tries to search for a seller with the name “InvisibleSeller” and clicks “See a list of stores”.
Expected result: when searching for “InvisibleSeller” it connects User with “InvisibleSeller”. When trying to see a list of stores there is an option of “InvisibleStore”. Both “InvisibleSeller” and “InvisibleStore” can be seen on the left panel when conversations are established.
Test status: Passed
User logs back in as “InvisibleBuyer” and become invisible to either “InvisibleSeller” or “InvisibleStore”
User logs back in as “InvisibleSeller”.
Expected result: “InvisibleBuyer” doesn’t appear anywhere on the left side panel. When User searches for a buyer named “InvisibleBuyer”, there’s an error message saying “Sorry, no user found with this name”.
Test status: Passed
User logs back in as “InvisibleBuyer”, right-clicks on “InvisibleSeller” and selects “Become Visible to User” then exits the program.
User logs back in as “InvisibleSeller”.
Expected result: “InvisibleBuyer” appears as a conversation for both “InvisibleSeller” and “InvisibleStore”
Test status: Passed


Test 8: Message Filtering
Steps:
User logs in the program with any account and go into “User Interaction”
User clicks on the setting button on the top right area of the screen.
Expected result: A menu pops up in the middle of the screen with 3 options: “Message Filter”, “Vanish Mode”, and “Exit” with an icon. The other part of the screen is blurred and the other buttons can’t be clicked or interacted with.
Test status: Passed
User clicks on the exit button.
Expected result: The menu disappears and the UI is back to normal
Test status: Passed
User clicks on the setting button again and chooses “Message Filtering”
Expected result: Another menu replaces the current menu. The menu consists of 4 buttons: “Add filter”, “Delete filter”, “Edit filter” and “Exit”.
Test status: Passed
User clicks on the “Add filter” button. User enters the word to censor, for example the f-bomb, then proceed. User selects “No” when asked if they want to use default filter “*”. User then enters a replacement, such as “bruh”.
Expected result: A success message pops up and then the filter menu reappears. When User clicks “Exit”, all instances of the f-bomb have turned into “bruh”
Test status: Passed
User opens the filter menu again and chooses “Edit filter”. User selects the f-bomb to edit filter. Then User selects “Yes” when asked if they want to use the default filter. The User clicks “OK” in the success message pop-up.
Expected result: All instances of the f-bomb that turned into “bruh” have become “****”.
Test status: Passed
User opens the filter menu again and chooses “Delete filter”. User selects the f-bomb to delete filter. User selects “OK” in the success message pop-up.
Expected result: All instances of the f-bomb reappear.
Test status: Passed

Test 9: Metrics Test
Steps:
Log in with the buyer in one instance and the seller in another.
In the buyer window, verify the number of messages to the previous store by selecting the metrics button.
Verify the number of messages received from the buyer by selecting the metrics button in the seller window.
Send a certain number of messages from the buyer to the store.
Refresh chats on the seller side.
Sender a certain number of chats from the store to the buyer.
Refresh chats on the buyer side.
Open the seller’s metrics and choose a common word.
Close the seller metrics.
Edit a message with that word in it to not have it.
Delete another message with that word.
Refresh chats in the buyer window.
Open the seller metrics.
Expected Result: The previously observed message numbers go up by the exact number of messages sent correspondingly for both the buyer’s metrics and the seller’s metrics for their store contacting the buyer. The count of the common word chosen goes down by the exact amount of that word that was edited out or deleted.
Test Status: passed

Test 10: Account Change and Deletion
Steps:
Login as the buyer, but do not proceed to user interaction.
Select the button account changes.
Select the button to change your username.
Enter a new valid username and select ok.
Select ok again.
Close the window.
Launch again, and attempt to login with the old username.
Login with the new username, but do not proceed to messaging.
Select account changes.
Select delete account.
Select yes.
Launch again and attempt to login as the deleted account.
Expected result: Changing the username successfully changes the data stored about the user. An attempt to login as the old name fails, and the new one succeeds. Deleting the account succeeds, and an attempt to login as the deleted user fails.
Test Status: passed

