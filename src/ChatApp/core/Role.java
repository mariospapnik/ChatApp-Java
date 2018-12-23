package ChatApp.core;

import ChatApp.db.RoleDAO;
import ChatApp.db.UserDAO;
import java.util.HashMap;
import java.util.Map;

public class Role {
    private int id;
    private String roleName;
    private Map<String,Boolean> rightsOnUsers = new HashMap<>();
    private Map<String,Boolean> rightsOnChats = new HashMap<>();
    private Map<String,Boolean> rightsOnMsgs = new HashMap<>();

    public void resetRoles() {
        rightsOnUsers.replaceAll((k,v)-> false);
        rightsOnChats.replaceAll((k,v)-> false);
        rightsOnMsgs.replaceAll((k,v)-> false);
    }

    public Role(int id){
        this.id  = id;
        this.readRole();
    }

    private void readRole() {
       RoleDAO roleDao = RoleDAO.getInstance();
        roleDao.readRole(this);
    }

    public int getID(){
        return id;
    }

    public void setID(int id){
        this.id = id;
        this.readRole();  //updates the role again!
    }

    public String getRoleName(){
        return roleName;
    }

    public void setRoleName(String roleName){
        this.roleName = roleName;
    }

    public Map<String,Boolean> getRightsOnUsers(){
        return rightsOnUsers;
    }

    public void setRightsOnUsers(Map<String,Boolean> rightsOnUsers){
        this.rightsOnUsers = rightsOnUsers;
    }

    public Map<String,Boolean> getRightsOnChats(){
        return rightsOnChats;
    }

    public void setRightsOnChats(Map<String,Boolean> rightsOnChats){
        this.rightsOnChats = rightsOnChats;
    }

    public Map<String,Boolean> getRightsOnMsgs(){
        return rightsOnMsgs;
    }

    public void setRightsOnMsgs(Map<String,Boolean> rightsOnMsgs){
        this.rightsOnMsgs = rightsOnMsgs;
    }

}
