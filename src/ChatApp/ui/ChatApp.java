package ChatApp.ui;

import ChatApp.core.Chat;
import ChatApp.core.Msg;
import ChatApp.core.User;
import ChatApp.db.ChatDAO;
import ChatApp.db.MsgDAO;
import ChatApp.db.RoleDAO;
import ChatApp.db.UserDAO;
import ChatApp.menu.Menu;
import ChatApp.menu.MenuItem;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Scanner;

public class ChatApp extends UI {

    public static ChatApp chatApp = null;
    Scanner sc = new Scanner(System.in);
    BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
    User curUser = null;
    HashMap<Integer, User> users = null;
    HashMap<Integer, String> roleNames = null;

    RoleDAO roleDAO = RoleDAO.getInstance();
    UserDAO userDao = UserDAO.getInstance();
    ChatDAO chatDao = ChatDAO.getInstance();
    MsgDAO msgDao = MsgDAO.getInstance();


    private ChatApp(){ }

    public static ChatApp getInstance() {
        if (chatApp == null)
            chatApp = new ChatApp();
        return chatApp;
    }

    //entry point
    public void startChatApp() {
        mainMenu();
    }

//    method to display the main screen
    private void mainMenu() {
        Menu menu = new Menu();
        menu.setTitle("Chat App");
        if(curUser == null){
            menu.addItem(new MenuItem("Log in", ChatApp.getInstance() , "login"));
            menu.addItem(new MenuItem("Sign up", ChatApp.getInstance() , "signUp"));
        }
        else {
            menu.addItem(new MenuItem("Log out", ChatApp.getInstance(), "logout"));
        }
        menu.execute();
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
                    users = userDao.selectAllUsers();
                    roleDAO.selectRoleNames(roleNames);
                    curUser = users.get(userID);
//                    userDao.setUser(curUser);      //Populate the user data from the DB
                    clrscr();
                    showExpBox("Logged in as\n" + curUser.getUsername() + "\n \nPress any key...");
                    sc.nextLine();
                    userMenu();
//                    userScreen(curUser);
                    break;
                } else {
                    clrscr();
                    showExpBox("Wrong Password!\n"+(3-tries)+" tries remaining!");
                    tries++;
                }
            }
        }
        else {
            showExpBox("User does not exist!\nPress any key.");
            sc.nextLine();
        }
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

    }

    public void userMenu(){
        clrscr();
        curUser.setRoleRights();
        Menu menu = new Menu();
        menu.setTitle("User Menu");
        menu.addItem(new MenuItem("Users", ChatApp.getInstance() , "usersMenu"));
        menu.addItem(new MenuItem("Chats", ChatApp.getInstance() , "chatsMenu"));
        menu.execute();
    }

    public void chatsMenu() {
        clrscr();

        Menu menu = new Menu();
        menu.setTitle("Chats for " + curUser.getUsername() );
        menu.addItem(new MenuItem("Create Chat", ChatApp.getInstance() , "createChat"));
        chatDao.readUserChats(curUser);
        curUser.chats.forEach((chat)-> {
            StringBuilder title = new StringBuilder()
                    .append("Chat " + chat.chatName + "\t with: ");
            for (int id : chat.usersIDs) {
                title.append(users.get(id).getUsername() + " ");
            }
            menu.addItem(new MenuItem( title.toString() ,ChatApp.getInstance(), "viewSingleChat", chat ));
        });
        menu.execute();

    }

    public void usersMenu(){
        clrscr();
        Menu menu = new Menu();
        menu.setTitle("Users Look up");
        menu.addItem(new MenuItem("Search for a user", ChatApp.getInstance() , "searchUser"));
        menu.addItem(new MenuItem("View All users", ChatApp.getInstance() , "viewAllUsers"));
        menu.execute();
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
    }

    public void viewAllUsers(){
        clrscr();
        int i=1;
        users.forEach((id,user)-> System.out.println("No" + " :" + user.getUsername() ));

        System.out.println("Press any key to go back");
        sc.nextLine();
    }

    public void viewSingleChat(Chat chat) {
        clrscr();
        chatDao.readChat(curUser, chat);
        StringBuilder chatString = new StringBuilder();
        chatString.append("Chat: " + chat.chatName + "\n\n");
        for(Msg msg: chat.msgs) {
            chatString.append(msg.id + "> " + msg.creator + "( date ) :\n\t" + msg.data + "\n\n");
        }

        showExpBox(chatString.toString());
        sc.nextLine();
    }

    public void createChat(){
        clrscr();
        Chat chat = new Chat();
        System.out.println("Enter chat name");
        String chatName  = sc.nextLine();
        chat.chatName = chatName;

        System.out.println("Select users");
        ArrayList<User> usersList = (ArrayList) users.values();

        while(true) {

            int i = 1;
            for( User user: usersList) {
                System.out.println("No"+ (i++) + " :" + user.getUsername());
            }
            System.out.println("Select a number to add user");

            String input =  sc.nextLine();
            try {
                int usertoAdd = Integer.parseInt(input);

                if ( (usertoAdd > 0) && (usertoAdd < usersList.size()) ) {
                    chat.usersIDs.add(usersList.get(usertoAdd).getID());
                }
                else {
                    System.out.println("Not a valid user");
                }
            }
            catch ( NumberFormatException e){
                System.out.println("Not a number!");
            }

            if (!UI.requestConfirmation("Do you want to continue adding users? (y/n)..")) break;
        }

        ArrayList<String> fields = new ArrayList<>();
        fields.add("name");
        fields.add("user_id");

        ArrayList<String> values;

        chatDao.createChat(chat, fields , curUser.getID() );
        chatsUsersDAO.insertUsers(chat);


    }

}

