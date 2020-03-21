package de.klopfdreh.rsocket.playground;

import lombok.Data;

@Data
public class PersonStatus {

    private boolean valid;

    private Person person;
}
