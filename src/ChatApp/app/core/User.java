package ChatApp.app.core;

import java.util.ArrayList;
import java.util.Date;

public class User{
    private Role role;
    private int id;
    private String username;
    private String fname;
    private String lname;
    private Date regDate;
    private boolean active;

    private ArrayList<Chat> chats = new ArrayList<>();

    //CONSTRUCTORS
    public User() { }

    public User(int id) {
        this.id = id;
    }

    //GETTERS - SETTERS
    public Role getRole(){
        return role;
    }

    public void setRole(Role role){
        this.role = role;
    }

    public int getID(){
        return id;
    }

    public void setID(int id){
        this.id = id;
    }

    public String getUsername(){
        return username;
    }

    public void setUsername(String username){
        this.username = username;
    }

    public String getFname(){
        return fname;
    }

    public void setFname(String fname){
        this.fname = fname;
    }

    public String getLname(){
        return lname;
    }

    public void setLname(String lname){
        this.lname = lname;
    }

    public boolean getActive() {
        return active;
    }

    public void setActive(boolean active){
        this.active = active;
    }

    public void setChats(ArrayList<Chat> chats) {
        this.chats = chats;
    }

    public ArrayList<Chat> getChats() {
        return chats;
    }


    public void accessError(){
        System.out.println("Not allowed!");
    }

    public Date getRegDate() {
        return regDate;
    }

    public void setRegDate(Date regDate) {
        this.regDate = regDate;
    }
}
