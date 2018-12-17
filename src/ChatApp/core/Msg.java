package ChatApp.core;

import java.util.Date;

//USER MSGS
public class Msg {
    public int id;
    public int creator;
    public String data;
    public Date sentDate = new Date();

    public Msg() {}
}