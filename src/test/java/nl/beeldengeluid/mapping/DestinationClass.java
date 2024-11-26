package nl.beeldengeluid.mapping;

import lombok.Data;
import nl.beeldengeluid.mapping.annotations.Source;

@Data
public class DestinationClass {
    
    @Source(field = "json", path="title")
    String title;
}
