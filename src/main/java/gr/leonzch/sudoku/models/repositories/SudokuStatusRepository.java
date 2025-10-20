package gr.leonzch.sudoku.models.repositories;

import gr.leonzch.sudoku.models.entities.constant_entities.sudoku_status.SudokuStatus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SudokuStatusRepository extends JpaRepository<SudokuStatus, Long> {

}
