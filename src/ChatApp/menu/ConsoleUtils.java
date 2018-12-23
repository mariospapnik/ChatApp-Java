package ChatApp.menu;

import java.io.Console;
import java.io.IOException;
import java.util.Arrays;

/*
 * ConsoleUtils exposes a few useful methods which can be used across a broad range of console apps.
 */ 
public class ConsoleUtils {
	private static Console console = null;
	
	/*
	 * This method will force execution to stop and wait until the user presses enter. It prompts the 
	 * user to press enter to continue. It is irrelevant if the user types any text, execution will 
	 * continue after 'Enter'.
	 */

        
    public final static void clearConsole()
    {
        try
        {
            final String os = System.getProperty("os.name");
            System.out.println(os);
            if (os.contains("Windows"))
            {
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            }
            else
            {
                Runtime.getRuntime().exec("clear");
                System.out.print("\033\143");
            }
        }
        catch (final IOException | InterruptedException e) {
        }
    }

    //method to repeat a character int times and export a string
    public static String repeatChar(char c, int length) {
        char[] data = new char[length];
        Arrays.fill(data, c);
        return new String(data);
    }

}