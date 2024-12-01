package nl.beeldengeluid.mapping;

import lombok.Getter;
import lombok.Setter;
import org.meeuw.mapping.annotations.Source;

@Getter@Setter
public class DestinationClass {

    public DestinationClass() {

    }
    
    @Source(field = "moreJson", pointer ="/title")
    @Source(field = "json", pointer ="/title", sourceClass = SourceClass.class)
    String title;


    @Source(field = "anotherJson", pointer ="/a/b/value")
    String description;

}
