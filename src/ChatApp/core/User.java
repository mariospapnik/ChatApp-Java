package ChatApp.core;

//import java.util.Date;


import ChatApp.ui.UI;

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
//    private Chat[] chats;

    public User() { }

    public User(int id) {
        this.id = id;
    }

    public void setUser(int id) {
//        db.setUser(id);
    }

    public void accessError(){
        System.out.println("Not allowed!");
    }

    public String[] userRights(int userID) {
        String[] rights = null;
        role = new Role(this.id, db);
        return rights;
    }

}
