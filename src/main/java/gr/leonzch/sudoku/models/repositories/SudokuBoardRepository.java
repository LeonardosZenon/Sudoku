package gr.leonzch.sudoku.models.repositories;

import gr.leonzch.sudoku.models.entities.constant_entities.sudoku_difficulty.SudokuDifficulty;
import gr.leonzch.sudoku.models.entities.sudoku_board.SudokuBoard;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SudokuBoardRepository extends JpaRepository<SudokuBoard, Long> {

    Optional<SudokuBoard> findTopByDifficultyEntityAndIdNotIn(SudokuDifficulty difficulty, List<Long> played);

    Optional<SudokuBoard> findByGridPlayable(String s);
}
