package rest.util;

import io.netty.handler.codec.http.QueryStringDecoder;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PathUtils {
    public static Map<String, List<String>> getURIParams(String uri) {
        return new QueryStringDecoder(uri).parameters();
    }

    public static String getURIPath(String uri) {
        return uri.split("\\?")[0];
    }

    public static List<String> getPathToken(String path) {
        return Arrays.stream(path.split("/")).filter(token -> !token.isEmpty()).collect(Collectors.toList());
    }
}
