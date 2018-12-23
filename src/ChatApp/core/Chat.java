package ChatApp.core;

import java.util.ArrayList;
import java.util.Date;

//USER CHAT
public class Chat {
    public int id;
    public int creatorUserID;
    public String chatName;
    public String msgTableName;
    public Date chatDate;
    public ArrayList<Integer> usersIDs = new ArrayList<>();
    public ArrayList<Msg> msgs = new ArrayList<>();

    public Chat(int id){
        this.id = id;
    }

    public Chat(){}

}