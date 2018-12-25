package ChatApp.ui;

import ChatApp.core.Chat;
import ChatApp.core.User;

import java.util.ArrayList;
import java.util.Collection;

public interface ChatMethods {

    void login();
    void logout();
    void signUp();

    void setCurUserAndEnvironment(int userID);
    String usernameInput();
    String passInput(String username);
    String nameInput(String nameType);

    void searchUser();
    void viewAllUsers();
    void viewUsers( Collection<User> users);

    void viewSingleChat(Chat chat);
    void createChat();

    void editProfile();
    void toggleUser(User user);
    void changeUserType(User user);
    void editUserFLName(User user);
    ArrayList<User> selectUsers();

    void previousMsgs();
    void nextMsgs();
    void toogleMsg();
    void sendMsg(Chat chat);
    void notYetImplemented();
}
