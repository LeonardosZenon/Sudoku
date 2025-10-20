package gr.leonzch.sudoku.services;

import gr.leonzch.sudoku.models.entities.user.User;
import gr.leonzch.sudoku.models.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private final UserRepository userRepository;

    @Autowired(required = false)
    private final OidcUser mockOidcUser;

    public UserService(UserRepository userRepository, @Autowired(required = false) OidcUser mockOidcUser) {
        this.userRepository = userRepository;
        this.mockOidcUser = mockOidcUser;
    }

    @Transactional
    public User findOrCreateUser(String username, String email) {
        return userRepository.findByUsernameOrEmail(username, email).orElseGet(() -> {
            User newUser = new User(username, email);
            return userRepository.save(newUser);
        });
    }

    public long findUserIdFromOidc(OidcUser oidcUser) {
        if (oidcUser == null) oidcUser = mockOidcUser;

        if (oidcUser != null) {
            String username = oidcUser.getPreferredUsername();
            String email = (String) oidcUser.getAttributes().get("email");
            return findOrCreateUser(username, email).getId();
        } else {
            throw new IllegalArgumentException("OIDC User is null");
        }
    }
}
