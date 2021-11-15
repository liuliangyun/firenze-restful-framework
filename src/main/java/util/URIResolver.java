package util;

import io.netty.handler.codec.http.QueryStringDecoder;

import java.util.List;
import java.util.Map;

public class URIResolver {
    public static Map<String, List<String>> getURIParams(String uri) {
        return new QueryStringDecoder(uri).parameters();
    }

    public static String getURIPath(String uri) {
        return uri.split("\\?")[0];
    }
}
