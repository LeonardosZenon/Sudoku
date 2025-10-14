package gr.leonzch.sudoku.controllers;

import gr.leonzch.sudoku.models.entities.sudoku_board.SudokuBoardEntityDTO;
import gr.leonzch.sudoku.propeties.ApplicationProperties;
import gr.leonzch.sudoku.services.SudokuService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "${api.base.sudoku}")
public class SudokuController {

    @Autowired
    SudokuService sudokuService;

    @Autowired
    ApplicationProperties applicationProperties;

    @GetMapping()
    public String apiBase() {
        return applicationProperties.getApiBaseSudoku() + " api";
    }

    @GetMapping(value = "/")
    public String apiInfo(HttpServletRequest request) {
        StringBuilder sb = new StringBuilder();
        sb.append(applicationProperties.getApiBaseSudoku()).append(" API provides tha following endpoints:\n");
        sb.append("* ").append(applicationProperties.getApiBaseSudokuGenerate()).append("/{difficulty} | GET | difficulty: 1 to 4 | ").append(request.getContextPath()).append("\n");

        return sb.toString();
    }

    @GetMapping(value = "${api.base.sudoku.generate}/{difficulty}")
    public SudokuBoardEntityDTO generateSudoku(@PathVariable int difficulty) {
        return sudokuService.generateSudoku(difficulty);
    }
}
