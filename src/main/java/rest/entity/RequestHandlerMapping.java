package rest.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class RequestHandlerMapping {
    private List<RequestHandler> requestHandlers;

    public RequestHandler mapping (RestfulRequest request) {
        return requestHandlers.stream()
                .filter(h -> h.isMatch(request))
                .findFirst()
                .orElse(null);
    }
}
