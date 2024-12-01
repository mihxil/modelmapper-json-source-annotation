package nl.beeldengeluid.mapping;

import com.fasterxml.jackson.databind.JsonNode;

public class AnotherSourceClass {

    JsonNode moreJson;

    public JsonNode getMoreJson() {
        return moreJson;
    }

    public void setMoreJson(JsonNode moreJson) {
        this.moreJson = moreJson;
    }
}
