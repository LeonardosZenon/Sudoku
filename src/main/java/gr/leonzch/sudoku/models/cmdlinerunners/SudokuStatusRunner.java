package gr.leonzch.sudoku.models.cmdlinerunners;

import gr.leonzch.sudoku.enums.SudokuStatusConstants;
import gr.leonzch.sudoku.models.entities.constant_entities.sudoku_status.SudokuStatus;
import gr.leonzch.sudoku.models.repositories.SudokuStatusRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SudokuStatusRunner implements CommandLineRunner {
    private final SudokuStatusRepository sudokuStatusRepository;

    public SudokuStatusRunner(SudokuStatusRepository sudokuStatusRepository) {
        this.sudokuStatusRepository = sudokuStatusRepository;
    }

    @Override
    public void run(String... args) {
        for (SudokuStatusConstants staus : SudokuStatusConstants.values()) {
            sudokuStatusRepository.findById(staus.getId()).map(entity -> {
                if (!entity.getLabel().equals(staus.getLabel())) {
                    entity.setLabel(staus.getLabel());
                    return sudokuStatusRepository.save(entity);
                }
                return entity;
            }).orElseGet(() -> sudokuStatusRepository.save(new SudokuStatus(staus)));
        }

        if (SudokuStatusConstants.length() != sudokuStatusRepository.count()) {
            List<SudokuStatus> entities = sudokuStatusRepository.findAll();

            for (SudokuStatus entity : entities) {
                if(!SudokuStatusConstants.exists(entity.getId())) {
                    sudokuStatusRepository.delete(entity);
                }
            }
        }
    }
}
