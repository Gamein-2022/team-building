package gamein2.team.kernel.repos;

import gamein2.team.kernel.entity.TeamOffer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TeamOfferRepository extends JpaRepository<TeamOffer, Long> {
    List<TeamOffer> findAllByUser_IdAndDeclinedIsFalse(Long userId);
    List<TeamOffer> findAllByTeam_Id(Long teamId);
    List<TeamOffer> findAllByTeamIdAndDeclinedIsFalse(Long teamId);
    Optional<TeamOffer> findByTeam_IdAndUser_Id(Long teamId, Long userId);
    List<TeamOffer> findAllByTeamIdAndUserIdAndDeclinedIsFalse(Long teamId,Long userId);
    void deleteAllByTeam_Id(Long teamId);
}
