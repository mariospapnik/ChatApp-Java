package ChatApp.menu;

import ChatApp.ui.UI;

import java.util.*;

public class Menu {
    private final String name;
    private final String text;
    private LinkedHashMap<String, Runnable> actionsMap = new LinkedHashMap<>();

    public Menu(String name, String text) {
        this.name = name;
        this.text = text;
    }

    public void putAction(String name, Runnable action) {

        actionsMap.put(name, action);
    }

    public void removeAction(String name) {
        actionsMap.remove(name);
    }

    public void clearActions() {
        actionsMap.clear();
    }

    public String generateText() {
        StringBuilder sb = new StringBuilder()
                .append(name + ": ")
                .append(text + ":\n");

        List<String> actionNames = new ArrayList<>(actionsMap.keySet());
        for (int i = 0 ; i < actionNames.size() ; i++) {
            sb.append(String.format(" %d: %s%n", i+1 , actionNames.get(i)));
        }
        return sb.toString();
    }

    public void executeAction(int actionSelected) {
        List<Runnable> actions = new ArrayList<>(actionsMap.values());
        actions.get(actionSelected).run();
     }

    public int userInput() {
        int effectiveActionNumber;
        int actionNumber = 0;

        while (true) {
            Scanner scanner = new Scanner(System.in);
            try {
                actionNumber = scanner.nextInt();
                effectiveActionNumber = actionNumber - 1;
            }
            catch (InputMismatchException e){
                effectiveActionNumber = -1;
            }

            if (effectiveActionNumber < 0 || effectiveActionNumber >= actionsMap.size()) {
                UI.clrscr();
                System.out.println(generateText());
                System.out.println("Ignoring menu choice: " + actionNumber);
            }
            else break;
        }

        return effectiveActionNumber;
    }

    public void activateMenu() {
        System.out.println(generateText());
        executeAction(userInput());
    }

}
