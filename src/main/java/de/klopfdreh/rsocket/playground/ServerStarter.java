package de.klopfdreh.rsocket.playground;

import java.io.InputStreamReader;

public class ServerStarter {
    public static void main(String[] args) {
	Server server = new Server();
	try {
	    InputStreamReader reader = new InputStreamReader(System.in);
	    reader.read();
	} catch (Exception e) {
	}
	server.dispose();
    }
}
