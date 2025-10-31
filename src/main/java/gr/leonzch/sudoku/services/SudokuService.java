package gr.leonzch.sudoku.services;

import gr.leonzch.sudoku.enums.SudokuStatusConstants;
import gr.leonzch.sudoku.models.entities.constant_entities.sudoku_status.SudokuStatus;
import gr.leonzch.sudoku.models.entities.sudoku_board.SudokuBoard;
import gr.leonzch.sudoku.models.entities.sudoku_board.SudokuBoardEntityDTO;
import gr.leonzch.sudoku.models.entities.user.User;
import gr.leonzch.sudoku.models.entities.user_sudoku_board.UserSudokuBoard;
import gr.leonzch.sudoku.models.repositories.SudokuBoardRepository;
import gr.leonzch.sudoku.models.repositories.UserSudokuBoardRepository;
import gr.leonzch.sudoku.utils.SudokuUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service
public class SudokuService {

    private final SudokuBoardRepository sudokuBoardRepository;
    private final UserSudokuBoardRepository userSudokuBoardRepository;

    @Autowired
    private SudokuUtils sudokuUtils;

    public SudokuService(SudokuBoardRepository sudokuBoardRepository, UserSudokuBoardRepository userSudokuBoardRepository) {
        this.sudokuBoardRepository = sudokuBoardRepository;
        this.userSudokuBoardRepository = userSudokuBoardRepository;
    }

    public SudokuBoardEntityDTO generateSudoku(long difficulty, long userId) {
        Optional<SudokuBoard> sudokuBoard = sudokuUtils.generatedBoardByOther(difficulty, userId);

        if (sudokuBoard.isEmpty()) {
            sudokuBoard = Optional.ofNullable(sudokuUtils.generateNewBoard(difficulty));
        }

        sudokuUtils.createUserSudokuBoardEntity(sudokuBoard.get(), userId);

        return sudokuUtils.createDTOForNewBoard(sudokuBoard.get());
    }

    public boolean isInProgress(long userId) {
        return sudokuUtils.findOptionalUserSudokuBoardInProgress(userId).isPresent();
    }

    public SudokuBoardEntityDTO continueSudoku(long userId) {
        UserSudokuBoard userSudokuBoard = sudokuUtils.findUserSudokuBoardInProgress(userId);
        return sudokuUtils.createDTOToContinueBoard(userSudokuBoard);
    }

    public SudokuBoardEntityDTO validate(SudokuBoardEntityDTO sudokuBoardEntityDTO, long userId) {
        return sudokuUtils.createDTOToContinueBoard(sudokuUtils.validateMove(sudokuBoardEntityDTO, userId));
    }

    public long getSolvedCountByDifficulty(int difficulty) {
        //TODO: Implement method
        return 0;
    }

    public String getBestTime(int difficulty) {
        //TODO: Implement method
        return "00:00:00";
    }

    public String getAverageTime(int difficulty) {
        //TODO: Implement method
        return "00:00:00";
    }

    public long getRankings(int difficulty) {
        //TODO: Implement method
        return 0;
    }

    public Map<String, String> getMyStats() {
        //TODO: Implement method
        return Map.of();
    }

    public String getHelp() {
        //TODO: Implement method
        return "";
    }

    public void setInProgressToLost(long userId) {
        Optional<UserSudokuBoard> userSudokuBoardOptional = userSudokuBoardRepository.findTopByUser_IdAndStatus_Id(userId, SudokuStatusConstants.IN_PROGRESS.getId());

        if (userSudokuBoardOptional.isPresent()) {
            UserSudokuBoard userSudokuBoard = userSudokuBoardOptional.get();
            userSudokuBoard.setStatus(new SudokuStatus(SudokuStatusConstants.LOST));
            userSudokuBoardRepository.save(userSudokuBoard);
        }
    }
}
