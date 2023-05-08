package gamein2.team.kernel.entity;

import gamein2.team.kernel.dto.result.UserDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;


@Entity
@Table(name = "users")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private Long id;

    @ManyToOne
    private Team team;

    @Column(name = "phone", unique = true, nullable = false)
    private String phone;

    @Column(name = "email", unique = true, nullable = false)
    private String email;

    @Column(name = "name")
    private String name;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "last_login")
    private Date lastLogin;

    @Column(name = "is_admin", columnDefinition = "boolean default false")
    private boolean isAdmin = false;

    @Column(name = "persian_name")
    private String persianName;

    @Column(name = "english_name")
    private String englishName;

    @Column(name = "password_code", unique = true)
    private String passwordCode;



    public UserDTO toDTO() {
        return new UserDTO(id, name);
    }
}