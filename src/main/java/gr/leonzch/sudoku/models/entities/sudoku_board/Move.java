package gr.leonzch.sudoku.models.entities.sudoku_board;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Move {

    private int value;
    private Position position;
}
