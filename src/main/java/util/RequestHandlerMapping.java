package util;

import entity.HttpRequest;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class RequestHandlerMapping {
    private List<RequestHandler> handlers;

    public RequestHandler mapping (HttpRequest request) {
        return handlers.stream()
                .filter(h -> h.isMatch(request))
                .findFirst()
                .orElse(null);
    }
}
