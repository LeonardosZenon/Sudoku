package gr.leonzch.sudoku.enums;

import lombok.Getter;

@Getter
public enum SudokuDifficultyConstants {
    EASY(1, "EASY"),
    MEDIUM(2, "MEDIUM"),
    HARD(3, "HARD"),
    EXTREME(4, "EXTREME");

    private final long id;
    private final String label;

    SudokuDifficultyConstants(long id, String label) {
        this.id = id;
        this.label = label;
    }

    public static int length() {
        return values().length;
    }

    public static boolean exists(long id) {
        for (SudokuDifficultyConstants constant : values()) {
            if (constant.id == id) {
                return true;
            }
        }
        return false;
    }

    public String getLabelById(long id) {
        return exists(id) ? values()[(int) id - 1].getLabel() : null;
    }

    public static SudokuDifficultyConstants getById(long id) {
        for (SudokuDifficultyConstants difficulty : values()) {
            if (difficulty.id == id) {
                return difficulty;
            }
        }
        return null;
    }
}
