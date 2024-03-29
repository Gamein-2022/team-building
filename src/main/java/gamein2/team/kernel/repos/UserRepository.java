package gamein2.team.kernel.repos;

import gamein2.team.kernel.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmailOrPhone(String email, String phone);

    List<User> findAllByIdNot(Long userId);
    List<User> findAllByIdNotAndTeamIsNull(Long userId);
    Optional<User> findByPasswordCode(String code);

    /*@Query(value = "select u from User as u where u.name is not null and u.team is null ")
    List<User> getALoneUsers();

    @Query(value = "select u from User  as u where u.team is null ")
    List<User> getBad();*/
}