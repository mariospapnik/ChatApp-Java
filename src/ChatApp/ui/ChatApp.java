package ChatApp.ui;

import ChatApp.core.Chat;
import ChatApp.core.Msg;
import ChatApp.core.Role;
import ChatApp.core.User;
import ChatApp.db.*;
import ChatApp.menu.Menu;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.*;

public class ChatApp extends UI {

    public static ChatApp chatApp = null;
    public static Scanner sc = new Scanner(System.in);
    public static BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
    public static User curUser = null;
    public static Role curRole = null;
    public static HashMap<Integer, User> users = null;
    public static HashMap<Integer, String> roleNames = null;

    public static RoleDAO roleDao = RoleDAO.getInstance();
    public static UserDAO userDao = UserDAO.getInstance();
    public static ChatDAO chatDao = ChatDAO.getInstance();
    public static ChatUsersDAO chatUsersDao = ChatUsersDAO.getInstance();
    public static MsgDAO msgDao = MsgDAO.getInstance();

    public static Menu mainMenu = new Menu("ChatApp", "main menu");
    public static Menu singleUserMenu = new Menu("User Menu", "User actions");
    public static Menu usersMenu = new Menu("Users Menu", "Find users");
    public static Menu editUserMenu = new Menu("Edit User", "Edit");
    public static Menu chatsMenu = new Menu("Chats Menu", "Chats actions");
    public static Menu singleChatMenu = new Menu("Chat Menu", "view Msgs");


    private ChatApp(){ }

    public static ChatApp getInstance() {
        if (chatApp == null)
            chatApp = new ChatApp();
        return chatApp;
    }

    //entry point
    public void startChatApp() {
        execMainMenu();
    }

//    method to display the main screen
    private void execMainMenu() {
        clrscr();
        mainMenu.clearActions();

        if(curUser == null) {
            mainMenu.putAction("Log in",()-> login());
            mainMenu.putAction("Sign up",()-> signUp());
        }
        else {
            mainMenu.putAction("User menu", () -> execSingleUserMenu());
            mainMenu.putAction("Log out", () -> logout());
        }
        mainMenu.putAction("Exit",()-> System.exit(0));

        mainMenu.activateMenu();
    }

    private void execSingleUserMenu() {
        clrscr();
        singleUserMenu.clearActions();

        singleUserMenu.putAction("Users", ()-> execUsersMenu() );
        singleUserMenu.putAction("Chats", () -> execChatsMenu() );

        if (curUser.getRole().getUserRight("canEditSelf"))
            usersMenu.putAction("Edit my profile", () -> editProfile() );

        singleUserMenu.putAction("Back to Main Menu",()-> execMainMenu() );
        singleUserMenu.putAction("Exit",()-> System.exit(0));

        singleUserMenu.activateMenu();
    }

    private void execUsersMenu() {
        clrscr();
        usersMenu.clearActions();

        if (curRole.getUserRight("canSearchUser")) {
            usersMenu.putAction("Search for a user", () -> searchUser() );
            usersMenu.putAction("View all users", () -> viewAllUsers() );
        }

        usersMenu.putAction("Back to profile view",()-> execMainMenu() );
        usersMenu.putAction("Exit",()-> System.exit(0));

        usersMenu.activateMenu();
    }

    private void execEditUserMenu() {
        clrscr();
        editUserMenu.clearActions();

        if(curRole.getUserRight("canToogleUser"))
            editUserMenu.putAction("Enable/Disable user", ()-> toggleUser(users.get(2)));

        if(curRole.getUserRight("canChangeUserType"))
            editUserMenu.putAction("Change user type", ()-> changeUserType(users.get(2)) );

        if(curRole.getUserRight("canChangeUserType"))
            editUserMenu.putAction("Edit user's fname and Lname", ()-> editUserFLName(users.get(2)) );

        editUserMenu.putAction("Exit",()-> System.exit(0));

        editUserMenu.activateMenu();

    }


    private void execChatsMenu() {

        clrscr();
        chatsMenu = new Menu("Chats for " + curUser.getUsername(), "Select a chat");
        chatsMenu.clearActions();

        chatDao.readUserChats(curUser);     //Get the chats of the current user

        chatsMenu.putAction("Create Chat", ()-> createChat() );
        curUser.chats.forEach((chat)-> {
            StringBuilder title = new StringBuilder()
                    .append("Chat " + chat.chatName + "\t with: ");
            for (int id : chat.usersIDs) {
                title.append(users.get(id).getUsername() + " ");
            }
            chatsMenu.putAction( title.toString() , ()-> viewSingleChat(chat) );
        });

        chatsMenu.putAction("Back to profile view", ()-> execSingleUserMenu() );

        chatsMenu.putAction("Exit",()-> System.exit(0));

        chatsMenu.activateMenu();

    }


    public void login() {
        clrscr();
        showExpBox("ChatApp");

        showExpBox("Enter Username");
        String username = sc.nextLine();

        int userID = userDao.checkUser(username);    //Check if username exists in the db

        if (userID!=0) {
            clrscr();
            showExpBox("Enter password");

            int tries = 1;
            while(tries<4) {
                String pass = sc.nextLine();
                boolean isPassCorrect = userDao.checkPass(userID, pass); // Check if the Password is correct
                if (isPassCorrect) {
                    curUser = new User(userID);    //create the user
                    users = userDao.selectAllUsers();   //get all the users
                    curUser = users.get(userID);    //set the current user according to ID
                    roleDao.selectRoleNames(roleNames); //set the rolenames
                    curRole =  roleDao.readRole(curUser.getRole()); //set the curUser role
                    clrscr();
                    pauseExecution("Logged in as\n" + curUser.getUsername() + "\n \nPress any key.");
                    execSingleUserMenu();
                } else {
                    clrscr();
                    showExpBox("Wrong Password!\n"+(3-tries)+" tries remaining!");
                    tries++;
                }
            }
        }
        else {
            pauseExecution("User does not exist!\nPress any key.");
            execMainMenu();
        }
        execMainMenu();
    }

    public void logout() {
        curUser = null;
        execMainMenu();
    }

    public void signUp() {

        String username;
        String pass;
        clrscr();
        showExpBox("Create new user\nEnter username");
        //username input
        while(true) {
            username = sc.nextLine();
            int id = userDao.checkUser(username);
            if (id != 0) {
                clrscr();
                showExpBox(username + "\n already exists!\n");
            }
            if (id == 0){
                break;
            }
        }
        clrscr();

        //password input
        showExpBox(username + "\n Enter password");
        while(true) {
            pass = sc.nextLine();
            showExpBox("Please\nrepeat\nyour password");
            String passRepeat = sc.nextLine();
            if (passRepeat.equals(pass)) {
                int isCreated = userDao.createUser(username, pass);
                if (isCreated==1) {
                    showExpBox("User\n"+username+"\nwas created!\nPlease\nlog in.");
                }
                else{
                    showExpBox("Something\nwent wrong!\nPlease\ntry again.");
                    break;
                }
                break;
            }
            else {
                showExpBox("Passwords are\nnot identical\ntry\nagain");
            }
        }

        execMainMenu();

    }


    public void searchUser() {
        clrscr();
        showExpBox("Search for a user");
        String userSearch = sc.nextLine();

        ArrayList<User> usersFound = userDao.searchForUser(userSearch);
        StringBuilder userPrint = new StringBuilder();

        if (usersFound.isEmpty()) {
            userPrint.append("No users found!");
        }
        else {
            for (User user : usersFound) {
                userPrint.append(user.getID() + ": " + user.getUsername() + "\n");
            }
        }
        userPrint.append("Press any key to go back.");
        System.out.println(userPrint.toString());
//        showExpBox(userPrint.toString());
        sc.nextLine();
        execUsersMenu();
    }

    public void viewAllUsers(){
        clrscr();
        int i=1;
        users.forEach((id,user)-> System.out.println("No" + " :" + user.getUsername() ));

        System.out.println("Press any key to go back");
        sc.nextLine();
        execUsersMenu();
    }

    public void viewSingleChat(Chat chat) {
        clrscr();
        chatDao.readChat(curUser, chat);
        StringBuilder chatString = new StringBuilder();
        chatString.append("-- Chat: " + chat.chatName + "--\n\n");
        for(Msg msg: chat.msgs) {
            chatString.append(msg.id + "> " + users.get(msg.creator).getUsername() + "( date ) :\n\t" + msg.data + "\n\n");
        }

        System.out.println(chatString.toString());
        sc.nextLine();
        execChatsMenu();
    }

    public void createChat() {
        clrscr();
        int chatInserted;
        int msgsTableCreated;
        int chatUsersInserted;

        Chat chat = new Chat();     //create the new chat object
        chat.creatorUserID = curUser.getID();   //set the creator id for the chat
        String chatName;
        while (true) {
            System.out.println("Enter chat name:");
            chatName = sc.nextLine();
            if (requestConfirmation("Proceed with '" + chatName + "' as the name of the Chat? (y/n) ")) break;
        }
        chat.chatName = chatName;
        chatInserted = chatDao.createChat(chat);    //create the chat in db
        if (chatInserted==1) {
            msgsTableCreated = msgDao.createMsgTable(chat); //if the chat was created create the msg table
            ArrayList<User> usersSelected = selectUsers();
            chatUsersInserted =  chatUsersDao.insertChatUsers(chat, usersSelected);
        }

        execChatsMenu();
    }

    public void editProfile() {
        System.out.println("Not yet implemented. Press any key to go back!");
        sc.nextLine();
        execSingleUserMenu();
    }



    public void toggleUser(User user) {
        notYetImplemented();
    }

    public void changeUserType(User user) {
        notYetImplemented();
    }

    public void editUserFLName(User user) {
        notYetImplemented();
    }


    public ArrayList<User> selectUsers() {

        ArrayList<User> usersList = new ArrayList<>();
        usersList.addAll(users.values());
        ArrayList<User> selectedUsers = new ArrayList<>();

        while(true) {

            int i = 1;
            for( User user: usersList) {
                System.out.println("No"+ (i++) + " :" + user.getUsername());
            }
            System.out.println("Select a number to add user");

            String input =  sc.nextLine();
            try {
                int usertoAdd = Integer.parseInt(input) -1 ;

                if ( (usertoAdd > 0) && (usertoAdd < usersList.size()) ) {
                    selectedUsers.add( usersList.get(usertoAdd) );
                }
                else {
                    System.out.println("Not a valid user");
                }
            }
            catch ( NumberFormatException e){
                System.out.println("Not a number!");
            }

            if (!requestConfirmation("Do you want to continue adding users? (y/n)..")) break;
        }

        return selectedUsers;

    }

    public void notYetImplemented() {
        System.out.println("Not yet implemented. Press any key to go back!");
        sc.nextLine();
        execEditUserMenu();
    }

}

