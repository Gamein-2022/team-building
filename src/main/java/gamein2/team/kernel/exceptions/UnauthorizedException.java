package gamein2.team.kernel.exceptions;

public class UnauthorizedException extends Exception{
    public UnauthorizedException() {
    }

    public UnauthorizedException(String message) {
        super(message);
    }
}
