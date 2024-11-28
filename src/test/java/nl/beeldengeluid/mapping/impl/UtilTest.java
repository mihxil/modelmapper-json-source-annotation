package nl.beeldengeluid.mapping.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import java.util.List;
import nl.beeldengeluid.mapping.AnotherSourceClass;
import nl.beeldengeluid.mapping.DestinationClass;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;

class UtilTest {

    @Test
    public void getMapping() throws NoSuchFieldException {
        var entry = Util.getEntry(AnotherSourceClass.class, DestinationClass.class.getDeclaredField("title")).orElseThrow();
        Mapping mapping = entry.getValue();
        assertThat(mapping.field().getName()).isEqualTo("moreJson");
   }

   @Test
   public void unwrap() throws JsonProcessingException {
       JsonNode node = Util.MAPPER.readTree("""
           [
           null,
           true,
           1,
           1.0,
           [1, 2, 3],
           "text"
           ]
           """);
       List<Object> unwrapped = (List<Object>) Util.unwrapJson(node);
       assertThat(unwrapped).containsExactly(
           null,
           Boolean.TRUE,
           1,
           1.0,
           List.of(1, 2, 3),
           "text"

       );

   }


}