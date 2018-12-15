package ChatApp.core;

import ChatApp.db.Database;
import ChatApp.ui.UI;

import java.util.HashMap;
import java.util.Map;

public class Role implements UI {
    public int id;
    public String name;
    public Map<String,Boolean> rightsOnUsers = new HashMap<>();
    public Map<String,Boolean> rightsOnChats = new HashMap<>();
    public Map<String,Boolean> rightsOnMsgs = new HashMap<>();

    //1.onUser rights
    public boolean canCreateUser;       //1.1
    public boolean canSearchUser;       //1.2
    public boolean canToogleUser;       //1.3
    public boolean canChangeUserType;   //1.4

    //2.onChat rights
    public boolean canCreateChat;       //2.1
    public boolean canSearchChat;       //2.2
    public boolean canGetChatTable;     //2.3
    public boolean canViewChats;        //2.4
    public boolean canEditChatName;     //2.5
    public boolean canToogleChat;       //2.6
    public boolean canDeleteChat;       //2.7
    public boolean canAddUserToChat;    //2.8
    public boolean canGetChatUsers;     //2.9
    public boolean canToogleChatUser;   //2.10

    //3.onMsgs rights
    public boolean canSendMsg;          //3.1
    public boolean canReadMsg;          //3.2
    public boolean canSearchMsg;        //3.3
    public boolean canToogleMsg;        //3.4
    public boolean canResetAllMsgs;     //3.5

    public Role(int id){
        this.id  = id;
        db.readRole(this);

        rightsOnUsers.put("Create user",canCreateUser);
        rightsOnUsers.put("Search for user",canSearchUser);
        rightsOnUsers.put("Disable/Enable User",canToogleUser);
        rightsOnUsers.put("Change user type",canChangeUserType);

        rightsOnChats.put("Create chat",canCreateChat);
        rightsOnChats.put("Search for chat",canSearchChat);
        rightsOnChats.put("Get all chats",canGetChatTable);
        rightsOnChats.put("View chats",canViewChats);
        rightsOnChats.put("Edit chat name",canEditChatName);
        rightsOnChats.put("Disable/Enable chat",canToogleChat);
        rightsOnChats.put("Delete chat",canDeleteChat);
        rightsOnChats.put("Add user to chat",canAddUserToChat);
        rightsOnChats.put("See chat's users",canGetChatUsers);
        rightsOnChats.put("Disable/Enable chat user",canToogleChatUser);

        rightsOnMsgs.put("Send a message",canSendMsg);
        rightsOnMsgs.put("Read a message",canReadMsg);
        rightsOnMsgs.put("Search for a message",canSearchMsg);
        rightsOnMsgs.put("Enable/Disable a message",canToogleMsg);
        rightsOnMsgs.put("Reset all messages",canResetAllMsgs);

    }

//    canSearchUser;        /1
//    canToogleUser;        /2
//    canChangeUserType;    /3
//
//    canCreateChat;
//    canSearchChat;
//    canGetChatTable;
//    canViewChats;
//    canEditChatName;
//    canToogleChat;
//    canDeleteChat;
//
//    canAddUserToChat;
//    canGetChatUsers;
//    canToogleChatUser;
//
//    canSendMsg;
//    canReadMsg;
//    canSearchMsg;
//    canToogleMsg;
//    canResetAllMsgs;

}
