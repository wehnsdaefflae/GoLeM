package net.sophomatics.markov_predictor;

import net.sophomatics.util.Timer;

import java.util.Random;
import java.util.logging.Logger;

/**
 * Test class for Markov predictor
 *
 * @author mark
 * @version 1.0
 * @since 2015-08-05
 *
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
        int size = 10;
        char[] randomArray0 = "peter".toCharArray();//getRandomArray(size);
        char[] randomArray1 = "piper".toCharArray();//getRandomArray(size);

        System.out.println(new String(randomArray0));
        System.out.println(new String(randomArray1));
        System.out.println();

        MarkovPredictorFactory<Character, Character> mFak = new MarkovPredictorFactory<>();
        MarkovPredictor<Character, Character> mp0 = mFak.newInstance();
        MarkovPredictor<Character, Character> mp1 = mFak.newInstance();

        for (int i = 1; i < randomArray0.length; i++) {
            mp0.store(randomArray0[i-1], randomArray0[i]);
        }

        for (int i = 1; i < randomArray1.length; i++) {
            mp1.store(randomArray1[i - 1], randomArray1[i]);
        }


        System.out.println(mp0.print());
        System.out.println(mp1.print());
        System.out.println();

        System.out.println(mp0.getMatch(mp1));
        System.out.println(mp1.getMatch(mp0));
    }
}
