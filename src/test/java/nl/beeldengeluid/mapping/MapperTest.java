package nl.beeldengeluid.mapping;

import java.nio.charset.StandardCharsets;
import lombok.extern.log4j.Log4j2;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;

@Log4j2
class MapperTest {
    
   @Test
   public void fromBytes() {
       SourceClass source = new SourceClass();
       source.setJson("""
       {'title': 'foobar'}
       """.getBytes(StandardCharsets.UTF_8));

       ModelMapper mapper = new ModelMapper(); 
         mapper.registerModule(
             new JsonFieldModule<>(SourceClass.class, DestinationClass.class)
         );
       
       DestinationClass destination = mapper.map(source, DestinationClass.class);

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

       ModelMapper mapper = new ModelMapper();
         mapper.registerModule(
             new JsonFieldModule<>(SourceClass.class, DestinationClass.class)
         );

       DestinationClass destination = mapper.map(source, DestinationClass.class);

       log.info("{}", destination);
       assertThat(destination.getDescription()).isEqualTo("abracadabra");

   }

}