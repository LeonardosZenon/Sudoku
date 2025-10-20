package gr.leonzch.sudoku.enums;

import lombok.Getter;

@Getter
public enum SudokuStatusConstants {
    SOLVED(1, "SOLVED"),
    LOST(2, "LOST"),
    IN_PROGRESS(3, "IN_PROGRESS");

    private final long id;
    private final String label;

    SudokuStatusConstants(long id, String label) {
        this.id = id;
        this.label = label;
    }

    public static int length() {
        return values().length;
    }

    public static boolean exists(long id) {
        for (SudokuStatusConstants constant : values()) {
            if (constant.id == id) {
                return true;
            }
        }
        return false;
    }

    public String getLabelById(long id) {
        return exists(id) ? values()[(int) id - 1].getLabel() : null;
    }

    public static SudokuStatusConstants getById(long id) {
        for (SudokuStatusConstants status : values()) {
            if (status.id == id) {
                return status;
            }
        }
        return null;
    }
}
