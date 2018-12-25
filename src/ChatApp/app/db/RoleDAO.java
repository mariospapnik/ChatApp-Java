package ChatApp.app.db;

import ChatApp.app.core.Role;
import ChatApp.app.db.dbcore.Database;

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


            String[][] rightsOnUsersString = {
                    {"canCreateUser","can_create_user"},             //1.1
                    {"canSearchUser","can_search_user"},             //1.2
                    {"canToogleUser","can_toogle_user"},             //1.3
                    {"canChangeUserType","can_change_user_type"},    //1.4
                    {"canEditUser","can_edit_user"},                 //1.5
                    {"canEditSelf","can_edit_self"}                  //1.6
            };

            String[][] rightsOnChatsString = {
                    {"canCreateChat", "can_create_chat"},            //2.1
                    {"canSearchChat", "can_search_chat"},            //2.2
                    {"canGetChatTable", "can_get_chat_table"},       //2.3
                    {"canViewChats", "can_view_chats"},              //2.4
                    {"canEditChatName", "can_edit_chat_name"},       //2.5
                    {"canToogleChat", "can_toggle_chat"},            //2.6
                    {"canDeleteChat", "can_delete_chat"},            //2.7
                    {"canAddUserToChat", "can_add_user_to_chat"},    //2.8
                    {"canGetChatUsers", "can_get_chat_users"},       //2.9
                    {"canExitChat", "can_exit_chat"},                //2.10
                    {"canEnterChat", "can_enter_chat"},              //2.11
                    {"canToogleChatUser", "can_toogle_chat_user"},   //2.12
            };

            String[][] rightsOnMsgsString = {
                    {"canSendMsg", "can_send_msg"},                 //3.1
                    {"canReadMsg", "can_read_msg"},                 //3.2
                    {"canSearchMsg", "can_search_msg"},             //3.3
                    {"canToogleMsg", "can_toogle_msg"},             //3.4
                    {"canResetAllMsgs", "can_reset_all_msgs"},      //3.5
                    {"canDeleteMsg" , "can_delete_msg"}             //3.6
            };

            //GET THE BOOLEANS FOR THE RIGHTS ON USERS
            for (String[] userRight: rightsOnUsersString ) {
                rightsOnUsers.put( userRight[0], ( ((Integer) row.get( userRight[1]) ) == 1) );
            }

            //GET THE BOOLEANS FOR THE RIGHTS ON CHATS
            for (String[] chatRight: rightsOnChatsString ) {
                rightsOnChats.put( chatRight[0], ( ((Integer) row.get( chatRight[1]) ) == 1) );
            }

            //GET THE BOOLEANS FOR THE RIGHTS ON MSGS
            for (String[] msgRight: rightsOnMsgsString ) {
                rightsOnMsgs.put( msgRight[0], ( ((Integer) row.get( msgRight[1]) ) == 1) );
            }


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
