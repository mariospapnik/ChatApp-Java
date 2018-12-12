package ChatApp.core;

import ChatApp.db_old.Database;

public class Role {
    int id;
    boolean canCreateUser;
    boolean canSearchUser;
    boolean canToogleUser;
    boolean canChangeUserType;

    boolean canCreateChat;
    boolean canSearchChat;
    boolean canGetChatTable;
    boolean canViewChats;
    boolean canEditChatName;
    boolean canToogleChat;
    boolean canDeleteChat;

    boolean canAddUserToChat;
    boolean canGetChatUsers;
    boolean canToogleChatUser;

    boolean canSendMsg;
    boolean canReadMsg;
    boolean canSearchMsg;
    boolean canToogleMsg;
    boolean canResetAllMsgs;

    public Role(int id, Database db){
        this.id  = id;
//        String[] results = db.getRole(id);
        //SET ALL THE BOOLEANS!!
    }

}
