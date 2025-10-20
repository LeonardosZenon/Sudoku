package gr.leonzch.sudoku.models.entities.user;

import gr.leonzch.sudoku.models.entities.user_sudoku_board.UserSudokuBoard;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "username", columnDefinition = "varchar(255)")
    private String username;

    @Column(name = "email", columnDefinition = "varchar(255)")
    private String email;

    public User(String username, String email) {
        this.username = username;
        this.email = email;
    }

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserSudokuBoard> userSudokuBoards = new ArrayList<>();
}
