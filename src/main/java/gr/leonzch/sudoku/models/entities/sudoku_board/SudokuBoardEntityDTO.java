package gr.leonzch.sudoku.models.entities.sudoku_board;

import gr.leonzch.sudoku.models.entities.constant_entities.sudoku_difficulty.SudokuDifficulty;
import gr.leonzch.sudoku.models.entities.constant_entities.sudoku_status.SudokuStatus;
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
    private String gridCurrent;
    private int[][] gridCurrentArray;
    private String gridPlayable;
    private int[][] gridPlayableArray;
    private SudokuDifficulty difficultyEntity;
    private SudokuStatus status;
    private int wrongValidationsCount;
    private Move move;
}
