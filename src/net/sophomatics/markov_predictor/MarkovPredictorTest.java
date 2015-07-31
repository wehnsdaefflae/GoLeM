package net.sophomatics.markov_predictor;

import net.sophomatics.util.Timer;

import java.util.Random;
import java.util.logging.Logger;

/**
 * Created by mark on 12.07.15.
 */
public class MarkovPredictorTest {
    private final static Random r = new Random(3771);
    private final static Logger logger = Logger.getLogger(MarkovPredictorTest.class.getSimpleName());

    private static char[] getRandomArray(int size) {
        char[] randomArray = new char[size];
        Timer t = new Timer(size, "Generating random characters.");
        for (int i = 0; i < size; i++) {
            randomArray[i] = (char) (r.nextInt(26) + 97);
            t.tick();
        }
        t.finished();
        return randomArray;
    }

    public static void main(String[] args) {
        Random r = new Random(3771);
        MarkovPredictorFactory<Character, Character> mFak = new MarkovPredictorFactory<>();
        MarkovPredictor<Character, Character> mp = mFak.newInstance();

        int size = 1000;
        char[] randomArray = getRandomArray(size);

        for (int i = 1; i < randomArray.length; i++) {
            mp.store(randomArray[i-1], randomArray[i]);
        }

        char nextChar, lastChar= "e".charAt(0);
        for (int i = 0; i < 20; i++) {
            nextChar = mp.getConsequence(lastChar);
            System.out.println(String.format("%s -> %s: %.2f, %s", lastChar, nextChar, mp.getProbability(lastChar, nextChar), mp.getFrequency(lastChar, nextChar)));
            lastChar = nextChar;
        }

        System.out.println(mp);

        MarkovPredictor<Character, Character> mp2 = mFak.newInstance();
        mp2.add(mp);

        System.out.println(mp2);
    }
}
