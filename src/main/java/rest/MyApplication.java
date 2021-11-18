package rest;

import rest.dispatcher.DispatcherConfig;
import rest.server.RestServer;

public class MyApplication {

    public static void main(String[] args) {
        new RestServer(8080, new DispatcherConfig("rest.example")).run();
    }
}
