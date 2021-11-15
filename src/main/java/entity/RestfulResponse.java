package entity;

import io.netty.handler.codec.http.HttpResponseStatus;
import lombok.Data;

@Data
public class RestfulResponse {
    private HttpResponseStatus status;
    private Object body;
}
