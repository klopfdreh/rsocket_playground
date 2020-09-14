package de.klopfdreh.rsocket.playground;

import io.rsocket.Payload;
import io.rsocket.RSocket;
import io.rsocket.SocketAcceptor;
import io.rsocket.core.RSocketServer;
import io.rsocket.frame.decoder.PayloadDecoder;
import io.rsocket.transport.netty.server.TcpServerTransport;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

@Slf4j
public class Server {

    private Disposable server;

    private final int TCP_PORT = 7000;

    private ServerController serverController;

    public Server() {
        server = RSocketServer
                .create(SocketAcceptor.with(new RSocketImpl()))
                .payloadDecoder(PayloadDecoder.ZERO_COPY)
                .bind(TcpServerTransport.create("localhost", TCP_PORT))
                .doOnNext(x -> log.info("Server started."))
                .subscribe();
        this.serverController = new ServerController();
    }

    private class RSocketImpl implements RSocket {
        @Override
        public Flux<Payload> requestChannel(Publisher<Payload> payloads) {
            return Flux.from(payloads)
                    .doOnNext(payload -> log.info("Received payload from client: [{}]", payload.getDataUtf8()))
                    .map(payload -> {
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
