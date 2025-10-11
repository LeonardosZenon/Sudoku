package gr.leonzch.sudoku.models.cmdlinerunners;

import gr.leonzch.sudoku.enums.SudokuDifficultyConstants;
import gr.leonzch.sudoku.models.entities.sudoku_difficulty.SudokuDifficulty;
import gr.leonzch.sudoku.models.repositories.SudokuDifficultyRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SudokuDifficultyRunner implements CommandLineRunner {
    private final SudokuDifficultyRepository sudokuDifficultyRepository;

    public SudokuDifficultyRunner(SudokuDifficultyRepository sudokuDifficultyRepository) {
        this.sudokuDifficultyRepository = sudokuDifficultyRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        for (SudokuDifficultyConstants level : SudokuDifficultyConstants.values()) {
            sudokuDifficultyRepository.findById(level.getId()).map(entity -> {
                if (!entity.getDifficulty().equals(level.getLabel())) {
                    entity.setDifficulty(level.getLabel());
                    return sudokuDifficultyRepository.save(entity);
                }
                return entity;
            }).orElseGet(() -> {
                return sudokuDifficultyRepository.save(new SudokuDifficulty(level));
            });
        }

        if (SudokuDifficultyConstants.length() != sudokuDifficultyRepository.count()) {
            List<SudokuDifficulty> entities = sudokuDifficultyRepository.findAll();

            for (SudokuDifficulty entity : entities) {
                if(!SudokuDifficultyConstants.exists(entity.getId())) {
                    sudokuDifficultyRepository.delete(entity);
                }
            }
        }

    }
}
