package de.klopfdreh.rsocket.playground;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.rsocket.Payload;
import io.rsocket.util.DefaultPayload;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class ClientController {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public Payload createClientPayload(Person person) {
        try {
            byte[] bytes = objectMapper.writeValueAsBytes(person);
            log.info("subscribe: [{}]", new String(bytes));
            return DefaultPayload.create(bytes);
        } catch (Exception e) {
            log.error("Error while sending person.", e);
            throw new IllegalStateException("Error while sending person.",e);
        }
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
