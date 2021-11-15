package entity;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Map;

@Data
@AllArgsConstructor
public class HttpRequest {
    private HttpMethod method;
    private String path;
   // 包括pathParam和queryParam
    private Map<String, String> params;
    private String body;
}
