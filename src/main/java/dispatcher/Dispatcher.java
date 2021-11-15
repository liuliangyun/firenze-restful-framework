package dispatcher;

import entity.RestfulResponse;
import io.netty.handler.codec.http.FullHttpRequest;

public interface Dispatcher {

    void init ();

    RestfulResponse dispatch(FullHttpRequest httpRequest);

}
