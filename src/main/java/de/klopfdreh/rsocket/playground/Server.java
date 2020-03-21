package de.klopfdreh.rsocket.playground;

import io.rsocket.AbstractRSocket;
import io.rsocket.Payload;
import io.rsocket.RSocketFactory;
import io.rsocket.frame.decoder.PayloadDecoder;
import io.rsocket.transport.netty.server.TcpServerTransport;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Slf4j
public class Server {

    private Disposable server;

    private final int TCP_PORT = 7000;

    private ServerController serverController;

    public Server() {
        this.server = RSocketFactory.receive().frameDecoder(PayloadDecoder.ZERO_COPY)
                .acceptor((setupPayload, reactiveSocket) -> Mono.just(new RSocketImpl()))
                .transport(TcpServerTransport.create("localhost", TCP_PORT)).start()
                .doOnNext(x -> log.info("Server started.")).subscribe();
        this.serverController = new ServerController();
    }

    private class RSocketImpl extends AbstractRSocket {
        @Override
        public Flux<Payload> requestChannel(Publisher<Payload> payloads) {
            return Flux.from(payloads).doOnNext(payload -> {
                log.info("Received payload: [{}]", payload.getDataUtf8());
            }).map(payload -> {
                try {
                    return serverController.processClientPayload(payload);
                } finally {
                    payload.release();
                }
            }).subscribeOn(Schedulers.parallel());
        }
    }

    public void dispose() {
        log.info("Server stopped.");
        this.server.dispose();
    }
}
