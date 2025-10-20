package gr.leonzch.sudoku.models.entities.user_sudoku_board;

import jakarta.persistence.Embeddable;
import lombok.*;

@Embeddable
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class UserSudokuBoardId {

    private Long userId;
    private Long sudokuBoardId;
}