package Java;

import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;

public class KeyboardShortcuts {
    
    private static KeyboardShortcuts ourInstance = new KeyboardShortcuts();

    public static KeyboardShortcuts getInstance() {
        return ourInstance;
    }
    
    private KeyboardShortcuts() {
    }
    
    
    //general
    public static final KeyCombination FULLSCREEN = new KeyCodeCombination(KeyCode.F11);
    public static final KeyCombination BACK = new KeyCodeCombination(KeyCode.BACK_SPACE);
    public static final KeyCombination REFRESH = new KeyCodeCombination(KeyCode.F5);
    public static final KeyCombination REFRESH_ALTERNATIVE = new KeyCodeCombination(KeyCode.R, KeyCombination.CONTROL_DOWN);
    public static final KeyCombination CHANGE_THEME = new KeyCodeCombination(KeyCode.T, KeyCombination.CONTROL_DOWN);


    //website
    public static final KeyCombination OPEN_MAL = new KeyCodeCombination(KeyCode.M, KeyCombination.CONTROL_DOWN);
    public static final KeyCombination OPEN_ANIDB = new KeyCodeCombination(KeyCode.D, KeyCombination.CONTROL_DOWN);


    //show select
    public static final KeyCombination SEARCH = new KeyCodeCombination(KeyCode.F, KeyCombination.CONTROL_DOWN);
    public static final KeyCombination SORT = new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN);
    public static final KeyCombination SEARCH_SELECT = new KeyCodeCombination(KeyCode.ENTER);
    

    public static final KeyCombination UPDATE_FILES = new KeyCodeCombination(KeyCode.U, KeyCombination.CONTROL_DOWN);

    
    
    
    
}
