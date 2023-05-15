package gamein2.team.kernel.dto.result;

import gamein2.team.kernel.enums.Gender;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TeamUserDTO extends UserDTO{
    private String email;
    public TeamUserDTO(Long id, String username, String persianName, String persianSurname, Gender gender,String email,
                       String school,String major) {
        super(id, username, persianName, persianSurname, gender,school,major);
        this.email = email;
    }
}
