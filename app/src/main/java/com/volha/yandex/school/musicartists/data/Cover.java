package com.volha.yandex.school.musicartists.data;

/**
 * Created by Volha on 17.04.2016.
 * generated with jsonschema2pojo.org
 */

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder( {
        "small",
        "big"
})
@JsonIgnoreProperties(ignoreUnknown = true)
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Cover)) return false;

        Cover cover = (Cover) o;

        if (getSmall() != null ? !getSmall().equals(cover.getSmall()) : cover.getSmall() != null)
            return false;
        return getBig() != null ? getBig().equals(cover.getBig()) : cover.getBig() == null;

    }

    @Override
    public int hashCode() {
        int result = getSmall() != null ? getSmall().hashCode() : 0;
        result = 31 * result + (getBig() != null ? getBig().hashCode() : 0);
        return result;
    }
}
