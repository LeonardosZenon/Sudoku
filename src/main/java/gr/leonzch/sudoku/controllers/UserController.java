package gr.leonzch.sudoku.controllers;

import gr.leonzch.sudoku.propeties.ApplicationProperties;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping(value = "${api.base.user}")
public class UserController {

    @Autowired
    ApplicationProperties applicationProperties;

    @GetMapping(value = "/")
    public String apiInfo(HttpServletRequest request) {
        StringBuilder sb = new StringBuilder();
        sb.append(applicationProperties.getApiBaseUser()).append(" API provides tha following endpoints:\n");
        sb.append("* ").append(applicationProperties.getApiBaseUserSignup()).append(" | GET\n");
        sb.append("* ").append(applicationProperties.getApiBaseUserLogin()).append(" | GET\n");
        sb.append("* ").append(applicationProperties.getApiBaseUserMe()).append(" | GET\n");

        return sb.toString();
    }

    @GetMapping("${api.base.user.login}")
    public String loginPage() {
        return "redirect:/oauth2/authorization/keycloak";
    }

    @GetMapping("${api.base.user.signup}")
    public String signupRedirect() {
        return "redirect:" + System.getenv("KEYCLOAK_BASE_URL")
                + "/realms/sudoku/protocol/openid-connect/registrations"
                + "?client_id=sudoku-api&response_type=code&scope=openid%20profile%20email"
                + "&redirect_uri=http://localhost:8081/api/sudoku";
    }

    @GetMapping("${api.base.user.me}")
    public Map<String, Object> getCurrentUser(@AuthenticationPrincipal Jwt jwt) {
        return Map.of(
                "username", jwt.getClaim("preferred_username"),
                "email", jwt.getClaim("email")
        );
    }
}
