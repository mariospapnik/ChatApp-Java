package ChatApp.app;

import ChatApp.app.core.Chat;
import ChatApp.app.core.Msg;
import ChatApp.app.core.Role;
import ChatApp.app.core.User;
import ChatApp.app.db.ChatDAO;
import ChatApp.app.db.ChatUsersDAO;
import ChatApp.app.db.MsgDAO;
import ChatApp.app.db.RoleDAO;
import ChatApp.app.db.UserDAO;
import ChatApp.app.interfaces.ChatMenus;
import ChatApp.app.interfaces.ChatMethods;
import ChatApp.app.menu.Menu;
import ChatApp.app.menu.Utilities;

import java.io.BufferedReader;
import java.io.Console;
import java.io.InputStreamReader;
import java.util.*;
import java.util.logging.Logger;

public class ChatApp extends Utilities implements ChatMenus, ChatMethods {

    // Declare the app variable
    private static ChatApp chatApp = null;
    private static Scanner sc;
    private static Console console;
//    private static final Logger LOGGER = Logger.getLogger( ChatApp.class.getName() );
//    private static BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

    // Declare the variables for current user, current role and all the users and role names
    private static User curUser = null;
    private static Role curRole = null;
    private static HashMap<Integer, User> users = null;
    private static HashMap<Integer, String> roleNames = null;

    // Declare the DAOs for CRUDing
    private static RoleDAO roleDao;
    private static UserDAO userDao;
    private static ChatDAO chatDao;
    private static ChatUsersDAO chatUsersDao;
    private static MsgDAO msgDao;

    // Declare the Menu objects
    private static Menu mainMenu;
    private static Menu singleUserMenu;
    private static Menu usersMenu;
    private static Menu editUserMenu;
    private static Menu chatsMenu;
    private static Menu singleChatMenu;
    private static Menu EditProfileMenu;


    private ChatApp(){ }

    /*
       ChatApp is Singleton
       on first run DAOs Menus and Scanner are initialized
    */
    public static ChatApp getInstance() {
        if (chatApp == null) {

            chatApp = new ChatApp();
            sc = new Scanner(System.in);
            console = System.console();

            roleDao = RoleDAO.getInstance();
            userDao = UserDAO.getInstance();
            chatDao = ChatDAO.getInstance();
            chatUsersDao = ChatUsersDAO.getInstance();
            msgDao = MsgDAO.getInstance();

            mainMenu = new Menu("ChatApp", "main menu");
        }
        return chatApp;
    }

    // Entry point
    public void startChatApp() {
        execMainMenu();
    }


    //  ****** Implementing the ChatMenus Interface ******

    public void execMainMenu() {
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

    public void execSingleUserMenu() {
        clrscr();

        if (curUser.getActive()) {
            singleUserMenu = new Menu("User Menu", "logged as: " + curUser.getUsername());
            singleUserMenu.clearActions();

            singleUserMenu.putAction("Users", () -> execUsersMenu());
            singleUserMenu.putAction("Chats", () -> execChatsMenu());

            if (curRole.getUserRight("canEditSelf"))
                singleUserMenu.putAction("Edit my profile", () -> execEditProfileMenu());

            singleUserMenu.putAction("Back to Main Menu", () -> execMainMenu());
            singleUserMenu.putAction("Exit", () -> System.exit(0));

            singleUserMenu.activateMenu();
        }
        else {
            showExpBox("> " + curUser.getUsername() + " is deactivated.");
            pauseExecution("Please contact the administrator");
            execMainMenu();
        }
    }

    public void execUsersMenu() {
        clrscr();
        usersMenu = new Menu("Users Menu", "logged as: " + curUser.getUsername() );
        usersMenu.clearActions();

        users = userDao.selectAllUsers();

        if (curRole.getUserRight("canCreateUser")) {
            usersMenu.putAction("Create a user", () -> signUp() );
        }

        if (curRole.getUserRight("canSearchUser")) {
            usersMenu.putAction("Search for a user", () -> searchUser() );
            usersMenu.putAction("View all users", () -> viewAllUsers() );
        }

        if (curRole.getUserRight("canEditUser")) {
            usersMenu.putAction("Edit a user", () -> execEditUserMenu() );
        }

        usersMenu.putAction("Back to profile view",()-> execSingleUserMenu() );
        usersMenu.putAction("Exit",()-> System.exit(0));

        usersMenu.activateMenu();
    }

    public void execEditUserMenu() {
        clrscr();

        User userToEdit;
        User userSelected = null;
        try {
            userSelected = selectFromUsers(1).get(0);    //Select a user to edit
        }
        catch( IndexOutOfBoundsException e) {
            execUsersMenu();    //if none selected go back!
        }
        finally {
            userToEdit = userSelected;
        }


        editUserMenu = new Menu("Edit User " +userToEdit.getUsername() + " " , "logged as: " + curUser.getUsername() );
        editUserMenu.clearActions();

        // Toogle User
        if(curRole.getUserRight("canToogleUser"))
            editUserMenu.putAction("Enable/Disable user", ()-> toggleUser(userToEdit) );

        // Edit user type
        if(curRole.getUserRight("canChangeUserType"))
            editUserMenu.putAction("Change user type", ()-> changeUserType(userToEdit) );

        // Edit user First and Last Names
        if(curRole.getUserRight("canEditUser"))
            editUserMenu.putAction("Edit user's fname and Lname", ()-> editUserFLName(userToEdit) );

        editUserMenu.putAction("Back to Users",()-> execUsersMenu());

        editUserMenu.putAction("Exit",()-> System.exit(0));

        editUserMenu.activateMenu();

    }

    public void execEditProfileMenu() {
        clrscr();

        EditProfileMenu = new Menu("Edit profile " , "logged as: " + curUser.getUsername() );
        EditProfileMenu.clearActions();

        EditProfileMenu.putAction("Change first or last name ",()-> editUserFLName(curUser));

        EditProfileMenu.putAction("Change password",()-> updateUserPassword());

        EditProfileMenu.putAction("Back",()-> execSingleUserMenu());

        EditProfileMenu.putAction("Exit",()-> System.exit(0));

        EditProfileMenu.activateMenu();


    }

    public void execChatsMenu() {

        clrscr();
        chatsMenu = new Menu("Chats for " + curUser.getUsername(), "logged as: " + curUser.getUsername() );
        chatsMenu.clearActions();

        chatDao.readUserChats(curUser);     //Get the chats of the current user
        chatDao.readUsersOfChats(curUser);  //Get the users of all the chats of the user

        chatsMenu.putAction("Create Chat", ()-> createChat() );
        curUser.getChats().forEach((chat)-> {
            StringBuilder title = new StringBuilder()
                    .append("Chat * " + chat.getChatName() + " *\n\t with: ");
            for (int id : chat.getUsersIDs()) {
                if (id!=1)
                    title.append(users.get(id).getUsername() + " ");
            }
            chatsMenu.putAction( title.toString() , ()-> execSingleChatMenu(chat) );
        });

        chatsMenu.putAction("Back to profile view", ()-> execSingleUserMenu() );
        chatsMenu.putAction("Exit",()-> System.exit(0));

        chatsMenu.activateMenu();

    }

    public void execSingleChatMenu(Chat chat) {
        clrscr();
        viewSingleChat(chat);
        singleChatMenu = new Menu("Chat: " + chat.getChatName(), "logged as: " + curUser.getUsername() );
//        singleChatMenu.putAction("Previous Messages", ()-> previousMsgs() );
//        singleChatMenu.putAction("Next Messages", () -> nextMsgs() );
        if (curRole.getMsgsRight("canSendMsg")) {
            singleChatMenu.putAction("Send Message", () -> sendMsg(chat));
        }
        if (curRole.getMsgsRight("canToogleMsg")) {
            singleChatMenu.putAction("Toogle Message", () -> toogleMsg(chat));
        }
        if (curRole.getMsgsRight("canDeleteMsg")) {
            singleChatMenu.putAction("Delete Message", () -> deleteMsg(chat));
        }
        if (curRole.getChatsRight("canAddUserToChat")) {
            singleChatMenu.putAction("Add user(s) to Chat", () -> addUsersToChat(chat));
            singleChatMenu.putAction("Remove user(s) from Chat", () -> removeUsersFromChat(chat));
        }
        if (curRole.getChatsRight("canExitChat")) {
            singleChatMenu.putAction("Exit from Chat", () -> exitChat(chat));
        }

        if (curRole.getChatsRight("canDeleteChat")) {
            singleChatMenu.putAction("Delete Chat", () -> deleteChat(chat));
        }

        singleChatMenu.putAction("Back to Chat view", () -> execSingleChatMenu(chat) );
        singleChatMenu.putAction("Back to all Chats", () -> execChatsMenu() );

        singleChatMenu.activateMenu();
    }


    //  ****** Implementing the ChatMethods Interface ******

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

               String pass = readPass();


                if ( userDao.checkPass(userID, pass) ) {    // Check if the Password is correct
                    setCurUserAndEnvironment(userID);       // Get the users list and populate the CurUser and CurRole
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

    public void logout() {
        curUser = null;
        curRole = null;
        execMainMenu();
    }

    public void signUp() {

        String username;
        String pass;
        String fname;
        String lname;
        int isCreated = 0;

        username = usernameInput();     //get username input from user
        pass = newPassInput(username);     //get password input from user
        fname = nameInput("first");     //get first name input from user
        lname = nameInput("last");      //get last name input from user

        // create the user
        isCreated = userDao.createUser(username, pass, fname, lname);

        //check if the user has been created!
        if (isCreated==1) {
            showExpBox("> User "+username+" was created!.");
            pauseExecution();
        }
        else{
            showExpBox("> Something went wrong!\n  Please try again.");
        }

        execMainMenu();         //go back to the main menu

    }

    public void setCurUserAndEnvironment(int userID) {
        curUser = new User(userID);             //create the user
        users = userDao.selectAllUsers();       //get all the users
        curUser = users.get(userID);            //set the current user according to ID
        roleNames = roleDao.selectRoleNames();  //set the rolenames
        roleDao.readRole(curUser.getRole());    //set the curUser role
        curRole = curUser.getRole();
    }

    public String usernameInput() {
        //username input
        String username;
        clrscr();
        while(true) {

            showExpBox("> Create new user\nEnter username");
            username = sc.nextLine();
            String pattern = "^[a-zA-Z0-9_-]{5,10}$";
            if (!username.matches(pattern)) {
                clrscr();
                showExpBox("> Username should be at least 5 and up to 10 characters long.\n " +
                        ".It should contain only letters, digits, underscores('_') and dashes('-').");
                pauseExecution();
                continue;
            }
            int id = userDao.checkUser(username);
            if (id != 0) {
                clrscr();
                showExpBox(username + "\n already exists!\n");
            }
            else break;
        }
        return username;
    }

    public String readPass(){
        String pass;
        if (console == null) {
//                    System.out.println("Couldn't get Console instance");
            pass = sc.nextLine();
        }
        else {
            char[] passwordChars = console.readPassword();
            pass = new String(passwordChars);
        }
        return pass;
    }

    public String newPassInput(String username) {
        //password input
        String pass;
        String pattern = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,10}";
        clrscr();
        while(true) {
            showExpBox(username + "\n> Enter password");
            pass = readPass();
            if (!pass.matches(pattern)) {
                clrscr();
                showExpBox("> Password should be at least 8 character long\n " +
                        "and contain at least one digit, one uprcase letter, one lowercase letter and one symbol.");
                pauseExecution();
                continue;
            }

            clrscr();
            showExpBox("> Please repeat your password");
            String passRepeat = readPass();
            if (passRepeat.equals(pass)) break;
            else {
                clrscr();
                showExpBox("> Passwords are not identical.");
                if (!requestConfirmation("Try again? (y/n)")) break;
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
        printUserDetailsTitles();

        for(User user : users) {
            printUserDetails(user,i);
            i++;
        }
    }

    public void viewSingleChat(Chat chat) {
        clrscr();
        msgDao.readChatMsgs(curUser, chat);

        StringBuilder chatString = new StringBuilder();
        showExpBox("-- Chat: " + chat.getChatName() + "--\n\n");

        chat.getUsersIDs().forEach( (id)-> {
            if(users.get(id).getActive()) {
                System.out.print("  " + users.get(id).getUsername() + "  ");
            }
            else {
                System.out.println(" (" + users.get(id).getUsername() + " - inactive)   ");
            }
        } );
        System.out.println("\n\n");

        int i =1;
        for(Msg msg: chat.getMsgs()) {
            chatString.append(i++ + "> " + users.get(msg.getCreator()).getUsername())
                      .append( "  (" + msg.getSentDate().toString().substring(0,16));
            if(msg.isActive())
                chatString.append( " ) :\n\t" + msg.getData() + "\n\n");
            else if ( !msg.isActive() )
                chatString.append( " ) :\n\t (muted message) \n\n");
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

        String chatName = chatNameInput();  //get ChatName input from user
        chat.setChatName(chatName);

        chatInserted = chatDao.createChat(chat);    //create the chat in db

        ArrayList<User> usersSelected = selectFromUsers(true);
        if (chatInserted == 1) {
            chatUsersInserted = chatUsersDao.insertChatUsers(chat, usersSelected);
            if (chatUsersInserted > 0) {
               showExpBox("> Chat '" + chat.getChatName() + "' was created\n and " + chatUsersInserted + " users where added.");
            }
        }
        pauseExecution();
        execChatsMenu();
    }

    public void  deleteChat(Chat chat) {

        int chatDeleted;

        if (requestConfirmation("> Are you sure you want to delete Chat '" + chat.getChatName()+ "' ? (y/n) " )) {
            chatDeleted = chatDao.deleteChat(chat);
            if (chatDeleted == 1) {
                showExpBox("> Chat deleted!");
                pauseExecution();
            }
        }

        execChatsMenu();
    }

    public String chatNameInput() {

        String chatName;

        while (true) {
            clrscr();
            showExpBox("> Enter chat name:\n");
            chatName = sc.nextLine();
            if (chatName!=null)
                if (requestConfirmation("> Proceed with '" + chatName + "' as the name of the Chat? (y/n) \n"))
                    return chatName;
        }
    }

    public void addUsersToChat(Chat chat){
        int usersAdded = 0;
        ArrayList<User> usersNotInChat = new ArrayList<>();
        // create a list with users not in the chat
        users.forEach((k,v)-> { if (!chat.getUsersIDs().contains(k))  usersNotInChat.add(v); });
        // select users from this list
        ArrayList<User> usersSelected = selectFromUsers(usersNotInChat);
        // if not empty execute the DELETE
        if (!usersSelected.isEmpty())
            usersAdded = chatUsersDao.insertChatUsers(chat, usersSelected);

        showExpBox( (usersAdded == usersSelected.size())
                ?"> Added selected users to chat!"
                :"> Something went wrong. Please try again.");
        pauseExecution();

        execChatsMenu();
    }

    public void removeUsersFromChat(Chat chat) {
        int usersRemoved = 0;
        ArrayList<User> usersInChat = new ArrayList<>();
        // create a list with users in the chat
        users.forEach((k,v)-> { if (chat.getUsersIDs().contains(k))  usersInChat.add(v); });
        // select users from this list
        ArrayList<User> usersSelected  = selectFromUsers(usersInChat);
        // if not empty execute the DELETE
        if (!usersSelected.isEmpty())
            usersRemoved = chatUsersDao.deleteChatUsers(chat, usersSelected);

        showExpBox( (usersRemoved == usersSelected.size())
                ?"> Removed selected users from chat!"
                :"> Something went wrong. Please try again.");
        pauseExecution();

        execChatsMenu();
    }

    public void exitChat(Chat chat) {
        int curUserRemoved = 0;
        //request confirmation
        if (!requestConfirmation("Are you sure you want to leave chat '" + chat.getChatName() + "' ?")) execChatsMenu();

        // make the array with one user and execute the DELETE
        ArrayList<User> userToRemove = new ArrayList<>();
        userToRemove.add(curUser);
        curUserRemoved = chatUsersDao.deleteChatUsers(chat, userToRemove);

        showExpBox( (curUserRemoved == userToRemove.size())
                ?"> You left the chat!"
                :"> Something went wrong. Please try again.");

        pauseExecution();
        execChatsMenu();
    }

    public void updateUserPassword() {
        int passUpdated =0;

        showExpBox("> Enter your current password");

        String pass = readPass();

        clrscr();
        if ( userDao.checkPass(curUser.getID(), pass) ) {    // Check if the Password is correct
            showExpBox("> New Password for " + curUser.getUsername());

            String newPass = newPassInput(curUser.getUsername());

            passUpdated = userDao.updatePass(curUser, newPass);

            if ( passUpdated ==1 ){
                clrscr();
                showExpBox("> Password successfully changed!");
                pauseExecution();
                logout();
            }
            else {
                clrscr();
                showExpBox("> Something went wrong. Please try again.");
                pauseExecution();
                execSingleUserMenu();
            }
        }
        else {
            showExpBox("> Wrong password. Please try again!");
            pauseExecution();
        }

        execEditProfileMenu();
    }

    public void toggleUser(User user) {
        if (requestConfirmation("> Are you sure you want to toggle " + user.getUsername() + " ? (y/n)")) {
            int userToggled = userDao.toogleUser(user);
            if (userToggled == 1) {
                showExpBox("> " + user.getUsername() + " toggled!");
                pauseExecution();
            }
        }
        execUsersMenu();

    }

    public void changeUserType(User user) {
        clrscr();
        int useRoleUpdated = 0;
        printUserDetails(user,1);
        printRolesNames();

        showExpBox("> Enter new role for user");
        try {
            int choice = Integer.parseInt(sc.nextLine());
            if ( roleNames.containsKey(choice) ) {
                useRoleUpdated = userDao.updateUserRole(user, choice);
                if (useRoleUpdated ==1 ){
                    clrscr();
                    showExpBox("> " + user.getUsername() + " role has changed to " + roleNames.get(choice));
                    pauseExecution();
                }
            }
            else {
                System.out.println("> Not a valid role!");
            }
        }
        catch (NumberFormatException e) {
            System.out.println("> Not a number!");
        }

        execUsersMenu();
    }

    public void editUserFLName(User user) {
        clrscr();
        printUserDetails(user,1);
        String name = null;

        String[] fields = {"fname","lname"};
        String[] names = {"first name","last name"};

        for (int i=0 ; i<fields.length ; i++) {
            if (requestConfirmation("> Change " + names[i] + " ? (y/n)\n")) {
                System.out.println("> Enter new " + names[i] + ":\n");
                name = sc.nextLine();
                if (name != null) {
                    if (userDao.updateUserFLName(user, name, fields[i]) == 1) {
                        showExpBox("> Changed " + user.getUsername() + "'s " + names[i] + " to " + name + " .");
                        pauseExecution();
                    }
                    else {
                        showExpBox("> Something went wrong!");
                        pauseExecution("> Please try again!");
                    }
                }
                else {
                    showExpBox("> Not valid input!");
                    pauseExecution("> Please try again!");
                }
            }
        }

        execUsersMenu();
    }

    public void printRolesNames() {
        roleNames.forEach((k,v)-> System.out.println(k + " : " + v));
    }

    /* Select users from a user list
       limit: how many users can be selected. if none default is the users list length
       addAdmin: if true user admin and current User are added to the selected users list
     */
    public ArrayList<User> selectFromUsers(ArrayList<User> usersToSelect , int limit, boolean addAdmin){

        ArrayList<User> selectedUsers = new ArrayList<>();
        Map<Integer,User> indexForUsers = new HashMap<>();

        // if true, admin and current user are added to the selection
        if (addAdmin) {
            selectedUsers.add(users.get(1));    // add admin
            if (curUser.getID()!=1)             // if current user not an admin
                selectedUsers.add(curUser);     // add current user
        }

        while (true) {
            clrscr();
            int i = 1;

            indexForUsers.clear();  //clear the index map for selecting

            //Print the users available for selection except the already selected
            printUserDetailsTitles();
            for( User user: usersToSelect) {
                if ( !selectedUsers.contains(user) ) {   //if not self and not admin!
                    printUserDetails(user,i);
                    indexForUsers.put(i,user);
                    i++;
                }
            }

            //Print the selected users except admin
            System.out.println("\nSelected Users:\n");
            int j = 1;
            for( User user: selectedUsers) {
                if (user.getID()!=1) {
                    printUserDetails(user, j);
                    j++;
                }
            }

            //Select a user
            showExpBox("> Select a number to add user");
            String input =  sc.nextLine();
            try {
                int usertoAdd = Integer.parseInt(input);
                if (indexForUsers.containsKey(usertoAdd)) {
                    selectedUsers.add( indexForUsers.get(usertoAdd) );
                    System.out.println("> Added user '" + indexForUsers.get(usertoAdd).getUsername() + "'.");
                }
                else {
                    System.out.println("> Not a valid user");
                }
            }
            catch ( NumberFormatException e){
                System.out.println("> Not a number!");
            }

            if (selectedUsers.size() < limit) {
                if (!requestConfirmation("> Do you want to continue adding users? (y/n)..\n")) break;
            }
            else break;

        }

        return selectedUsers;
    }

    // Select users. Overloaded methods!
    public ArrayList<User> selectFromUsers() {
        ArrayList<User> usersList = new ArrayList<>();
        usersList.addAll(users.values());
        return selectFromUsers(usersList);
    }

    public ArrayList<User> selectFromUsers(int limit) {

        ArrayList<User> usersList = new ArrayList<>();
        usersList.addAll(users.values());
        return selectFromUsers( usersList, limit, false);
    }

    public ArrayList<User> selectFromUsers( boolean addAdmin) {

        ArrayList<User> usersList = new ArrayList<>();
        usersList.addAll(users.values());
        return selectFromUsers( usersList, addAdmin);
    }

    public ArrayList<User> selectFromUsers(ArrayList<User> usersToSelect){
        return selectFromUsers(usersToSelect, usersToSelect.size(), false);
    }

    public ArrayList<User> selectFromUsers(ArrayList<User> usersToSelect, boolean addAdmin){
        return selectFromUsers(usersToSelect, usersToSelect.size(), addAdmin);
    }

    public void printUserDetailsTitles() {
        System.out.printf("%4s | %8s | %10s | %20s | %20s | %8s | %17s\n", "No", "Role", "Username", "First Name", "Last Name","Active","Reg.Date");
    }

    public void printUserDetails(User user, int i) {
        System.out.printf("%4d | %8s | %10s | %20s | %20s | %8s | %s\n",
                i ,  roleNames.get( user.getRole().getID() ) , user.getUsername(), user.getFname(), user.getLname(), user.getActive(), user.getRegDate().toString() );
    }

    public void toogleMsg(Chat chat) {
        Msg msg = selectMsg(chat);
        if (msgDao.toogleMsg(msg) == 1) {
            showExpBox("> Message toggled!");
            pauseExecution();
        }
        execSingleChatMenu(chat);
    }

    public void deleteMsg(Chat chat) {
        int msgDeleted;
        Msg msg = selectMsg(chat);

        if (requestConfirmation("> Are you sure you want to delete message \n'" + msg.getData() + "' ? \n (y/n) " )) {
            msgDeleted = msgDao.deleteMsg(msg);
            if (msgDeleted == 1) {
                showExpBox("> Message deleted!");
                pauseExecution();
            }
        }
        execSingleChatMenu(chat);
    }

    public Msg selectMsg(Chat chat) {
        while (true) {

            viewSingleChat(chat);
            showExpBox("> Please select a message Number");
            try {
                int selectedMsg = Integer.parseInt(sc.nextLine()) - 1;
                if (selectedMsg >= 0 && selectedMsg < chat.getMsgs().size()) {
                    pauseExecution("\n> Message " + (selectedMsg+1) + " selected.\nPress any key.\n" );
                    return chat.getMsgs().get(selectedMsg);
                }
                else{
                    pauseExecution("> Such message does not exist! Press any key");
                }
            } catch (NumberFormatException  e) {
                pauseExecution("> No int provided");
            }
        }

    }

    public void sendMsg(Chat chat) {
        String data;
        while (true) {
            viewSingleChat(chat);

            showExpBox("> Enter your message: (250 characters max) ");
            data = sc.nextLine();

            if (data.length()<250) break;

            clrscr();
            showExpBox("> No more that 250 characters allowed!");
            pauseExecution();
        }

        if (msgDao.sendMsg(curUser, chat, data) == 1) {
            clrscr();
            showExpBox("> Message was sent!");
            pauseExecution();
        }

        execSingleChatMenu(chat);
    }

    public void notYetImplemented() {
        showExpBox("> Not yet implemented. Press any key to go back!");
        pauseExecution();
        execEditUserMenu();
    }

    public User getCurUser() {
        return curUser;
    }

}

