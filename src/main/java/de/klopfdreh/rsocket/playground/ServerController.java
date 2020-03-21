package de.klopfdreh.rsocket.playground;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.rsocket.Payload;
import io.rsocket.util.DefaultPayload;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ServerController {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public Payload processClientPayload(Payload payload) {
        try {
            String payloadString = payload.getDataUtf8();
            PersonStatus personStatusSingle = new PersonStatus();
            personStatusSingle.setPerson(objectMapper.readValue(payloadString, Person.class));
            personStatusSingle.setValid(true);
            byte[] bytes = objectMapper.writeValueAsBytes(personStatusSingle);
            log.info("processClientPayload: [{}]", new String(bytes));
            return DefaultPayload.create(bytes);
        } catch (Exception e) {
            log.error("Error while reading client payload.", e);
            throw new RuntimeException(e);
        }
    }

}
