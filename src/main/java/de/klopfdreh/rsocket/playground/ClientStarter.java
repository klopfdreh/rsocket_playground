package de.klopfdreh.rsocket.playground;

import java.io.InputStreamReader;
import java.util.Arrays;

public class ClientStarter {
    public static void main(String[] args) {
        Client client = new Client();

        Person person1 = new Person();
        person1.setName("Jon");
        person1.setAge(11);
        person1.setSize(192);

        Person person2 = new Person();
        person2.setName("Maria");
        person2.setAge(21);
        person2.setSize(170);

        client.sendPersons(Arrays.asList(person1, person2));
        try {
            InputStreamReader reader = new InputStreamReader(System.in);
            reader.read();
        } catch (Exception e) {
        }
        client.dispose();
    }
}
