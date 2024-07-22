package recruitment_task.dto;

import lombok.Data;

@Data
public class ErrorResponse {
    private final Integer status;
    private final String message;
}
