package com.bosch.eet.skill.management.enums;

import org.codehaus.jackson.annotate.JsonCreator;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public enum Level {
    L50("L50"),
    L51("L51"),
    L52("L52"),
    L53("L53"),
    L54("L54"),
    L55("L55"),
    @JsonProperty("SL+")
    SL_PLUS("SL+")
    ;

    String level;

    Level(String level) {
        this.level = level;
    }

    @JsonCreator
    public static Level fromText(String text){
        for(Level r : Level.values()){
            if(r.getLevel().equals(text)){
                return r;
            }
        }
        throw new IllegalArgumentException();
    }

    @Override
    public String toString() {
        return level;
    }
}
