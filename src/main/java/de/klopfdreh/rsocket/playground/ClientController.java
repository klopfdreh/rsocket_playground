package de.klopfdreh.rsocket.playground;

import java.util.List;

import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.rsocket.Payload;
import io.rsocket.util.DefaultPayload;

public class ClientController implements Publisher<Payload> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClientController.class);

    private List<Person> persons;

    public ClientController(List<Person> persons) {
	this.persons = persons;
    }

    @Override
    public void subscribe(Subscriber<? super Payload> subscriber) {
	for (Person person : persons) {
	    try {
		byte[] bytes = new ObjectMapper().writeValueAsBytes(person);
		subscriber.onNext(DefaultPayload.create(bytes));
	    } catch (Exception e) {
		LOGGER.error("Error while sending person", e);
	    }
	}
	subscriber.onComplete();
    }

    public void processServerPayload(Payload payload) {
	try {
	    String personStatusString = payload.getDataUtf8();
	    PersonStatus personStatus = new ObjectMapper().readValue(personStatusString, PersonStatus.class);
	    LOGGER.info(personStatusString);
	    // TODO handle personStatus
	} catch (Exception e) {
	    LOGGER.error("Error while reading server payload", e);
	}
    }
}
