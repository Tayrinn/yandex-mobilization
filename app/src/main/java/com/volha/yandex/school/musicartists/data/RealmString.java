package com.volha.yandex.school.musicartists.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import io.realm.RealmObject;

/**
 * Created by Volha on 19.04.2016.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class RealmString extends RealmObject {

    private String value;

    public RealmString(){}

    public RealmString( String value ){
        this.value =  value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}