package ChatApp.core;

import ChatApp.db.ChatDAO;

import java.security.Timestamp;
import java.util.ArrayList;

public class User{
    private Role role;
    private int roleID;
    private int id;
    private String username;
    private String fname;
    private String lname;
    private Timestamp regDate;
    private boolean active;

    public ArrayList<Chat> chats = new ArrayList<>();

    public User() { }

    public User(int id) {
        this.id = id;
    }
    //Getters-Setters
    public Role getRole(){
        return role;
    }

    public  void setRole(Role role){
        this.role = role;
        roleID = role.getID();
    }

    public int getRoleID(){
        return roleID;
    }

    public void setRoleID(int roleID){
        this.roleID = roleID;
        role = new Role(roleID);    //update the role when the id is updated!!
    }

    public void setRoleRights(){
        role = new Role(roleID);    //set the role id and create the new Role inside the curUser
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



    public void getChats() {
        ChatDAO.getInstance().readUserChats(this);
    }

    public void accessError(){
        System.out.println("Not allowed!");
    }

}
