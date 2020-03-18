package de.klopfdreh.rsocket.playground;

import io.rsocket.AbstractRSocket;
import io.rsocket.Payload;
import io.rsocket.RSocketFactory;
import io.rsocket.frame.decoder.PayloadDecoder;
import io.rsocket.transport.netty.server.TcpServerTransport;
import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

public class Server {

    private static final Logger LOGGER = LoggerFactory.getLogger(Server.class);

    private Disposable server;

    private final int TCP_PORT = 7000;

    private ServerController serverController;

    public Server() {
        this.server = RSocketFactory.receive().frameDecoder(PayloadDecoder.ZERO_COPY)
                .acceptor((setupPayload, reactiveSocket) -> Mono.just(new RSocketImpl()))
                .transport(TcpServerTransport.create("localhost", TCP_PORT)).start()
                .doOnNext(x -> LOGGER.info("Server started.")).subscribe();
        this.serverController = new ServerController();
    }

    private class RSocketImpl extends AbstractRSocket {
        @Override
        public Flux<Payload> requestChannel(Publisher<Payload> payloads) {
            return Flux.from(payloads).doOnNext(payload -> {
                LOGGER.info("Received payload: [{}]", payload.getDataUtf8());
            }).map(payload -> serverController.processClientPayload(payload))
                    .subscribeOn(Schedulers.parallel());
        }
    }

    public void dispose() {
        LOGGER.info("Server stopped.");
        this.server.dispose();
    }
}
