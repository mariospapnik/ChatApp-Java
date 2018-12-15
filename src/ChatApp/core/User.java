package ChatApp.core;

//import java.util.Date;


import ChatApp.ui.UI;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class User implements UI {
    public Role role;
    public int roleID;
    public int id;
    public String username;
//    public String pass;
//    private String fname;
//    private String lname;
//    private int role_id;
//    private Date regDate;
//    private boolean active;
    public ArrayList<Chat> chats = new ArrayList<>();

    public User() { }

    public User(int id) {
        this.id = id;
    }

    public void setRoleRights(){
        role = new Role(roleID);    //set the role id and create the new Role inside the curUser
    }

    public void getChats() {
        db.readUserChats(this);
    }
    public void accessError(){
        System.out.println("Not allowed!");
    }

}
