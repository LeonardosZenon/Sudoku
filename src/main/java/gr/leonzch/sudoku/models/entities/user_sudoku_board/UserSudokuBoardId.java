package gr.leonzch.sudoku.models.entities.user_sudoku_board;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Embeddable
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class UserSudokuBoardId {

    private Long userId;
    private Long sudokuBoardId;
}