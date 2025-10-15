package gr.leonzch.sudoku.propeties;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Getter
public class ApplicationProperties {

    @Value("${server.port}")
    private int serverPort;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Value("${api.base.sudoku}")
    private String apiBaseSudoku;

    @Value("${api.base.sudoku.generate=/generate")
    private String apiBaseSudokuGenerate;

    @Value("${api.base.user}")
    private String apiBaseUser;

    @Value("${api.base.user.login}")
    private String apiBaseUserLogin;

    @Value("${api.base.user.signup}")
    private String apiBaseUserSignup;

    @Value("${api.base.user.me}")
    private String apiBaseUserMe;

}
