package ChatApp.core;

import java.util.ArrayList;
import java.util.Date;

//USER CHAT
public class Chat {
    private int id;
    private int creatorUserID;
    private String chatName;
    private Date chatDate;
    private ArrayList<Integer> usersIDs = new ArrayList<>();
    private ArrayList<Msg> msgs = new ArrayList<>();

    public Chat(int id){
        this.id = id;
    }

    public Chat(){}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCreatorUserID() {
        return creatorUserID;
    }

    public void setCreatorUserID(int creatorUserID) {
        this.creatorUserID = creatorUserID;
    }

    public String getChatName() {
        return chatName;
    }

    public void setChatName(String chatName) {
        this.chatName = chatName;
    }

    public Date getChatDate() {
        return chatDate;
    }

    public void setChatDate(Date chatDate) {
        this.chatDate = chatDate;
    }

    public ArrayList<Integer> getUsersIDs() {
        return usersIDs;
    }

    public void setUsersIDs(ArrayList<Integer> usersIDs) {
        this.usersIDs = usersIDs;
    }

    public ArrayList<Msg> getMsgs() {
        return msgs;
    }

    public void setMsgs(ArrayList<Msg> msgs) {
        this.msgs = msgs;
    }
}