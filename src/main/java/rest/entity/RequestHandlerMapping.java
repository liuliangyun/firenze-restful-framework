package rest.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class RequestHandlerMapping {
    private List<RequestHandler> handlers;

    public RequestHandler mapping (RestfulRequest request) {
        return handlers.stream()
                .filter(h -> h.isMatch(request))
                .findFirst()
                .orElse(null);
    }
}
