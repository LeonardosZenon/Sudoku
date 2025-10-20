package gr.leonzch.sudoku.models.repositories;

import gr.leonzch.sudoku.enums.SudokuStatusConstants;
import gr.leonzch.sudoku.models.entities.user_sudoku_board.UserSudokuBoard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserSudokuBoardRepository extends JpaRepository<UserSudokuBoard, Long> {

    @Query("SELECT usb.sudokuBoard.id FROM UserSudokuBoard usb WHERE usb.user.id = :userId")
    List<Long> findSudokuBoardIdByUser_Id(Long userId);

    Optional<UserSudokuBoard> findTopByUser_IdAndStatus_Id(Long user_id, Long status_id);

    Optional<UserSudokuBoard> findByUser_IdAndSudokuBoard_Id(Long user_id, Long sudokuBoard_id);
}
