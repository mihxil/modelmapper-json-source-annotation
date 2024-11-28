package nl.beeldengeluid.mapping;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import lombok.extern.log4j.Log4j2;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.modelmapper.spi.ValueReader;

@Log4j2
class MapperTest {

    ModelMapper sourceToDest = new ModelMapper();

    {
        sourceToDest.registerModule(JsonFieldModule.of(SourceClass.class,
            DestinationClass.class));
    }


    @Test
    void testMapper() {
        SourceClass source = new SourceClass();
        source.setJson("""
              {
              "title": 1
            }
            """.getBytes(StandardCharsets.UTF_8));
        ValueReader<Object> reader = (ValueReader<Object>) sourceToDest.getConfiguration().getValueReaders().get(1);
        Integer t = (Integer) reader.get(source, "title");
        assertThat(t).isEqualTo(1);
        log.info("{}", t);

    }

    
   @Test
    void fromBytes() {
       SourceClass source = new SourceClass();
       source.setJson("""
       {'title': 'foobar'}
       """.getBytes(StandardCharsets.UTF_8));


       DestinationClass destination = sourceToDest.map(source, DestinationClass.class);

       log.info("{}", destination);
       assertThat(destination.getTitle()).isEqualTo("foobar");

   }


   @Test
   public void fromString() {
       SourceClass source = new SourceClass();
       source.setAnotherJson("""
         { a: {
             b: {
                value: "abracadabra"
             }
         }
         }
       """);

       DestinationClass destination = sourceToDest.map(source, DestinationClass.class);

       log.info("{}", destination);
       assertThat(destination.getDescription()).isEqualTo("abracadabra");
   }




   @Test
   public void fromObject() throws IOException {
       AnotherSourceClass source = new AnotherSourceClass();
       source.setMoreJson(new ObjectMapper().readTree("""
         { "title": "abracadabra"
         }
       """.getBytes(StandardCharsets.UTF_8)));

       ModelMapper mapper = new ModelMapper();
       mapper.registerModule(JsonFieldModule.of(DestinationClass.class));

       DestinationClass destination = mapper.map(source, DestinationClass.class);

       log.info("{}", destination);
       assertThat(destination.getTitle()).isEqualTo("abracadabra");
   }

   /* @Test
    @Disabled
    public void withInstance() {
       AnotherSourceClass source = new AnotherSourceClass();
       source.setMoreJson("""
         { title: "abracadabra"
         }
       """.getBytes(StandardCharsets.UTF_8));

       ModelMapper mapper = new ModelMapper();
       mapper.registerModule(JsonFieldModule.instance());

       DestinationClass destination = mapper.map(source, DestinationClass.class);

       log.info("{}", destination);
       assertThat(destination.getTitle()).isEqualTo("abracadabra");
   }*/


}