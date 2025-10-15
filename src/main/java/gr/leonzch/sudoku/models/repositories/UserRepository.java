package gr.leonzch.sudoku.models.repositories;

import gr.leonzch.sudoku.models.entities.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    User findByUsername(String username);
    User findByEmail(String email);

    Optional<User> findByUsernameOrEmail(String username, String email);
}
