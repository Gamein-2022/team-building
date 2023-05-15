package gamein2.team.kernel.dto.result;

import gamein2.team.kernel.enums.Gender;
import lombok.AllArgsConstructor;
import lombok.Getter;


@Getter
@AllArgsConstructor
public class UserDTO {
    private Long id;
    private String username;
    private String persianName;
    private String persianSurname;
    private Gender gender;
    private String school;
    private String major;
}
