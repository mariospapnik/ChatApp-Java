package ChatApp;

import ChatApp.core.Chat;
import ChatApp.core.User;
import ChatApp.db.Database;
import java.util.ArrayList;

public class Main {

    static Database db;

    Main(){
        db = new Database();
    }

    public static void main(String[] args) {

        new Main();
        User curUser =  new User(1,db);
        ArrayList<Chat> chats =  new ArrayList<>();
        String[] cols = {"id","username"};
        db.readFromTable("users",cols);




    }
}
