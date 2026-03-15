package com.retroroom;

import javafx.scene.input.KeyCode;

/**
 * Configurazione controlli centralizzata (stile "header").
 * Modifica questa classe per rimappare i tasti in tutto il progetto.
 */
public final class InputBindings {

    private InputBindings() {
    }

    public static final class Common {
        public static final KeyCode UP = KeyCode.W;
        public static final KeyCode DOWN = KeyCode.S;
        public static final KeyCode LEFT = KeyCode.A;
        public static final KeyCode RIGHT = KeyCode.D;

        private Common() {
        }
    }

    public static final class Menu {
        public static final KeyCode PREVIOUS_GAME = Common.UP;
        public static final KeyCode NEXT_GAME = Common.DOWN;
        public static final KeyCode START_GAME = KeyCode.P;

        private Menu() {
        }
    }

    public static final class Dungeon {
        public static final KeyCode MOVE_UP = Common.UP;
        public static final KeyCode MOVE_DOWN = Common.DOWN;
        public static final KeyCode MOVE_LEFT = Common.LEFT;
        public static final KeyCode MOVE_RIGHT = Common.RIGHT;
        public static final KeyCode BACK_TO_MENU = KeyCode.L;

        private Dungeon() {
        }
    }

    public static final class Space {
        public static final KeyCode MOVE_LEFT = Common.LEFT;
        public static final KeyCode MOVE_RIGHT = Common.RIGHT;
        public static final KeyCode FIRE_PRIMARY = Common.UP;
        public static final KeyCode FIRE_SECONDARY = KeyCode.P;
        public static final KeyCode BACK_TO_MENU = KeyCode.L;

        private Space() {
        }
    }

    public static final class Forza4 {
        public static final KeyCode MOVE_LEFT = Common.LEFT;
        public static final KeyCode MOVE_RIGHT = Common.RIGHT;
        public static final KeyCode DROP_PRIMARY = Common.DOWN;
        public static final KeyCode DROP_SECONDARY = KeyCode.P;
        public static final KeyCode BACK_TO_MENU = KeyCode.L;

        private Forza4() {
        }
    }

    public static String menuHint() {
        return "Comandi: " + keyName(Menu.PREVIOUS_GAME) + "/" + keyName(Menu.NEXT_GAME)
                + " seleziona gioco | " + keyName(Menu.START_GAME) + " avvia";
    }

    public static String dungeonHint() {
        return "Comandi: " + keyName(Dungeon.MOVE_UP) + "/" + keyName(Dungeon.MOVE_LEFT) + "/"
                + keyName(Dungeon.MOVE_DOWN) + "/" + keyName(Dungeon.MOVE_RIGHT)
                + " muovi | " + keyName(Dungeon.BACK_TO_MENU) + " menu";
    }

    public static String spaceHint() {
        return "Comandi: " + keyName(Space.MOVE_LEFT) + "/" + keyName(Space.MOVE_RIGHT)
                + " muovi | " + keyName(Space.FIRE_PRIMARY) + " o " + keyName(Space.FIRE_SECONDARY)
                + " spara | " + keyName(Space.BACK_TO_MENU) + " menu";
    }

    public static String forza4Hint() {
        return "Comandi: " + keyName(Forza4.MOVE_LEFT) + "/" + keyName(Forza4.MOVE_RIGHT)
                + " colonna | " + keyName(Forza4.DROP_PRIMARY) + " o " + keyName(Forza4.DROP_SECONDARY)
                + " inserisci | " + keyName(Forza4.BACK_TO_MENU) + " menu";
    }

    private static String keyName(KeyCode code) {
        return code.getName();
    }
}

