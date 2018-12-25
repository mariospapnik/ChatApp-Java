package ChatApp.ui;

import java.io.IOException;
import java.util.Arrays;

import java.io.Console;

public class Utilities {

    private static Console console = null;

    // Method that clears the screen
    public static void clrscr(){
        try {
            if (System.getProperty("os.name").contains("Windows"))
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            else
                Runtime.getRuntime().exec("clear");
        } catch (IOException | InterruptedException ex) {}
    }

    //method to repeat a character int times and export a string
    public static String repeatChar(char c, int length) {
        char[] data = new char[length];
        Arrays.fill(data, c);
        return new String(data);
    }

    //method to display a given String inside a dynamic box
    public static void showExpBox(String str) {
        //Setting the chars
        char UR = 0x2510;
        char DL = 0x2514;
        char DR = 0x2518;
        char UL = 0x250C;
        char H = 0x2500;
        char V = 0x2502;
        char S = 0x00A0;
        //creating the string for the printf
        String[] strs = str.split("\n");
        String longestStr = strs[0];

        for (String st : strs)
            if (st.length() > longestStr.length()) longestStr = st;


        int strLength = Math.max(20, longestStr.length());
        String Hor = repeatChar(H, strLength);
        System.out.println(UL + Hor + UR);
        for (String st : strs) {
            System.out.println(V + st + repeatChar(S, strLength - st.length()) + V );
        }
        System.out.println(DL + Hor + DR);

    }

    public static void pauseExecution() {
        pauseExecution("Press Enter to Continue... ");
    }

    public static void pauseExecution(String message) {
        if(console == null) console = System.console();
        System.out.print(message);
        console.readLine();
    }

    /*
     * This method can be used if a particular operation requires confirmation, it is useful for delete
     * or irreversible operations. It forces that the uses explicitly enters "y/yes" or "n/no", any
     * other input will fail and the confirmation request will be presented again.
     */
    public static boolean requestConfirmation() {
        return requestConfirmation("Confirm Operation (y/n)... ");
    }

    public static boolean requestConfirmation(String message) {
        String in = "";
        if(console == null) console = System.console();

        while (true) {
            System.out.print(message);
            in = console.readLine().toLowerCase();
            if(in.equals("y") || in.equals("yes"))
                return true;
            else if(in.equals("n") || in.equals("no"))
                return false;
        }
    }
}
