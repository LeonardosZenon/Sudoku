package gr.leonzch.sudoku.utils;

import gr.leonzch.sudoku.enums.SudokuDifficultyConstants;
import gr.leonzch.sudoku.enums.SudokuStatusConstants;
import gr.leonzch.sudoku.models.entities.constant_entities.sudoku_difficulty.SudokuDifficulty;
import gr.leonzch.sudoku.models.entities.constant_entities.sudoku_status.SudokuStatus;
import gr.leonzch.sudoku.models.entities.sudoku_board.SudokuBoard;
import gr.leonzch.sudoku.models.entities.sudoku_board.SudokuBoardEntityDTO;
import gr.leonzch.sudoku.models.entities.user.User;
import gr.leonzch.sudoku.models.entities.user_sudoku_board.UserSudokuBoard;
import gr.leonzch.sudoku.models.entities.user_sudoku_board.UserSudokuBoardId;
import gr.leonzch.sudoku.models.repositories.SudokuBoardRepository;
import gr.leonzch.sudoku.models.repositories.UserRepository;
import gr.leonzch.sudoku.models.repositories.UserSudokuBoardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class SudokuUtils {

    private final SudokuBoardRepository sudokuBoardRepository;
    private final UserSudokuBoardRepository userSudokuBoardRepository;
    private final UserRepository userRepository;

    public SudokuUtils(SudokuBoardRepository sudokuBoardRepository,
                       UserSudokuBoardRepository userSudokuBoardRepository,
                       UserRepository userRepository) {
        this.sudokuBoardRepository = sudokuBoardRepository;
        this.userSudokuBoardRepository = userSudokuBoardRepository;
        this.userRepository = userRepository;
    }

    public Optional<SudokuBoard> generatedBoardByOther(long difficulty, long userId) {
        assert SudokuDifficultyConstants.getById(difficulty) != null;
        return sudokuBoardRepository.findTopByDifficultyEntityAndIdNotIn(
                new SudokuDifficulty(SudokuDifficultyConstants.getById(difficulty)),
                userSudokuBoardRepository.findSudokuBoardIdByUser_Id(userId)
        );
    }

    public SudokuBoard generateNewBoard(long difficulty) {
        int[][] solvedArray;
        int[][] playableArray;
        do {
            solvedArray = createSolvedBoard();
            playableArray = createPlayableBoard(solvedArray, SudokuDifficultyConstants.getById(difficulty));
        } while (sudokuBoardRepository.findByGridPlayable(sudoku2DArrayToString(playableArray)).isPresent());

        SudokuBoard board = createEntityFromArrays(solvedArray, playableArray, SudokuDifficultyConstants.getById(difficulty));
        sudokuBoardRepository.save(board);

        return board;
    }

    public SudokuBoardEntityDTO createDTOForNewBoard(SudokuBoard sudokuBoard) {
        SudokuBoardEntityDTO sudokuBoardEntityDTO = new SudokuBoardEntityDTO();

        sudokuBoardEntityDTO.setId(sudokuBoard.getId());
        sudokuBoardEntityDTO.setGridCurrent(sudokuBoard.getGridPlayable());
        sudokuBoardEntityDTO.setGridCurrentArray(sudokuStringTo2DArray(sudokuBoard.getGridPlayable()));
        sudokuBoardEntityDTO.setGridPlayable(sudokuBoard.getGridPlayable());
        sudokuBoardEntityDTO.setGridPlayableArray(sudokuStringTo2DArray(sudokuBoard.getGridPlayable()));
        sudokuBoardEntityDTO.setDifficultyEntity(sudokuBoard.getDifficultyEntity());
        sudokuBoardEntityDTO.setStatus(new SudokuStatus(SudokuStatusConstants.IN_PROGRESS));
        sudokuBoardEntityDTO.setWrongValidationsCount(0);

        return sudokuBoardEntityDTO;
    }

    public int[][] sudokuStringTo2DArray(String gridString) {
        if (gridString.length() != 81) throw new IllegalArgumentException("Invalid Sudoku string length");

        int[][] gridArray = new int[9][9];
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                char c = gridString.charAt(i * 9 + j);
                gridArray[i][j] = Character.getNumericValue(c);
            }
        }
        return gridArray;
    }

    public String sudoku2DArrayToString(int[][] gridArray) {
        StringBuilder gridString = new StringBuilder();
        for (int[] row : gridArray) {
            for (int num : row) {
                gridString.append(num);
            }
        }
        return gridString.toString();
    }

    public int[][] createSolvedBoard() {
        int[][] center = randomizeBox();
        int[][] topLeft = randomizeBox();
        int[][] bottomRight = randomizeBox();

        int[][] board = placeTheRandomBoxesInTheBoard(center, topLeft, bottomRight);

        fillBoard(board);

        return board;
    }

    public int[][] randomizeBox() {
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

    public int[][] convertToBox (int[] box) {
        int[][] result = new int[3][3];
        for (int i = 0; i < 3; i++) {
            System.arraycopy(box, i * 3, result[i], 0, 3);
        }
        return result;
    }

    public int[][] placeTheRandomBoxesInTheBoard(int[][] center, int[][] topLeft, int[][] bottomRight) {
        int[][] board = new int[9][9];

        placeBox(board, center, 3, 3);
        placeBox(board, topLeft, 0, 0);
        placeBox(board, bottomRight, 6, 6);

        return board;
    }

    public void placeBox(int[][] board, int[][] box, int startIndexX, int startIndexY) {
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

    public boolean fillBoard(int[][] board) {
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

    public int[][] createPlayableBoard(int[][] solved, SudokuDifficultyConstants difficulty) {
        int[][] playable = Utils.deepCopy(solved);

        int minRemove, maxRemove;
        switch (difficulty) {
            case EASY -> {
                minRemove = 30; maxRemove = 39;
            }
            case MEDIUM -> {
                minRemove = 37; maxRemove = 46;
            }
            case HARD -> {
                minRemove = 44; maxRemove = 53;
            }
            case EXTREME -> {
                minRemove = 51; maxRemove = 59;
            }
            default -> {
                minRemove = 40; maxRemove = 49;
            }
        }

        int cellsToRemove = MathUtils.getRandomInt(maxRemove - minRemove + 1) + minRemove;

        int removedCount = 0;
        int attempts = 0;
        int maxAttempts = 100000;
        int failedInRow = 0;
        int maxFailsBeforeBackup = 3000;

        int[][] backup1 = Utils.deepCopy(playable);
        int[][] backup2 = Utils.deepCopy(playable);

        int bestRemoved = 0;
        int[][] bestBoard = Utils.deepCopy(playable);

        while (removedCount < cellsToRemove && attempts < maxAttempts) {
            attempts++;

            int row = MathUtils.getRandomInt(9);
            int col = MathUtils.getRandomInt(9);

            if (playable[row][col] == 0) continue;

            int backupValue = playable[row][col];
            playable[row][col] = 0;

            int[][] copy = Utils.deepCopy(playable);
            if (countSolutions(copy) == 1) {
                removedCount++;
                failedInRow = 0;

                if (removedCount > bestRemoved) {
                    bestRemoved = removedCount;
                    bestBoard = Utils.deepCopy(playable);
                }

                if (removedCount % 10 == 0) {
                    backup2 = Utils.deepCopy(backup1);
                    backup1 = Utils.deepCopy(playable);
                }

            } else {
                playable[row][col] = backupValue;
                failedInRow++;
            }

            if (failedInRow > maxFailsBeforeBackup) {
                System.out.println("Too many failed removals in a row, reverting to a previous backup...");
                playable = Utils.deepCopy(Math.random() > 0.5 ? backup1 : backup2);
                failedInRow = 0;

                removedCount = countZeros(playable);

                if (removedCount > bestRemoved) {
                    bestRemoved = removedCount;
                    bestBoard = Utils.deepCopy(playable);
                }

                if (removedCount >= minRemove && removedCount <= maxRemove) {
                    System.out.println("Backup is already within target range (" + removedCount + " cells removed). Returning board early.");
                    visualiseBoard(solved, playable);
                    return playable;
                }
            }
        }

        if (removedCount >= cellsToRemove) {
            System.out.println("Goal reached! Removed " + removedCount + " cells (" + difficulty + ")");
            visualiseBoard(solved, playable);
            return playable;
        } else {
            System.out.println("Could not reach target (" + cellsToRemove + "). Best achieved: " + bestRemoved + " cells removed.");
            visualiseBoard(solved, bestBoard);
            return bestBoard;
        }
    }

    private int countZeros(int[][] board) {
        int count = 0;
        for (int[] row : board) {
            for (int cell : row) {
                if (cell == 0) count++;
            }
        }
        return count;
    }

    public void visualiseBoard(int[][] solved, int[][] playable) {
        System.out.println("\n==================== SUDOKU BOARD ====================");
        System.out.println("Solved board:");
        printBoard(solved, false);
        System.out.println("\nPlayable board (0 = empty):");
        printBoard(playable, true);
        System.out.println("======================================================\n");
    }

    private void printBoard(int[][] board, boolean showEmptyAsDot) {
        for (int row = 0; row < 9; row++) {
            if (row % 3 == 0 && row != 0) {
                System.out.println("------+-------+------");
            }

            for (int col = 0; col < 9; col++) {
                if (col % 3 == 0 && col != 0) {
                    System.out.print("| ");
                }

                int value = board[row][col];
                if (value == 0 && showEmptyAsDot) {
                    System.out.print(". ");
                } else {
                    System.out.print(value + " ");
                }
            }
            System.out.println();
        }
    }

    public int countSolutions(int[][] board) {
        return countSolutionsHelper(board, 0, 0);
    }

    public int countSolutionsHelper(int[][] board, int row, int col) {
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

    public boolean isSafe(int[][] board, int row, int col, int num) {
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

    public SudokuBoard createEntityFromArrays(int[][] solved, int[][] playable, SudokuDifficultyConstants difficulty) {
        SudokuBoard entity = new SudokuBoard();
        entity.setGridSolved(sudoku2DArrayToString(solved));
        entity.setGridPlayable(sudoku2DArrayToString(playable));
        entity.setDifficulty(new SudokuDifficulty(difficulty));

        return entity;
    }

    public void createUserSudokuBoardEntity(SudokuBoard sudokuBoard, long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));

        UserSudokuBoardId id = new UserSudokuBoardId();
        id.setUserId(userId);
        id.setSudokuBoardId(sudokuBoard.getId());

        UserSudokuBoard userSudokuBoard = new UserSudokuBoard();
        userSudokuBoard.setId(id);
        userSudokuBoard.setSudokuBoard(sudokuBoard);
        userSudokuBoard.setUser(user);
        userSudokuBoard.setGridCurrent(sudokuBoard.getGridPlayable());
        userSudokuBoard.setStatus(new SudokuStatus(SudokuStatusConstants.IN_PROGRESS));
        userSudokuBoard.setWrongValidationsCount(0);

        userSudokuBoardRepository.save(userSudokuBoard);
    }

    public UserSudokuBoard findUserSudokuBoardInProgress(long userId) {
        return findOptionalUserSudokuBoardInProgress(userId)
                .orElseThrow(() -> new IllegalArgumentException("No Sudoku board in progress for user id: " + userId));
    }

    public Optional<UserSudokuBoard> findOptionalUserSudokuBoardInProgress(long userId) {
        return userSudokuBoardRepository.findTopByUser_IdAndStatus_Id(userId, SudokuStatusConstants.IN_PROGRESS.getId());
    }

    public SudokuBoardEntityDTO createDTOToContinueBoard(UserSudokuBoard userSudokuBoard) {
        SudokuBoardEntityDTO sudokuBoardEntityDTO = new SudokuBoardEntityDTO();

        sudokuBoardEntityDTO.setId(userSudokuBoard.getSudokuBoard().getId());
        sudokuBoardEntityDTO.setGridCurrent(userSudokuBoard.getGridCurrent());
        sudokuBoardEntityDTO.setGridCurrentArray(sudokuStringTo2DArray(userSudokuBoard.getGridCurrent()));
        sudokuBoardEntityDTO.setGridPlayable(userSudokuBoard.getSudokuBoard().getGridPlayable());
        sudokuBoardEntityDTO.setGridPlayableArray(sudokuStringTo2DArray(userSudokuBoard.getSudokuBoard().getGridPlayable()));
        sudokuBoardEntityDTO.setDifficultyEntity(userSudokuBoard.getSudokuBoard().getDifficultyEntity());
        sudokuBoardEntityDTO.setStatus(userSudokuBoard.getStatus());
        sudokuBoardEntityDTO.setWrongValidationsCount(userSudokuBoard.getWrongValidationsCount());

        return sudokuBoardEntityDTO;
    }

    public boolean validateMove(SudokuBoardEntityDTO sudokuBoardEntityDTO, long userId) {
        int value = sudokuBoardEntityDTO.getMove().getValue();
        int x = sudokuBoardEntityDTO.getMove().getPosition().getX();
        int y = sudokuBoardEntityDTO.getMove().getPosition().getY();

        Optional<UserSudokuBoard> userSudokuBoardOptional =userSudokuBoardRepository.findByUser_IdAndSudokuBoard_Id(userId, sudokuBoardEntityDTO.getId());
        if (userSudokuBoardOptional.isPresent()) {
            UserSudokuBoard userSudokuBoard = userSudokuBoardOptional.get();
            SudokuBoard board = userSudokuBoard.getSudokuBoard();
            if (value == board.getGridSolved().charAt(x * 9 + y)) {
                String gc = userSudokuBoard.getGridCurrent();
                userSudokuBoard.setGridCurrent(gc.substring(0, x * 9 + y) + value + gc.substring(x * 9 + y));
                userSudokuBoardRepository.save(userSudokuBoard);
                return true;
            } else {
                userSudokuBoard.setWrongValidationsCount(userSudokuBoard.getWrongValidationsCount() + 1);
            }
            userSudokuBoardRepository.save(userSudokuBoard);
        }
        return false;
    }
}
