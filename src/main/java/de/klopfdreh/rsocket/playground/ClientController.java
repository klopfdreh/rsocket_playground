package de.klopfdreh.rsocket.playground;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.rsocket.Payload;
import io.rsocket.util.DefaultPayload;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;

import java.util.List;

@Slf4j
public class ClientController implements Publisher<Payload> {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    private List<Person> persons;

    public ClientController(List<Person> persons) {
        this.persons = persons;
    }

    @Override
    public void subscribe(Subscriber<? super Payload> subscriber) {
        for (Person person : persons) {
            try {
                byte[] bytes = objectMapper.writeValueAsBytes(person);
                log.info("subscribe: [{}]", new String(bytes));
                subscriber.onNext(DefaultPayload.create(bytes));
            } catch (Exception e) {
                log.error("Error while sending person.", e);
            }
        }
        subscriber.onComplete();
    }

    public void processServerPayload(Payload payload) {
        try {
            String personStatusString = payload.getDataUtf8();
            PersonStatus personStatus = objectMapper.readValue(personStatusString, PersonStatus.class);
            log.info("processServerPayload: [{}]", personStatusString);
            // TODO handle personStatus
        } catch (Exception e) {
            log.error("Error while reading server payload.", e);
        }
    }
}
