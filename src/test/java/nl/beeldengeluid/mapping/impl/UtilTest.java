package nl.beeldengeluid.mapping.impl;

import nl.beeldengeluid.mapping.AnotherSourceClass;
import nl.beeldengeluid.mapping.DestinationClass;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

class UtilTest {

    @Test
   public void getMapping() throws NoSuchFieldException {
       var entry = Util.getEntry(AnotherSourceClass.class, DestinationClass.class.getDeclaredField("title")).orElseThrow();
       Mapping mapping = entry.getValue();
       assertThat(mapping.field().getName()).isEqualTo("moreJson");
   }


}