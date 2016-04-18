package com.volha.yandex.school.musicartists.data;

/**
 * Created by Volha on 17.04.2016.
 * generated with jsonschema2pojo.org
 */
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder( {
        "small",
        "big"
})
public class Cover {

    @JsonProperty("small")
    private String small;
    @JsonProperty("big")
    private String big;

    /**
     * @return The small
     */
    @JsonProperty("small")
    public String getSmall() {
        return small;
    }

    /**
     * @param small The small
     */
    @JsonProperty("small")
    public void setSmall( String small ) {
        this.small = small;
    }

    /**
     * @return The big
     */
    @JsonProperty("big")
    public String getBig() {
        return big;
    }

    /**
     * @param big The big
     */
    @JsonProperty("big")
    public void setBig( String big ) {
        this.big = big;
    }
}
