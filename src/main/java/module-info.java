module nl.beeldengeluid.mapping.modelmapper {
    requires static org.slf4j;
    requires modelmapper;
    requires org.meeuw.mapping.annotations;
    requires static lombok;

    exports nl.beeldengeluid.mapping;
    opens nl.beeldengeluid.mapping to
        modelmapper,
        org.meeuw.mapping.annotations;
}