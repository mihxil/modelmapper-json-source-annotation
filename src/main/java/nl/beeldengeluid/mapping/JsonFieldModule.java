package nl.beeldengeluid.mapping;

import lombok.extern.slf4j.Slf4j;
import nl.beeldengeluid.mapping.impl.JsonValueReader;
import org.modelmapper.ModelMapper;


@Slf4j
public class JsonFieldModule<SOURCE, DESTINATION> implements org.modelmapper.Module {

    private final JsonValueReader reader;

    private JsonFieldModule(Class<SOURCE> sourceClass, Class<DESTINATION> destinationClass) {
        this.reader = new JsonValueReader(sourceClass, destinationClass);
    }

  /*  public static JsonFieldModule<Object, Object> instance() {
        // For this to work we will need annotation scanning library.
        return new JsonFieldModule<>(Object.class, Object.class);
    }*/

    public static <DESTINATION> JsonFieldModule<Object, DESTINATION> of(Class<DESTINATION> destinationClass) {
        return new JsonFieldModule<>(Object.class, destinationClass);
    }

    public static <SOURCE, DESTINATION> JsonFieldModule<SOURCE, DESTINATION> of(Class<SOURCE> sourceClass, Class<DESTINATION> destinationClass) {
        return new JsonFieldModule<>(sourceClass, destinationClass);
    }

    @Override
    public void setupModule(ModelMapper modelMapper) {
        modelMapper.getConfiguration().addValueReader(reader);
    }



}
