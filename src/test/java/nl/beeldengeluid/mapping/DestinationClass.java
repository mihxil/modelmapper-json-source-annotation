package nl.beeldengeluid.mapping;

import lombok.Data;
import nl.beeldengeluid.mapping.annotations.Source;

@Data
public class DestinationClass {

    public DestinationClass() {

    }
    
    @Source(field = "moreJson", pointer ="/title")
    @Source(field = "json", pointer ="/title", sourceClass = SourceClass.class)
    String title;


    @Source(field = "anotherJson", pointer ="/a/b/value")
    String description;

}
