package gr.leonzch.sudoku.models.entities.constant_entities.sudoku_status;

import gr.leonzch.sudoku.enums.SudokuStatusConstants;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "sudoku_status")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SudokuStatus {
    @Id
    @Column(nullable = false, unique = true)
    private Long id;

    private String label;

    public SudokuStatus(SudokuStatusConstants constant) {
        this.id = constant.getId();
        this.label = constant.getLabel();
    }
}
