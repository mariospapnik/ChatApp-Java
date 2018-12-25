package ChatApp.ui;

import ChatApp.core.Chat;

public interface ChatMenus {

    void execMainMenu();
    void execSingleUserMenu();
    void execUsersMenu();
    void execEditUserMenu();
    void execChatsMenu();
    void execSingleChatMenu(Chat chat);
}
