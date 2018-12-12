package ChatApp.ui;

import ChatApp.db.Database;
import com.sun.deploy.uitoolkit.ui.ConsoleController;
import com.sun.deploy.uitoolkit.ui.ConsoleWindow;

import java.io.Console;
import java.io.IOException;
import java.util.Arrays;
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
        int id = db.checkUser(username);
        if (id!=0) {
            clrscr();
            showExpBox("Enter\npassword");
            String pass = sc.nextLine();
            boolean isPassCorrect = db.checkPass(id, pass);
            if (isPassCorrect) {
                clrscr();
                showExpBox("Logged in as\nadmin\n \nPress any key...");
                sc.nextLine();
                userScreen();
            }
//

        }
    }

    default void signUpScreen() {
        String username;
        String pass;
        clrscr();
        showExpBox("Create new user\nEnter username");
        username = sc.nextLine();
        int id = db.checkUser(username);
        if (id==0) {
            clrscr();
            showExpBox(username+"\n Enter password");
            pass = sc.nextLine();
        }

    }

    default void userScreen() {
        clrscr();
        showExpBox("admin");
        showExpBox("1.Add User\n2.View Users\n3.View Chats\n4.check\n5.Log out");
        String choice = sc.nextLine();
        switch (choice) {
            case "1":
                break;
            case "2":
                break;
            case "5":
                clrscr();
                showExpBox("Bye!");
                return;
        }
    }

    default void searchUserScreen() {
    }

    default void searchChatScreen() {
    }

    default void viewChatsScreen() {
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

