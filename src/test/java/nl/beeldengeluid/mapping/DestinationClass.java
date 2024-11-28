package nl.beeldengeluid.mapping;

import lombok.Data;
import nl.beeldengeluid.mapping.annotations.Source;

@Data
public class DestinationClass {

    public DestinationClass() {

    }
    
    @Source(field = "json", pointer ="/title")
    String title;


    @Source(field = "anotherJson", pointer ="/a/b/value")
    String description;

}
