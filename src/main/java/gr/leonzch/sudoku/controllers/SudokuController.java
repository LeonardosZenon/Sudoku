package gr.leonzch.sudoku.controllers;

import gr.leonzch.sudoku.models.entities.sudoku_board.SudokuBoardEntityDTO;
import gr.leonzch.sudoku.propeties.ApplicationProperties;
import gr.leonzch.sudoku.services.SudokuService;
import gr.leonzch.sudoku.services.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping(value = "${api.base.sudoku}")
public class SudokuController {

    @Autowired
    SudokuService sudokuService;

    @Autowired
    UserService userService;

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
    public SudokuBoardEntityDTO generateSudoku(@PathVariable long difficulty, @AuthenticationPrincipal OidcUser oidcUser) {
        long userId = userService.findUserIdFromOidc(oidcUser);

        sudokuService.setInProgressToLost(userId);

        return sudokuService.generateSudoku(difficulty, userId);
    }

    @GetMapping(value = "${api.base.sudoku.isinprogress}")
    public boolean isInProgres(@AuthenticationPrincipal OidcUser oidcUser) {
        long userId = userService.findUserIdFromOidc(oidcUser);

        return sudokuService.isInProgress(userId);
    }

    @GetMapping(value = "${api.base.sudoku.continue}")
    public SudokuBoardEntityDTO continueSudoku(@AuthenticationPrincipal OidcUser oidcUser) {
        long userId = userService.findUserIdFromOidc(oidcUser);

        return sudokuService.continueSudoku(userId);
    }

    @PostMapping(value = "${api.base.sudoku.validate}")
    public boolean validate(@RequestBody SudokuBoardEntityDTO sudokuBoardEntityDTO, @AuthenticationPrincipal OidcUser oidcUser) {
        long userId = userService.findUserIdFromOidc(oidcUser);

        return sudokuService.validate(sudokuBoardEntityDTO, userId);
    }

    @GetMapping(value = "${api.base.sudoku.solvedcount}/{difficulty}")
    public long getSolvedCountByDifficulty(@PathVariable int difficulty) {
        return sudokuService.getSolvedCountByDifficulty(difficulty);
    }

    @GetMapping(value = "${api.base.sudoku.besttime}/{difficulty}")
    public String getBestTime(@PathVariable int difficulty) {
        return sudokuService.getBestTime(difficulty);
    }

    @GetMapping(value = "${api.base.sudoku.averagetime}/{difficulty}")
    public String getAverageTime(@PathVariable int difficulty) {
        return sudokuService.getAverageTime(difficulty);
    }

    @GetMapping(value = "${api.base.sudoku.rankings}/{difficulty}")
    public long getRankings(@PathVariable int difficulty) {
        return sudokuService.getRankings(difficulty);
    }

    @GetMapping(value = "${api.base.sudoku.mystats}")
    public Map<String, String> getMyStats() {
        return sudokuService.getMyStats();
    }

    @GetMapping(value = "${api.base.sudoku.help}")
    public String getHelp() {
        return sudokuService.getHelp();
    }
}
