package ChatApp.db;

import ChatApp.core.Chat;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public class MsgDAO extends Database {

    public static MsgDAO msgDAO = null;

    private MsgDAO(){}

    public static MsgDAO getInstance() {
        if (msgDAO == null)
            msgDAO = new MsgDAO();
        return msgDAO;
    }

    public int createMsgTable(Chat chat) {

        Connection conn = createConnection();
        Statement st = null;
        int rowsInserted = 0;

        String query = new StringBuilder()
                .append("CREATE TABLE `"+ chat.msgTableName + "` (")
                .append("`id` INT NOT NULL AUTO_INCREMENT,\n" +
                        "    `user_id` INT NOT NULL,\n" +
                        "    `create_date` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,\n" +
                        "    `data` VARCHAR(200),\n" +
                        "    PRIMARY KEY (`id`),\n" +
                        "    FOREIGN KEY (`user_id`) REFERENCES `users`(`id`)\n" +
                        ");").toString();

        try {
            st = conn.createStatement();
            rowsInserted = st.executeUpdate(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rowsInserted;
    }
}
