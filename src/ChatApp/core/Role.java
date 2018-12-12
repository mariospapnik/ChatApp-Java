package ChatApp.core;

import ChatApp.db.Database;
import java.util.Map;

public class Role {
    public int id;
    public boolean canCreateUser;
    public boolean canSearchUser;
    public boolean canToogleUser;
    public boolean canChangeUserType;

    public boolean canCreateChat;
    public boolean canSearchChat;
    public boolean canGetChatTable;
    public boolean canViewChats;
    public boolean canEditChatName;
    public boolean canToogleChat;
    public boolean canDeleteChat;

    public boolean canAddUserToChat;
    public boolean canGetChatUsers;
    public boolean canToogleChatUser;

    public boolean canSendMsg;
    public boolean canReadMsg;
    public boolean canSearchMsg;
    public boolean canToogleMsg;
    public boolean canResetAllMsgs;

    public Role(int id){
        this.id  = id;
        this = db.readRole(this);
//        String[] results = db.getRole(id);
        //SET ALL THE BOOLEANS!!
    }

}
