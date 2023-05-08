package gamein2.team.kernel.dto.result;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;


@AllArgsConstructor
@Getter
public class ErrorResultDTO extends BaseResult {
    private String message;
    private HttpStatus status;
}
