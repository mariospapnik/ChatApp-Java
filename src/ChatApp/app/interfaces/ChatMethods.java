package ChatApp.app.interfaces;

import ChatApp.app.core.Chat;
import ChatApp.app.core.Msg;
import ChatApp.app.core.User;

import java.util.ArrayList;
import java.util.Collection;

public interface ChatMethods {

    void login();
    void logout();
    void signUp();

    void setCurUserAndEnvironment(int userID);
    String usernameInput();
    String newPassInput(String username);
    String nameInput(String nameType);
    String readPass();

    void searchUser();
    void viewAllUsers();
    void viewUsers( Collection<User> users);
    void printRolesNames();
    void printUserDetailsTitles();
    void printUserDetails(User user, int i);
    void updateUserPassword();

    void viewSingleChat(Chat chat);
    void createChat();
    void deleteChat(Chat chat);
    String chatNameInput();
    void addUsersToChat(Chat chat);
    void removeUsersFromChat(Chat chat);
    void exitChat(Chat chat);


    void toggleUser(User user);
    void changeUserType(User user);
    void editUserFLName(User user);
    ArrayList<User> selectFromUsers();

    Msg selectMsg(Chat chat);
//    void previousMsgs();
//    void nextMsgs();
    void toogleMsg(Chat chat);
    void deleteMsg(Chat chat);
    void sendMsg(Chat chat);
    void notYetImplemented();
}
