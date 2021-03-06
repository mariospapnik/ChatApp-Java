package ChatApp.app.core;

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
    }

    public int getID(){
        return id;
    }

    public void setID(int id){
        this.id = id;
    }

    public Boolean getUserRight(String right){
        return rightsOnUsers.get(right);
    }

    public Boolean getChatsRight(String right){
        return rightsOnChats.get(right);
    }

    public Boolean getMsgsRight(String right){
        return rightsOnMsgs.get(right);
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
