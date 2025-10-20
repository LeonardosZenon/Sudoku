package gr.leonzch.sudoku.controllers;

import gr.leonzch.sudoku.models.entities.constant_entities.sudoku_difficulty.SudokuDifficulty;
import gr.leonzch.sudoku.models.repositories.SudokuDifficultyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "${api.base.sudokudifficulty}")
public class SudokuDifficultyController {
    @Autowired
    SudokuDifficultyRepository sudokuDifficultyRepository;

    @GetMapping(value = "${api.base.sudokudifficulty.getall}")
    public List<SudokuDifficulty> getAll() {
        return sudokuDifficultyRepository.findAll();
    }
}
