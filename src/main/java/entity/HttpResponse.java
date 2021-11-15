package entity;

import lombok.Data;

@Data
public class HttpResponse {
    private HttpStatus status;
    private String body;
}
