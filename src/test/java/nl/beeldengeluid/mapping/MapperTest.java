package nl.beeldengeluid.mapping;

import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;

class MapperTest {
    
   @Test
   public void jsonMappign() {
       SourceClass source = new SourceClass();
       source.setJson("{'title': 'foobar'}".getBytes(StandardCharsets.UTF_8));

       ModelMapper mapper = new ModelMapper(); 
       mapper.registerModule(new JsonFieldConverter<>(SourceClass.class, DestinationClass.class));
       
       DestinationClass destination = mapper.map(source, DestinationClass.class);
        
   }

}