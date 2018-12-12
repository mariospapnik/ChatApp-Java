package ChatApp;

import ChatApp.core.Chat;
import ChatApp.core.User;
import ChatApp.db.Database;
import ChatApp.ui.UI;
import org.fusesource.jansi.Ansi;
import org.fusesource.jansi.AnsiConsole;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

public class Main implements UI{

    static Database db;

    Main(){
        db = new Database();

//        startChatApp();
    }

    public static void main(String[] args) {
        UI ui = new Main();
        ui.startChatApp();

//        User curUser =  new User(1,db);
//        ArrayList<Chat> chats =  new ArrayList<>();
//        String[] cols = {"id","username"};
//        db.readFromTable("users",cols);
    }

    public final static void clearConsole()
    {
        try
        {
            final String os = System.getProperty("os.name");
            System.out.println(os);
            if (os.contains("Windows")) {
                System.out.println("1");
                Runtime.getRuntime().exec("cmd /c cls");
            }
            else {
                Runtime.getRuntime().exec("clear");
            }
        }
        catch (final Exception e)
        {
            //  Handle any exceptions.
        }
    }


    public static void clearScreen() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }
}
