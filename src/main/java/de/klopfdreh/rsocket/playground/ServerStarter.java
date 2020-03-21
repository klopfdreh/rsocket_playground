package de.klopfdreh.rsocket.playground;

import lombok.extern.slf4j.Slf4j;

import java.io.InputStreamReader;

@Slf4j
public class ServerStarter {
    public static void main(String[] args) {
        Server server = new Server();
        try {
            InputStreamReader reader = new InputStreamReader(System.in);
            reader.read();
        } catch (Exception e) {
            log.error("Error while reading the input stream.", e);
        }
        server.dispose();
    }
}
