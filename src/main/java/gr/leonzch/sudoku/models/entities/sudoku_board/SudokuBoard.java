package gr.leonzch.sudoku.models.entities.sudoku_board;

import gr.leonzch.sudoku.models.entities.sudoku_difficulty.SudokuDifficulty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "sudoku_board")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SudokuBoard {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "varchar(81)", nullable = false)
    private String gridSolved;

    @Column(columnDefinition = "varchar(81)", nullable = false)
    private String gridPlayable;

    @ManyToOne
    @JoinColumn(name = "difficulty_id",
            referencedColumnName = "id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_sudoku_board_difficulty"),
            columnDefinition = "bigint default 1")
    private SudokuDifficulty difficultyEntity;

//    @OneToMany(mappedBy = "sudokuBoard", cascade = CascadeType.ALL, orphanRemoval = true)
//    private List<UserSudokuBoard> userSudokuBoards = new ArrayList<>();

    public void setDifficulty(SudokuDifficulty sudokuDifficultyEntity) {
        this.difficultyEntity = sudokuDifficultyEntity;
    }
}
