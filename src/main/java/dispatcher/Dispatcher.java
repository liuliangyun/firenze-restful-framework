package dispatcher;

import entity.HttpRequest;
import entity.HttpResponse;

public interface Dispatcher {

    void init ();

    HttpResponse dispatch(HttpRequest restfulRequest);

}
