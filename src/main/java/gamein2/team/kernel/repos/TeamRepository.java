package gamein2.team.kernel.repos;

import gamein2.team.kernel.entity.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;


public interface TeamRepository extends JpaRepository<Team, Long> {
    @Query("SELECT COUNT  (*) FROM Team ")
    Integer getCount();

    @Modifying
    @Query(value = "update teams set region = 0",nativeQuery = true)
    void resetTeamsRegion();

    Optional<Team> findTeamByName(String name);

    @Query(value = "select t from Team as t where t.users.size = 1")
    List<Team> getALoneTeams();


}