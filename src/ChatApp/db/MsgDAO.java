package ChatApp.db;

public class MsgDAO extends Database {

    public static MsgDAO msgDAO = null;

    private MsgDAO(){}

    public static MsgDAO getInstance() {
        if (msgDAO == null)
            msgDAO = new MsgDAO();
        return msgDAO;
    }
}
