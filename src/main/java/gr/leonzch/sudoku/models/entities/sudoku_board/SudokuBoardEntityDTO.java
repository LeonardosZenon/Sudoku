package gr.leonzch.sudoku.models.entities.sudoku_board;

import gr.leonzch.sudoku.models.entities.sudoku_difficulty.SudokuDifficulty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SudokuBoardEntityDTO {

    private Long id;
    private String gridSolved;
    private int[][] gridSolvedArray;
    private String gridPlayable;
    private int[][] gridPlayableArray;
    private SudokuDifficulty difficultyEntity;
}
