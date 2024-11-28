/*
 * Copyright (C) 2024 Licensed under the Apache License, Version 2.0
 */
package nl.beeldengeluid.mapping.impl;

import java.lang.reflect.Field;
import nl.beeldengeluid.mapping.annotations.Source;

record Mapping(Source source, Field field) {

}
