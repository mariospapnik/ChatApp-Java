package ChatApp;

import ChatApp.db.Database;
import ChatApp.ui.UI;

public class Main implements UI{

    Main(){
    }

    public static void main(String[] args) {
        UI ui = new Main();
        ui.startChatApp();

//        User curUser =  new User(1,db);
//        ArrayList<Chat> chats =  new ArrayList<>();
//        String[] cols = {"id","username"};
//        db.readFromTable("users",cols);
    }

}
