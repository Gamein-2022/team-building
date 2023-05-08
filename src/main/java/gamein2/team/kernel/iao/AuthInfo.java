package gamein2.team.kernel.iao;

import gamein2.team.kernel.entity.Team;
import gamein2.team.kernel.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;


@AllArgsConstructor
@Getter
public class AuthInfo {
    private User user;
    private Team team;
}