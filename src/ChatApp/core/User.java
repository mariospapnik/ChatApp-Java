package ChatApp.core;

//import java.util.Date;


public class User{
    private Role role;
    private int id;
    private String username;
    private String pass;
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

    }

    public void accessError(){
        System.out.println("Not allowed!");
    }


}
