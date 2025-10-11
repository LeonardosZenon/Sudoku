package gr.leonzch.sudoku.utils;

public class MathUtils {

    /**
     * Returns a random integer between 0 (inclusive) and the specified value (exclusive).
     * @param i
     * @return random integer
     */
    public static int getRandomInt(int i) {
        return (int) (Math.random() * i);
    }
}
