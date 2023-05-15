package gamein2.team.kernel.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;


@Entity
@Table(name = "teams")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Team {
    @Id
    @Column(name = "id", unique = true, nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(fetch = FetchType.EAGER)
    private List<User> users;

    @OneToOne(optional = false)
    private User owner;

    @Column(name = "name", nullable = false, unique = true)
    private String name;
}