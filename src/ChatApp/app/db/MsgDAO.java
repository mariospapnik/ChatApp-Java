package ChatApp.app.db;

import ChatApp.app.core.Chat;
import ChatApp.app.core.Msg;
import ChatApp.app.core.User;
import ChatApp.app.db.dbcore.Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Map;

public class MsgDAO extends Database {

    public static MsgDAO msgDAO = null;

    private MsgDAO(){}

    public static MsgDAO getInstance() {
        if (msgDAO == null)
            msgDAO = new MsgDAO();
        return msgDAO;
    }

    public void readChatMsgs(User curUser, Chat chat){

        //SELECT MSGS TABLE OF THE CHAT
        String queryMsgs = "SELECT * FROM `msgs` WHERE `chat_id` = '"+ chat.getId() + "' ORDER BY `create_date` ASC LIMIT 100;";

        Collection<Map<String,Object>> answerMsgs = new ArrayList<>();
        answerMsgs = getGenericSelect(queryMsgs);

        chat.getMsgs().clear();

        for (Map<String,Object> row: answerMsgs) {
            Msg msg = new Msg();
            msg.setId( (Integer) row.get("id") );
            msg.setCreator( (Integer) row.get("user_id") );
            msg.setSentDate( (Date) row.get("create_date"));
            msg.setData( (String) row.get("data") );
            msg.setActive( ((Integer) row.get("active"))  == 1);

            chat.getMsgs().add(msg);
        }
    }

    public int sendMsg(User curUser, Chat chat, String data) {

        Connection conn = createConnection();
        PreparedStatement prest = null;
        int msgInserted = 0;

        String query = "INSERT INTO `msgs` (`chat_id`, `user_id`,`data`) VALUES (?,?,?) ;";

        try {

            prest = conn.prepareStatement(query);
            prest.setInt(1, chat.getId());
            prest.setInt(2, curUser.getID());
            prest.setString(3, data);

            msgInserted = prest.executeUpdate();
            prest.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return msgInserted;

    }

    public int toogleMsg(Msg msg) {
        int toogled = 0;
        int toogle = (msg.isActive())? 0 : 1;
        String query = "UPDATE `msgs` SET `active` = '"+ toogle + "' WHERE `id` = '" + msg.getId() + "';";
        toogled = execUpdateInsert(query);
        return toogled;
    }

    public int deleteMsg(Msg msg) {
        int deletedMsg;
        String query = "DELETE FROM `msgs` WHERE `id` = '" + msg.getId() + "';";
        deletedMsg = execUpdateInsert(query);
        return deletedMsg;
    }
}

