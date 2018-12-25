package ChatApp.ui;

import ChatApp.core.Chat;
import ChatApp.core.Msg;
import ChatApp.core.Role;
import ChatApp.core.User;
import ChatApp.db.ChatDAO;
import ChatApp.db.ChatUsersDAO;
import ChatApp.db.MsgDAO;
import ChatApp.db.RoleDAO;
import ChatApp.db.UserDAO;
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
        singleUserMenu.putAction("Chats", ()-> execChatsMenu() );

        if (curRole.getUserRight("canEditSelf"))
            singleUserMenu.putAction("Edit my profile", ()-> editProfile() );

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
        chatDao.readUsersOfChats(curUser);  //Get the users of all the chats

        chatsMenu.putAction("Create Chat", ()-> createChat() );
        curUser.getChats().forEach((chat)-> {
            StringBuilder title = new StringBuilder()
                    .append("Chat " + chat.getChatName() + "\t with: ");
            for (int id : chat.getUsersIDs()) {
                title.append(users.get(id).getUsername() + " ");
            }
            chatsMenu.putAction( title.toString() , ()-> execSingleChatMenu(chat) );
        });

        chatsMenu.putAction("Back to profile view", ()-> execSingleUserMenu() );
        chatsMenu.putAction("Exit",()-> System.exit(0));

        chatsMenu.activateMenu();

    }

    private void execSingleChatMenu(Chat chat) {
        clrscr();
        viewSingleChat(chat);
        singleChatMenu = new Menu("Chat: " + chat.getChatName(), "");
        singleChatMenu.putAction("Previous Messages", ()-> previousMsgs() );
        singleChatMenu.putAction("Next Messages", () -> nextMsgs() );
        singleChatMenu.putAction("Send Message", () -> sendMsg(chat) );
        singleChatMenu.putAction("Toogle Message", () -> toogleMsg() );
        singleChatMenu.putAction("Back to Chat view", () -> execSingleChatMenu(chat) );
        singleChatMenu.putAction("Back to all Chats", () -> execChatsMenu() );

        singleChatMenu.activateMenu();
    }


    public void login() {
        clrscr();

        showExpBox(" -- ChatApp -- ");
        showExpBox("> Enter Username");
        String username = sc.nextLine();

        int userID = userDao.checkUser(username);    //Check if username exists in the db

        if (userID!=0) {
            clrscr();
            showExpBox("> Enter password");

            int tries = 1;
            while(tries<4) {
                String pass = sc.nextLine();
                boolean isPassCorrect = userDao.checkPass(userID, pass); // Check if the Password is correct
                if (isPassCorrect) {
                    setCurUserAndEnvironment(userID);
                    clrscr();
                    showExpBox("> Logged in as " + curUser.getUsername() );
                    pauseExecution("  Press any key.");
                    execSingleUserMenu();
                } else {
                    clrscr();
                    showExpBox("> Wrong Password!\n  "+(3-tries)+" tries remaining!");
                    tries++;
                }
            }
        }
        else {
            showExpBox("> User does not exist!");
            pauseExecution("Press any key.");
        }
        execMainMenu();
    }

    public void setCurUserAndEnvironment(int userID) {
        curUser = new User(userID);             //create the user
        users = userDao.selectAllUsers();       //get all the users
        curUser = users.get(userID);            //set the current user according to ID
        roleNames = roleDao.selectRoleNames();  //set the rolenames
        roleDao.readRole(curUser.getRole());    //set the curUser role
        curRole = curUser.getRole();
    }

    public void logout() {
        curUser = null;
        curRole = null;
        execMainMenu();
    }

    public String usernameInput() {
        //username input
        String username;
        clrscr();
        while(true) {

            showExpBox("> Create new user\nEnter username");
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
        return username;
    }

    public String passInput(String username) {
        //password input
        String pass;
        clrscr();
        while(true) {
            showExpBox(username + "\n> Enter password");
            pass = sc.nextLine();
            clrscr();
            showExpBox("> Please repeat your password");
            String passRepeat = sc.nextLine();
            if (passRepeat.equals(pass)) break;
            else {
                clrscr();
                showExpBox("> Passwords are not identical.\n  Try again");
            }
        }
        return pass;
    }

    public String nameInput(String nameType) {
        //first name input
        String name;
        clrscr();
        while (true) {
            clrscr();
            showExpBox("> Enter your " + nameType + " name");
            name = sc.nextLine();
            if (requestConfirmation("Is " + name + " your " + nameType + " name? (y/n)")) break;
        }
        return name;
    }

    public void signUp() {

        String username;
        String pass;
        String fname;
        String lname;
        int isCreated = 0;

        username = usernameInput();     //get username input from user
        pass = passInput(username);     //get password input from user
        fname = nameInput("first");     //get first name input from user
        lname = nameInput("last");      //get last name input from user

        // create the user
        isCreated = userDao.createUser(username, pass, fname, lname);

        //check if the user has been created!
        if (isCreated==1) {
            showExpBox("> User "+username+" was created!.");
            pauseExecution("  Please log in");
        }
        else{
            showExpBox("> Something went wrong!\n  Please try again.");
        }

        execMainMenu();         //go back to the main menu

    }


    public void searchUser() {
        clrscr();
        showExpBox("> Search for a user");
        String userSearch = sc.nextLine();

        //search for users using the given keyword
        HashMap<Integer,User> usersFound = userDao.searchForUser(userSearch);

        if (usersFound.isEmpty())
            showExpBox("> No users found!");
        else
            viewUsers( usersFound.values() );

        pauseExecution("  Press any key to go back.");
        execUsersMenu();
    }

    public void viewAllUsers(){

        viewUsers( users.values() );
        pauseExecution("Press any key to go back");
        execUsersMenu();
    }

    public void viewUsers( Collection<User> users) {
        clrscr();
        int i=1;
        System.out.printf("%4s | %8s | %10s | %20s | %20s | %8s\n", "No", "Role", "Username", "First Name", "Last Name","Active");
        for(User user : users) {
            System.out.printf("%4d | %8s | %10s | %20s | %20s | %8s\n",
                    i++ ,  roleNames.get( user.getRole().getID() ) , user.getUsername(), user.getFname(), user.getLname(), user.getActive() );
        }
    }


    public void viewSingleChat(Chat chat) {
        clrscr();
        msgDao.readChatMsgs(curUser, chat);
        StringBuilder chatString = new StringBuilder();
        showExpBox("-- Chat: " + chat.getChatName() + "--\n\n");
        int i =1;
        for(Msg msg: chat.getMsgs()) {
            chatString.append(i++ + "> " + users.get(msg.getCreator()).getUsername())
                      .append( "  (" + msg.getSentDate())
                      .append( " ) :\n\t" + msg.getData() + "\n\n");
        }

        System.out.println(chatString.toString());
        pauseExecution();
    }

    public void createChat() {
        clrscr();
        int chatInserted;
        int chatUsersInserted;

        Chat chat = new Chat();     //create the new chat object
        chat.setCreatorUserID( curUser.getID() );   //set the creator id for the chat
        String chatName;
        while (true) {
            System.out.println("Enter chat name:");
            chatName = sc.nextLine();
            if (requestConfirmation("Proceed with '" + chatName + "' as the name of the Chat? (y/n) ")) break;
        }
        chat.setChatName(chatName);
        chatInserted = chatDao.createChat(chat);    //create the chat in db

        ArrayList<User> usersSelected = selectUsers();
        chatUsersInserted =  chatUsersDao.insertChatUsers(chat, usersSelected);

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

            clrscr();

            int i = 1;
            for( User user: usersList) {
                if (user.getID()!=curUser.getID()) {
                    System.out.println("No" + (i++) + " :" + user.getUsername());
                }
            }
            System.out.println("Select a number to add user");

            String input =  sc.nextLine();
            try {
                int usertoAdd = Integer.parseInt(input) -1 ;

                if ( (usertoAdd > 0) && (usertoAdd < usersList.size()) ) {
                    selectedUsers.add( usersList.get(usertoAdd) );
                    System.out.println("added user " + usertoAdd);
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


    public void previousMsgs() {
        notYetImplemented();
    }

    public void nextMsgs() {
        notYetImplemented();
    }

    public void toogleMsg() {
        notYetImplemented();
    }

    public void sendMsg(Chat chat) {
        viewSingleChat(chat);
        showExpBox("Enter your message: (250 characters max) ");
        String data = sc.nextLine();
        requestConfirmation("Are you sure yu want to send the message? (y/n)");

        if (msgDao.sendMsg(curUser, chat, data)==1) {
            showExpBox("Message was sent!");
            pauseExecution();
        }
        execSingleChatMenu(chat);
    }

    public void notYetImplemented() {
        System.out.println("Not yet implemented. Press any key to go back!");
        sc.nextLine();
        execEditUserMenu();
    }

}

