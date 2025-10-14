package gr.leonzch.sudoku.controllers;

import gr.leonzch.sudoku.propeties.ApplicationProperties;
import gr.leonzch.sudoku.services.UserService;
import gr.leonzch.sudoku.utils.logging.Logging;
import gr.leonzch.sudoku.utils.logging.LoggingTypes;
import jakarta.servlet.http.HttpServletRequest;
import net.minidev.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping(value = "${api.base.user}")
public class UserController {

    @Autowired
    ApplicationProperties applicationProperties;

    @Autowired
    UserService userService;

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
                + "&redirect_uri=http://localhost:8081/api/user/me";
    }

//    @GetMapping("${api.base.user.me}")
//    public String getCurrentUser(@AuthenticationPrincipal OAuth2AuthenticationToken token) {
//        if (token == null) {
//            throw new RuntimeException("User is not authenticated");
//        }
//
//        Map<String, Object> attributes = token.getPrincipal().getAttributes();
//        String username = (String) attributes.get("preferred_username");
//        String email = (String) attributes.get("email");
//
//        var user = userService.findOrCreateUser(username, email);
//
//        return "redirect:" + applicationProperties.getApiBaseSudoku() + "/";
//    }

    @GetMapping("${api.base.user.oauth2debug}")
    @ResponseBody
    public Object debugAuth(Authentication authentication, HttpServletRequest request) {
        return Map.of(
                "authentication", authentication,
                "principal", authentication != null ? authentication.getPrincipal() : null,
                "sessionId", request.getSession(false) != null ? request.getSession(false).getId() : null
        );
    }

    @GetMapping("${api.base.user.me}")
    public String getCurrentUser(@AuthenticationPrincipal OidcUser oidcUser) {

        if (oidcUser != null) {
            String username = oidcUser.getPreferredUsername();
            String email = (String) oidcUser.getAttributes().get("email");
            userService.findOrCreateUser(username, email);

            Logging.log(LoggingTypes.INFO, "Saved user: " + username + " / " + email);
        }

        return "redirect:" + applicationProperties.getApiBaseSudoku() + "/";
    }
}
