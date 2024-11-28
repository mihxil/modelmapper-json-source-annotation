module nl.beeldengeluid.mapping.annotations {
    requires static lombok;

    requires com.fasterxml.jackson.databind;
    requires modelmapper;
    requires org.slf4j;

    exports nl.beeldengeluid.mapping.annotations;
    exports nl.beeldengeluid.mapping;
}