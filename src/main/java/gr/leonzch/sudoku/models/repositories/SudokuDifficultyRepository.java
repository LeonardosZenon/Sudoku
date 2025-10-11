package gr.leonzch.sudoku.models.repositories;

import gr.leonzch.sudoku.models.entities.sudoku_difficulty.SudokuDifficulty;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SudokuDifficultyRepository extends JpaRepository<SudokuDifficulty, Long> {

}
