package ChatApp.core;

import java.util.Date;

//USER CHAT
public class Chat {
    public int id;
    public int creatorUserID;
    public String chatName;
    public String msgTableName;
    public Date chatDate;

    public Chat(int id){
        this.id = id;
    }

}