package gamein2.team.kernel.dto.result;

import lombok.AllArgsConstructor;
import lombok.Getter;


@AllArgsConstructor
@Getter
public class ErrorResultDTO extends BaseResult {
    private String message;
}
