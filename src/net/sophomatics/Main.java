package net.sophomatics;

import net.sophomatics.hierarchy.Hierarchy;

import java.util.Arrays;

public class Main {

    public static void main(String[] args) {
        // TODO: check for integers where there should be floats or doubles (.1f changes nothing)
        Hierarchy<Character, Void> h = new Hierarchy<>(1f);

        String text = "peter piper picked a peck of pickled peppers a peck of pickled peppers peter piper picked if peter piper picked a peck of pickled peppers wheres the peck of pickled peppers peter piper picked ";
        char[] textArray = text.toCharArray();

        int repetitions = 20;

        char[] prediction = new char[textArray.length * repetitions];
        for (int r = 0; r < repetitions; r++) {
            for (int i = 0; i < textArray.length; i++) {
                h.stimulate(textArray[i % textArray.length], null, textArray[(i + 1) % textArray.length]);
                prediction[i + textArray.length * r] = h.predict();
            }
        }

        // TODO: why not anticipate breakdown? 1. check for identical cause, 2. check for breakdown effect
        System.out.println();
        System.out.println(h.print());
        System.out.println(Arrays.toString(h.getStructure().toArray()));
        System.out.println(new String(prediction));
    }

}
