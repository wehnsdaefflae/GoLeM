package net.sophomatics;

import net.sophomatics.hierarchy.Hierarchy;
import net.sophomatics.util.Timer;

import java.util.Arrays;
import java.util.Random;

public class Main {

    private void interact(char[] textArray) {
        Random r = new Random(3771);

        // TODO: check for integers where there should be floats or doubles (.1f changes nothing)
        // using likelihood as MarkovPredictor.getMatch() causes perfect models, makes ]0, 1[ thresholds redundant

        Hierarchy<Character, Boolean> h = new Hierarchy<>(1f);

        char[] prediction = new char[textArray.length];

        boolean isForward;
        char thisChar;
        Character prevChar = null;
        int thisPos;

        int its = 10000;
        Timer t = new Timer(its, "");
        for (int i = 0; i < its; i++) {
            //isForward = r.nextBoolean();
            isForward = true;
            thisPos = (i + (isForward ? 1 : -1)) % textArray.length;

            thisChar = textArray[thisPos];
            if (prevChar != null) {
                prediction[thisPos] = h.predict(prevChar, isForward);
                h.perceive(prevChar, isForward, thisChar);
            }

            prevChar = thisChar;
            t.tick();
        }

        t.finished();

        // TODO: why not anticipate breakdown? 1. check for identical cause, 2. check for breakdown effect

        System.out.println();
        System.out.println(h.print());
        System.out.println(Arrays.toString(h.getStructure().toArray()));
        System.out.println(new String(prediction));

    }

    public static void main(String[] args) {
        String text = "peter piper picked a peck of pickled peppers a peck of pickled peppers peter piper picked if peter piper picked a peck of pickled peppers wheres the peck of pickled peppers peter piper picked ";
        Main m = new Main();
        m.interact(text.toCharArray());
    }
}
