package ChatApp.db;

import ChatApp.core.Role;

import java.util.*;

public class RoleDAO extends Database {

    public static RoleDAO roleDAO = null;

    private RoleDAO(){}

    public static RoleDAO getInstance() {
        if (roleDAO == null)
            roleDAO = new RoleDAO();
        return roleDAO;
    }

    public void readRole(Role role){

        String query =  "SELECT * FROM `roles` "+
                "WHERE `id` = '" + role.getID()+ "';";

        Collection<Map<String,Object>> answer = new ArrayList<>();

        answer = getGenericSelect(query);

        //Loop through the rows and get values
        for (Map<String,Object> row: answer){
            role.setRoleName( String.valueOf(row.get("name")) );

            Map<String,Boolean> rightsOnUsers = new HashMap<>();
            Map<String,Boolean> rightsOnChats = new HashMap<>();
            Map<String,Boolean> rightsOnMsgs = new HashMap<>();

            //GET THE BOOLEANS FOR THE RIGHTS ON USERS
            rightsOnUsers.put("canCreateUser", ( ((Integer) row.get("can_create_user")) == 1) );    //1.1
            rightsOnUsers.put("canSearchUser", ( ((Integer) row.get("can_search_user")) == 1) );    //1.2
            rightsOnUsers.put("canToogleUser", ( ((Integer) row.get("can_toogle_user")) == 1) );    //1.3
            rightsOnUsers.put("canChangeUserType", ( ((Integer) row.get("can_change_user_type")) == 1) );//1.4
            rightsOnUsers.put("canEditUser", ( ((Integer) row.get("can_edit_user")) == 1) );         //1.5
            rightsOnUsers.put("canEditSelf", ( ((Integer) row.get("can_edit_self")) == 1) );        //1.6

            //GET THE BOOLEANS FOR THE RIGHTS ON CHATS
            rightsOnChats.put("canCreateChat", row.get("can_create_chat").equals("1") );            //2.1
            rightsOnChats.put("canSearchChat", row.get("can_search_chat").equals("1") );            //2.2
            rightsOnChats.put("canGetChatTable", row.get("can_get_chat_table").equals("1") );       //2.3
            rightsOnChats.put("canViewChats", row.get("can_view_chats").equals("1") );              //2.4
            rightsOnChats.put("canEditChatName",row.get("can_edit_chat_name").equals("1") );        //2.5
            rightsOnChats.put("canToogleChat", row.get("can_toggle_chat").equals("1") );            //2.6
            rightsOnChats.put("canDeleteChat", row.get("can_delete_chat").equals("1") );            //2.7
            rightsOnChats.put("canAddUserToChat",row.get("can_add_user_to_chat").equals("1") );     //2.8
            rightsOnChats.put("canGetChatUsers", row.get("can_get_chat_users").equals("1") );       //2.9
            rightsOnChats.put("canExitChat", row.get("can_exit_chat").equals("1") );                //2.10
            rightsOnChats.put("canEnterChat", row.get("can_enter_chat").equals("1") );              //2.11
            rightsOnChats.put("canToogleChatUser", row.get("can_toogle_chat_user").equals("1") );   //2.12

            //GET THE BOOLEANS FOR THE RIGHTS ON MSGS
            rightsOnMsgs.put("canSendMsg", row.get("can_send_msg").equals("1") );                   //3.1
            rightsOnMsgs.put("canReadMsg", row.get("can_read_msg").equals("1") );                   //3.2
            rightsOnMsgs.put("canSearchMsg", row.get("can_search_msg").equals("1") );               //3.3
            rightsOnMsgs.put("canToogleMsg", row.get("can_toogle_msg").equals("1") );               //3.4
            rightsOnMsgs.put("canResetAllMsgs", row.get("can_reset_all_msgs").equals("1") );        //3.5

            //SET HASMAPS TO THE ROLE OBJECT
            role.setRightsOnChats(rightsOnChats);
            role.setRightsOnUsers(rightsOnUsers);
            role.setRightsOnMsgs(rightsOnMsgs);

        }

    }

    public HashMap<Integer,String> selectRoleNames() {

        HashMap<Integer,String> roleNames = new HashMap<>();
        Collection<Map<String,Object>> answer = new ArrayList<>();

        String query = "SELECT * FROM `roles`;";

        answer = getGenericSelect(query);
        for (Map<String,Object> row: answer){
            roleNames.put( (Integer) row.get("id"), (String) (row.get("role")) );
        }

        return roleNames;
    }
}
