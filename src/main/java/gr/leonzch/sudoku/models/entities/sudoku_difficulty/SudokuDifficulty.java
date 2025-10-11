package gr.leonzch.sudoku.models.entities.sudoku_difficulty;


import gr.leonzch.sudoku.enums.SudokuDifficultyConstants;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "sudoku_difficulty")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SudokuDifficulty {
    @Id
    @Column(nullable = false, unique = true)
    private Long id;

    private String difficulty;

    public SudokuDifficulty(SudokuDifficultyConstants level) {
        this.id = level.getId();
        this.difficulty = level.getLabel();
    }
}
