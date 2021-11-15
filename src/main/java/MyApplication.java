import dispatcher.DispatcherConfig;
import server.RestServer;

public class MyApplication {

    public static void main(String[] args) {
        new RestServer(8080, new DispatcherConfig("resource")).run();
    }
}
