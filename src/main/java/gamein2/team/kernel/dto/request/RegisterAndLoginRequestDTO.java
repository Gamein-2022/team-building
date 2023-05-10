package gamein2.team.kernel.dto.request;

import lombok.Getter;


@Getter
public class RegisterAndLoginRequestDTO {
    private String phone;
    private String email;
    private String password;
    private String persianName;
    private String persianLastName;
}
