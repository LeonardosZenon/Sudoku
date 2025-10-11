package gr.leonzch.sudoku.models.entities.user_sudoku_board;

import gr.leonzch.sudoku.models.entities.sudoku_board.SudokuBoard;
import gr.leonzch.sudoku.models.entities.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "user_sudoku_board")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserSudokuBoard {

    @EmbeddedId
    private UserSudokuBoardId id;

    @ManyToOne
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @MapsId("sudokuBoardId")
    @JoinColumn(name = "sudoku_board_id")
    private SudokuBoard sudokuBoard;

    private boolean solved;
}
