package ChatApp;

import ChatApp.ui.UI;

public class Main implements UI{

    Main(){
    }

    public static void main(String[] args) {
        UI ui = new Main();
        ui.startChatApp();
    }

}
