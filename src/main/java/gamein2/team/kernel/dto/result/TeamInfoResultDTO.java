package gamein2.team.kernel.dto.result;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;


@AllArgsConstructor
@Getter
public class TeamInfoResultDTO {
    private String name;
    private List<TeamUserDTO> users;
    private Boolean isOwner;
}
