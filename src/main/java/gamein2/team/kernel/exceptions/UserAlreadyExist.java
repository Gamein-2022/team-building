package gamein2.team.kernel.exceptions;

public class UserAlreadyExist extends Exception{
    public UserAlreadyExist() {
    }

    public UserAlreadyExist(String message) {
        super(message);
    }
}
