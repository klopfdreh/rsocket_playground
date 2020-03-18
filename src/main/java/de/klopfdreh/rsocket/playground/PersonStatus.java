package de.klopfdreh.rsocket.playground;

public class PersonStatus {

    private boolean valid;

    private Person person;

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }
}
