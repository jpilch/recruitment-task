package recruitment_task.dto;

public class ResourceNotFoundException extends RuntimeException {
    
    public ResourceNotFoundException(String resourceName) {
        super(resourceName + " not found.");
    }
}
