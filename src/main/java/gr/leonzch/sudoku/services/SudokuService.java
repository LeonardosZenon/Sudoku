package gr.leonzch.sudoku.services;

import com.google.gson.Gson;
import gr.leonzch.sudoku.enums.SudokuDifficultyConstants;
import gr.leonzch.sudoku.models.entities.sudoku_board.SudokuBoard;
import gr.leonzch.sudoku.models.entities.sudoku_board.SudokuBoardEntityDTO;
import gr.leonzch.sudoku.models.entities.sudoku_difficulty.SudokuDifficulty;
import gr.leonzch.sudoku.utils.MathUtils;
import gr.leonzch.sudoku.utils.Utils;
import gr.leonzch.sudoku.utils.logging.Logging;
import gr.leonzch.sudoku.utils.logging.LoggingTypes;
import org.springframework.stereotype.Service;

@Service
public class SudokuService {

    private final Gson gson = new Gson();

    public SudokuBoardEntityDTO generateSudoku(int difficultyLevel) {
        Logging.log(LoggingTypes.INFO, "Generating Sudoku with difficulty level: " + difficultyLevel);
        SudokuDifficultyConstants difficulty = SudokuDifficultyConstants.getById(difficultyLevel);

        int[][] solvedArray = createSolvedBoard();
        int[][] playableArray = createPlayableBoard(solvedArray, difficulty);

        SudokuBoard board = createEntityFromArrays(solvedArray, playableArray, difficulty);

        System.out.println("SOLVED:\n" + visualizeBoard(solvedArray));
        System.out.println("\nPLAYABLE:\n" + visualizeBoard(playableArray));

        int[][] testsolved = solveAndPrintAmbiguity(Utils.deepCopy(playableArray));

        return new SudokuBoardEntityDTO(board.getId(),
                board.getGridSolved(),
                solvedArray,
                board.getGridPlayable(),
                playableArray,
                board.getDifficultyEntity());

    }

    private int[][] solveAndPrintAmbiguity(int[][] board) {
        if (fillBoard(board)) {
            System.out.println("Solved board:\n" + visualizeBoard(board));
        } else {
            System.out.println("No solution exists");
        }
        return board;
    }

    private String visualizeBoard(int[][] board) {
        StringBuilder sb = new StringBuilder();
        String rowSeparator = "+-------+-------+-------+\n";
        for (int r = 0; r < 9; r++) {
            if (r % 3 == 0) sb.append(rowSeparator);
            for (int d = 0; d < 9; d++) {
                if (d % 3 == 0) sb.append("| ");
                sb.append(board[r][d] == 0 ? ". " : board[r][d] + " ");
            }
            sb.append("|\n");
        }
        sb.append(rowSeparator);
        return sb.toString();
    }


    private int[][] createPlayableBoard(int[][] solved, SudokuDifficultyConstants difficulty) {
        int[][] playable = new int[9][9];

        // Copy the solved board to the playable board
        for (int i = 0; i < 9; i++) {
            System.arraycopy(solved[i], 0, playable[i], 0, 9);
        }

        int cellsToRemove;
        switch (difficulty) {
            case EASY -> cellsToRemove = MathUtils.getRandomInt(10) + 30; // 30-39
            case MEDIUM -> cellsToRemove = MathUtils.getRandomInt(10) + 40; // 40-49
            case HARD -> cellsToRemove = MathUtils.getRandomInt(10) + 50; // 50-59
            case EXTREME -> cellsToRemove = MathUtils.getRandomInt(5) + 60; // 60-64
            default -> cellsToRemove = 40; // Default to medium if undefined
        }

        while (cellsToRemove > 0) {
            int row = MathUtils.getRandomInt(9);
            int col = MathUtils.getRandomInt(9);

            if (playable[row][col] != 0) {
                int backup = playable[row][col];
                playable[row][col] = 0;
                int[][] playableCopy = Utils.deepCopy(playable);
                if (countSolutions(playableCopy) != 1) {
                    playable[row][col] = backup; // Restore if not unique
                    continue;
                }
                cellsToRemove--;
            }
        }
        return playable;
    }

    // Global uniqueness check: returns true if board has exactly one solution
    private boolean checkOnlyOneSolution(int[][] playable) {
        return countSolutions(Utils.deepCopy(playable)) == 1;
    }

    private int countSolutions(int[][] board) {
        return countSolutionsHelper(board, 0, 0);
    }

    private int countSolutionsHelper(int[][] board, int row, int col) {
        if (row == 9) return 1; // Found a solution

        if (col == 9) return countSolutionsHelper(board, row + 1, 0);

        if (board[row][col] != 0) {
            return countSolutionsHelper(board, row, col + 1);
        }

        int count = 0;
        for (int num = 1; num <= 9; num++) {
            if (isSafe(board, row, col, num)) {
                board[row][col] = num;
                count += countSolutionsHelper(board, row, col + 1);
                if (count > 1) { // Early exit if more than one solution
                    board[row][col] = 0;
                    return count;
                }
                board[row][col] = 0;
            }
        }
        return count;
    }


    private int[][] createSolvedBoard() {
        int[][] center = randomizeBox();
        int[][] topLeft = randomizeBox();
        int[][] bottomRight = randomizeBox();

        int[][] board = placeTheRandomBoxesInTheBoard(center, topLeft, bottomRight);

        fillBoard(board);

        return board;
    }

    private boolean fillBoard(int[][] board) {
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                if (board[row][col] == 0) {
                    for (int num = 1; num <= 9; num++) {
                        if (isSafe(board, row, col, num)) {
                            board[row][col] = num;
                            if (fillBoard(board)) {
                                return true;
                            }
                            board[row][col] = 0;
                        }
                    }
                    return false; // No valid number found, trigger backtracking
                }
            }
        }
        return true; // Board is filled
    }

    private boolean isSafe(int[][] board, int row, int col, int num) {
        // Check row and column
        for (int i = 0; i < 9; i++) {
            if (board[row][i] == num || board[i][col] == num) {
                return false;
            }
        }
        // Check 3x3 box
        int boxRowStart = row - row % 3;
        int boxColStart = col - col % 3;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (board[boxRowStart + i][boxColStart + j] == num) {
                    return false;
                }
            }
        }
        return true;
    }

    private int[][] placeTheRandomBoxesInTheBoard(int[][] center, int[][] topLeft, int[][] bottomRight) {
        int[][] board = new int[9][9];

        placeBox(board, center, 3, 3);
        placeBox(board, topLeft, 0, 0);
        placeBox(board, bottomRight, 6, 6);

        return board;
    }

    private void placeBox(int[][] board, int[][] box, int startIndexX, int startIndexY) {
        for (int digit = 1; digit <= 9; digit++) {
            boolean placed = false;
            while (!placed) {
                int row = MathUtils.getRandomInt(3);
                int col = MathUtils.getRandomInt(3);
                if (board[startIndexX + row][startIndexY + col] == 0) {
                    board[startIndexX + row][startIndexY + col] = digit;
                    placed = true;
                }
            }
        }
    }


    // Fix randomizeBox to generate a true random permutation of 1-9
    private int[][] randomizeBox() {
        int[] box = new int[9];
        for (int i = 0; i < 9; i++) {
            box[i] = i + 1;
        }
        // Shuffle the array
        for (int i = 8; i > 0; i--) {
            int j = MathUtils.getRandomInt(i + 1);
            int temp = box[i];
            box[i] = box[j];
            box[j] = temp;
        }
        return convertToBox(box);
    }

    private int[][] convertToBox (int[] box) {
        int[][] result = new int[3][3];
        for (int i = 0; i < 3; i++) {
            System.arraycopy(box, i * 3, result[i], 0, 3);
        }
        return result;
    }

    private SudokuBoard createEntityFromArrays(int[][] solved, int[][] playable) {
        SudokuBoard entity = new SudokuBoard();
        entity.setGridSolved(arrayToJson(solved));
        entity.setGridPlayable(arrayToJson(playable));
        entity.setDifficulty(this.defineDifficulty(playable));
//        entity.setUserSudokuBoards(new ArrayList<>());

        return entity;
    }

    private SudokuBoard createEntityFromArrays(int[][] solved, int[][] playable, SudokuDifficultyConstants difficulty) {
        SudokuBoard entity = new SudokuBoard();
        entity.setGridSolved(arrayToJson(solved));
        entity.setGridPlayable(arrayToJson(playable));
        entity.setDifficulty(new SudokuDifficulty(difficulty));
//        entity.setUserSudokuBoards(new ArrayList<>());

        return entity;
    }

    private SudokuDifficulty defineDifficulty(int[][] playable) {
        int emptyCells = this.countEmptyCells(playable);
        if (emptyCells <= 40) {
            return new SudokuDifficulty(SudokuDifficultyConstants.EASY);
        } else if (emptyCells <= 50) {
            return new SudokuDifficulty(SudokuDifficultyConstants.MEDIUM);
        } else if (emptyCells <= 60) {
            return new SudokuDifficulty(SudokuDifficultyConstants.HARD);
        } else {
            return new SudokuDifficulty(SudokuDifficultyConstants.EXTREME);
        }

    }

    private int countEmptyCells (int[][] board) {
        int count = 0;
        for (int[] row : board) {
            for (int cell : row) {
                if (cell == 0) {
                    count++;
                }
            }
        }
        return count;
    }

    private String arrayToJson(int[][] board) {
        return gson.toJson(board);
    }
}
