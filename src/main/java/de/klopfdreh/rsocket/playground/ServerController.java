package de.klopfdreh.rsocket.playground;

import java.util.ArrayList;
import java.util.List;

import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.rsocket.Payload;
import io.rsocket.util.DefaultPayload;

public class ServerController implements Publisher<Payload> {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(ServerController.class);
    
    private List<PersonStatus> personStatus = new ArrayList<>();

    @Override
    public void subscribe(Subscriber<? super Payload> subscriber) {
	for(PersonStatus personStatusSingle: personStatus) {
	    try {
		byte[] bytes = new ObjectMapper().writeValueAsBytes(personStatusSingle);
		LOGGER.info(new String(bytes));
		subscriber.onNext(DefaultPayload.create(bytes));
	    }catch (Exception e) {
		LOGGER.error("Error while sending person status back to the client",e);
	    }
	}
	subscriber.onComplete();
    }
    
    public void processClientPayload(Payload payload) {
	try {
	    String payloadString = payload.getDataUtf8();
	    LOGGER.info("Person added: "+payloadString);
	    PersonStatus personStatusSingle = new PersonStatus();
	    personStatusSingle.setPerson(new ObjectMapper().readValue(payloadString, Person.class));
	    personStatusSingle.setValid(true);
	    this.personStatus.add(personStatusSingle);
	}catch (Exception e) {
	    LOGGER.error("Error while reading client payload", e);
	}
    }

}
