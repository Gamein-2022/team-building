package gamein2.team.kernel.dto.result;

import gamein2.team.kernel.enums.Gender;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TeamUserDTO extends UserDTO{
    private String email;
    public TeamUserDTO(Long id, String username, String persianName, String persianSurname, Gender gender,String email) {
        super(id, username, persianName, persianSurname, gender);
        this.email = email;
    }
}
