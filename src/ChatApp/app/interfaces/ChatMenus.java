package ChatApp.app.interfaces;

import ChatApp.app.core.Chat;

public interface ChatMenus {

    void execMainMenu();
    void execSingleUserMenu();
    void execUsersMenu();
    void execEditUserMenu();
    void execChatsMenu();
    void execSingleChatMenu(Chat chat);
    void execEditProfileMenu();
}
