package gr.leonzch.sudoku.services;

import gr.leonzch.sudoku.models.entities.user.User;
import gr.leonzch.sudoku.models.repositories.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public User findOrCreateUser(String username, String email) {
        return userRepository.findByUsernameOrEmail(username, email).orElseGet(() -> {
            User newUser = new User(username, email);
            return userRepository.save(newUser);
        });
    }
}
