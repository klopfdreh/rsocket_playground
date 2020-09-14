package de.klopfdreh.rsocket.playground;

import io.rsocket.Payload;
import io.rsocket.RSocket;
import io.rsocket.core.RSocketConnector;
import io.rsocket.frame.decoder.PayloadDecoder;
import io.rsocket.transport.netty.client.TcpClientTransport;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

import java.util.Arrays;
import java.util.List;

@Slf4j
public class Client {

    private RSocket socket;

    private final int TCP_PORT = 7000;

    private ClientController clientController;

    public Client() {
        this.clientController = new ClientController();
        this.socket = RSocketConnector
                .create()
                .payloadDecoder(PayloadDecoder.ZERO_COPY)
                .dataMimeType("application/json")
                .connect(TcpClientTransport.create("localhost", TCP_PORT))
                .doOnNext(x -> log.info("Client started."))
                .block();
    }

    public void sendPersons(List<Person> persons) {
        Flux<Payload> requestPayloads = Flux.fromIterable(persons).map(clientController::createClientPayload);
        this.socket.requestChannel(requestPayloads)
                .doOnNext(payload -> log.info("Received payload from server: [{}]", payload.getDataUtf8()))
                .doOnNext(payload -> {
                    try {
                        clientController.processServerPayload(payload);
                    } finally {
                        payload.release();
                    }
                }).blockLast();
    }

    public void dispose() {
        log.info("Client stopped.");
        this.socket.dispose();
    }
}
