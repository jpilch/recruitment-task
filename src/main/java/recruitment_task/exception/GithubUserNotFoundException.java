package recruitment_task.exception;

public class GithubUserNotFoundException extends RuntimeException {
    
    public GithubUserNotFoundException() {
        super("User does not exist");
    }
}
