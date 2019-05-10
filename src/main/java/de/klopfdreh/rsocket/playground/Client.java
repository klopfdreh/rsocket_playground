package de.klopfdreh.rsocket.playground;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.rsocket.RSocket;
import io.rsocket.RSocketFactory;
import io.rsocket.frame.decoder.PayloadDecoder;
import io.rsocket.transport.netty.client.TcpClientTransport;
import reactor.core.publisher.Flux;

public class Client {

    private static final Logger LOGGER = LoggerFactory.getLogger(Client.class);

    private RSocket socket;

    private final int TCP_PORT = 7000;

    public Client() {
	this.socket = RSocketFactory.connect().frameDecoder(PayloadDecoder.ZERO_COPY)
		.transport(TcpClientTransport.create("localhost", TCP_PORT)).start()
		.doOnNext(x -> LOGGER.info("Client started.")).block();
    }

    public void sendPersons(List<Person> persons) {
	ClientController clientController = new ClientController(persons);
	this.socket.requestChannel(Flux.from(clientController)).doOnNext(clientController::processServerPayload)
		.blockLast();
    }
    
    public void dispose() {
	LOGGER.info("Client stopped.");
	this.socket.dispose();
    }
}
