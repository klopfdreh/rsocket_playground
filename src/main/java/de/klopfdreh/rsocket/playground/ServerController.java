package de.klopfdreh.rsocket.playground;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.rsocket.Payload;
import io.rsocket.util.DefaultPayload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ServerController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ServerController.class);

    public Payload processClientPayload(Payload payload) {
        try {
            String payloadString = payload.getDataUtf8();
            PersonStatus personStatusSingle = new PersonStatus();
            personStatusSingle.setPerson(new ObjectMapper().readValue(payloadString, Person.class));
            personStatusSingle.setValid(true);
            byte[] bytes = new ObjectMapper().writeValueAsBytes(personStatusSingle);
            LOGGER.info("Person processed: " + payloadString);
            return DefaultPayload.create(bytes);
        } catch (Exception e) {
            LOGGER.error("Error while reading client payload", e);
            throw new RuntimeException(e);
        }
    }

}
