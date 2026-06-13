package me.kveex.somelocators.fun;

import java.util.Random;

public enum RandomNullErrorPhrases {
    NULL_PHRASE_1("%s is null for some reason, don't worry, I've created new one just for you :)"),
    NULL_PHRASE_2("Oh? A %s is null again? I expected that, here I'll fix it for you"),
    NULL_PHRASE_3("You know that %s is null? Of course not! That's why i'm here, so you don't even notice a difference"),
    NULL_PHRASE_4("I'll divide that null from %s by 0, hahaha >:D! Yea sorry, i know, too much"),
    NULL_PHRASE_5("Look who tried to make a mess, well %s won't bother you again, at least for now");

    private final String phrase;

    RandomNullErrorPhrases(String phrase) {
        this.phrase = phrase;
    }

    public static String getRandomPhrase(String problem) {
        Random random = new Random();

        RandomNullErrorPhrases[] values = RandomNullErrorPhrases.values();
        int length = values.length;

        String phrase = values[random.nextInt(length)].phrase;
        return phrase.formatted(problem);
    }
}
