package ChatApp.ui;

import ChatApp.core.Chat;
import ChatApp.core.User;
import ChatApp.db.Database;
import com.sun.deploy.uitoolkit.ui.ConsoleController;
import com.sun.deploy.uitoolkit.ui.ConsoleWindow;

import java.io.Console;
import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.Scanner;

public interface UI {

    Scanner sc = new Scanner(System.in);
    Database db = new Database();

    default void startChatApp() {
        mainScreen();
    }

    //method to display the main screen
    default void mainScreen() {
        while (true) {
            clrscr();
            showExpBox("ChatApp");
            showExpBox("1. Login  \n2. Sign up\n3. Exit   ");
            String choice = sc.nextLine();
            switch (choice) {
                case "1":
                    loginScreen();
                    break;
                case "2":
                    signUpScreen();
                    break;
                case "3":
                    clrscr();
                    showExpBox("Bye!");
                    return;
            }
        }
    }

    default void loginScreen() {
        clrscr();
        showExpBox("ChatApp");

        showExpBox("Enter\nUsername");
        String username = sc.nextLine();

        int userID = db.checkUser(username);    //Check if username exists in the db

        if (userID!=0) {
            clrscr();
            showExpBox("Enter\npassword");
            String pass = sc.nextLine();
            boolean isPassCorrect = db.checkPass(userID, pass); // Check if the Password is correct

            if (isPassCorrect) {
                User curUser = new User(userID);    //create the user
                db.setUser(curUser);      //Populate the user data from the DB
                clrscr();
                showExpBox("Logged in as\n"+curUser.username+"\n \nPress any key...");
                sc.nextLine();
                userScreen(curUser);
            }
//

        }
    }

    default void signUpScreen() {
        String username;
        String pass;
        clrscr();
        showExpBox("Create new user\nEnter username");
        //username input
        while(true) {
            username = sc.nextLine();
            int id = db.checkUser(username);
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
                int isCreated = db.createUser(username, pass);
                if (isCreated==1) {
                    showExpBox("User\n"+username+"\nwas created!\nPlease\nlog in.");
                }
                else{
                    showExpBox("Something\nwent wrong!\nPlease\ntry again.");
                }
                break;
            }
            else {
                showExpBox("Passwords are\nnot identical\ntry\nagain");
            }
        }


    }

    default void userScreen(User curUser) {
        clrscr();
        curUser.setRoleRights();    //Create the role for the User!
        showExpBox("1.Users\n2.Chats\n3.Back");
        String choice = sc.nextLine();
        switch (choice) {
            case "1":
                clrscr();
                showExpBox("Users");
                searchUserScreen(curUser);
                break;
            case "2":
                clrscr();
                showExpBox("Chats");
                curUser.getChats();
                viewChatsScreen(curUser);
                break;
            case "3":
                clrscr();
                showExpBox("Bye!");
                return;
        }
    }

    default void viewChatsScreen(User curUser) {
        clrscr();
        StringBuilder chatsStr = new StringBuilder().append("Chats for:"+curUser.username +"\n");
        System.out.println(curUser.chats.size());
        for(Chat chat : curUser.chats){
            chatsStr.append(chat.id + ": " + chat.chatName + "\n");
            System.out.println("one more!");
        }
        chatsStr.append("Please select:");
        showExpBox(chatsStr.toString());

        String choice = sc.nextLine();

    }

    default void searchUserScreen(User curUser) {
        clrscr();
        Map<String,Boolean> rightsOnUsers = curUser.role.rightsOnUsers;     //get the rights on Users
        //Create a String with all the Rights
        String rightsUsersStr = "";
        int i = 1;
        for (Map.Entry<String, Boolean> entry : rightsOnUsers.entrySet()) {
            if (entry.getValue()) {
                rightsUsersStr += (i + ". " + entry.getKey() + "\n");
                i++;
            }
        }
        showExpBox(rightsUsersStr);

//        showExpBox("Search for a user");
        String userSearch = sc.nextLine();
    }

    default void viewSingleChatScreen() {
    }

    // Method that clears the screen
    default void clrscr(){
        try {
            if (System.getProperty("os.name").contains("Windows"))
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            else
                Runtime.getRuntime().exec("clear");
        } catch (IOException | InterruptedException ex) {}
    }

    //method to repeat a character int times and export a string
    default String repeatChar(char c, int length) {
        char[] data = new char[length];
        Arrays.fill(data, c);
        return new String(data);
    }

    //method to display a given String inside a dynamic box
    default void showExpBox(String str) {
        //Setting the chars
        char UR = 0x2510;
        char DL = 0x2514;
        char DR = 0x2518;
        char UL = 0x250C;
        char H = 0x2500;
        char V = 0x2502;
        char S = 0x00A0;
        //creating the string for the printf
        String[] strs = str.split("\n");
        String longestStr = strs[0];

        for (String st : strs)
            if (st.length() > longestStr.length()) longestStr = st;

        int strLength = Math.max(20, longestStr.length());
        String Hor = repeatChar(H, strLength);
        String upDownLines = "%1c%-" + strLength + "s%1c\n";

        // Printing the lines
        System.out.printf(upDownLines, UL, Hor, UR);      //top line
        for (String st : strs) {
            int b = (strLength - st.length()) / 2;
            int len = st.length();
            if (((strLength - len) % 2) != 0) len++;
            String line = "%1c%" + b + "c%-" + len + "s%" + b + "c%1c\n";
            System.out.printf(line, V, S, st, S, V);    // middle lines with string
        }
        System.out.printf(upDownLines, DL, Hor, DR);      //bottom line
    }
}

