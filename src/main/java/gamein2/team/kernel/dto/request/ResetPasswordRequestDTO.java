package gamein2.team.kernel.dto.request;

import lombok.Getter;


@Getter
public class ResetPasswordRequestDTO {
    private String code;
    private String password;
}
