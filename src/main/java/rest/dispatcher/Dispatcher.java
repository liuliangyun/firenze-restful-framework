package rest.dispatcher;

import io.netty.handler.codec.http.FullHttpRequest;
import rest.entity.RestfulResponse;

public interface Dispatcher {

    void init ();

    RestfulResponse dispatch(FullHttpRequest httpRequest);

}
