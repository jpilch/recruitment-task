package recruitment_task.exception;

public class GithubUserNotFoundException extends ResourceNotFoundException {
    
    public GithubUserNotFoundException() {
        super("User does not exist");
    }
}
