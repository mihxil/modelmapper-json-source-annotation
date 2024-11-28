package nl.beeldengeluid.mapping.impl;

import java.lang.reflect.Field;
import nl.beeldengeluid.mapping.annotations.Source;

public record Mapping(Source source, Field field) {

}
