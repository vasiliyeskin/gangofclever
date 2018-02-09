package ru.pet.gangofclever;

import com.google.common.collect.ImmutableMap;

import java.util.Map;

public class LetterSets {
    public final static Map<String, Integer> orangeSet = ImmutableMap.<String, Integer>builder()
            .put("а", 1)
            .put("в", 1)
            .put("е", 1)
            .put("и", 1)
            .put("л", 1)
            .put("н", 1)
            .put("о", 1)
            .put("с", 1)
            .put("т", 1).build();
}
