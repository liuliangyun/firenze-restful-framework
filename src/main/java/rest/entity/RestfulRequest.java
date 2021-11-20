package rest.entity;

import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.util.CharsetUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import rest.util.PathUtils;

import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
public class RestfulRequest {
    private HttpMethod method;
    private String path;
    private Map<String, List<String>> params;
    private String body;

    public static RestfulRequest of(FullHttpRequest httpRequest) {
        HttpMethod method = httpRequest.method();
        String uri = httpRequest.uri();
        String path = PathUtils.getURIPath(uri);
        Map<String, List<String>> params = PathUtils.getURIParams(uri);
        String body = httpRequest.content().toString(CharsetUtil.UTF_8);
        return new RestfulRequest(method, path, params, body);
    }

}
