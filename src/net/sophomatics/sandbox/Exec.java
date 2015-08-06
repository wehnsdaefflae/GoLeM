package net.sophomatics.sandbox;

import net.sophomatics.markov_predictor.MarkovPredictor;
import net.sophomatics.markov_predictor.MarkovPredictorFactory;
import net.sophomatics.util.Tuple;

import java.util.Random;

/**
 * Testing class
 *
 * @author mark
 * @version 1.0
 * @since 2015-08-05
 *
 */
public class Exec {
    public static void main(String[] args) {
        Random r = new Random(3771);
        MarkovPredictorFactory<Tuple<Integer, Void>, Integer> mFak = new MarkovPredictorFactory<>();

        MarkovPredictor<Tuple<Integer, Void>, Integer> mp0 = mFak.newInstance();
        for (int i = 0; i < 10; i++) {
            mp0.store(new Tuple<Integer, Void>(r.nextInt(3), null), r.nextInt(3));
        }

        MarkovPredictor<Tuple<Integer, Void>, Integer> mp1 = mFak.newInstance();
        for (int i = 0; i < 2; i++) {
            mp1.store(new Tuple<Integer, Void>(r.nextInt(3), null), r.nextInt(3));
        }

        System.out.println(mp0.print());
        System.out.println();
        System.out.println(mp1.print());
        System.out.println();
        System.out.println(mp0.getMatch(mp1));
        System.out.println(mp1.getMatch(mp0));
    }
}
