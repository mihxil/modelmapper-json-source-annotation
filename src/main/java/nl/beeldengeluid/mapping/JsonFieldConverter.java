package nl.beeldengeluid.mapping;

import java.lang.reflect.Field;
import lombok.SneakyThrows;
import nl.beeldengeluid.mapping.annotations.Source;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;


public class JsonFieldConverter<SOURCE, DESTINATION> implements org.modelmapper.Module {
    
    private final Class<SOURCE> sourceClass;
    private final Class<DESTINATION> destinationClass;
    
    

    public JsonFieldConverter(Class<SOURCE> sourceClass, Class<DESTINATION> destinationClass) {
        this.sourceClass = sourceClass;
        this.destinationClass = destinationClass;
    }

    @Override
    public void setupModule(ModelMapper modelMapper) {
        TypeMap<SOURCE, DESTINATION> sourcedestinationTypeMap = modelMapper.typeMap(sourceClass, destinationClass);
        for (Field field : destinationClass.getDeclaredFields()) {
            field.setAccessible(true);
            Source s = field.getAnnotation(Source.class);
            
            if (s != null) {
              sourcedestinationTypeMap
                    .addMapping((source) -> 
                        "foo",
                        (destination, value) -> set(destination, field, value)
                     );
                        
               
                
            }
        }
        
    }
    
    @SneakyThrows
    private void set(DESTINATION dest, Field field, Object value) {
        field.set(dest, value);
        
    }
}
