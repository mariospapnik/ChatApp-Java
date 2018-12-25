package ChatApp.app.log;


import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.logging.Logger;

/* Log
     /Chats/chat[id].log
     /Users/users.log
*/

public class ChatLog {


    public static void logChats(ResultSet rs) {

        FileWriter fw;
        File f = new File("UsersTB.txt");
        try {
            fw = new FileWriter(f,true);
            StringBuilder a = new StringBuilder().append("{");
            while(rs.next()) {
                ResultSetMetaData rsmd = rs.getMetaData();
                a.append("{");
                for (int i = 1; i <= rsmd.getColumnCount(); i++) {
                    a.append("\"" + rsmd.getColumnName(i) + "\":\"" + rs.getString(i));
                    if (!(i==rsmd.getColumnCount())) a.append(",\n");
                }
                a.append("\n}");
                if (!rs.isLast()) a.append(",\n");
            }
            a.append("}");
            fw.write(String.format(a.toString()));
            fw.write(System.lineSeparator()); //new line
            fw.flush();
            fw.close();
        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
    }

//    public static void writeToFile(ResultSet rs) {
//        FileWriter fw;
//        File f = new File("mytextfile.txt");
//        try {
//            fw = new FileWriter(f,true);
//            StringBuilder a = new StringBuilder().append("{");
//            while(rs.next()) {
//                ResultSetMetaData rsmd = rs.getMetaData();
//                a.append("{");
//                for (int i = 1; i <= rsmd.getColumnCount(); i++) {
//                    a.append("\"" + rsmd.getColumnName(i) + "\":\"" + rs.getString(i));
//                    if (!(i==rsmd.getColumnCount())) a.append(",\n");
//                }
//                a.append("\n}");
//                if (!rs.isLast()) a.append(",\n");
//            }
//            a.append("}");
//            fw.write(String.format(a.toString()));
//            fw.write(System.lineSeparator()); //new line
//            fw.flush();
//            fw.close();
//        } catch (IOException | SQLException e) {
//            e.printStackTrace();
//        }
//    }

}
