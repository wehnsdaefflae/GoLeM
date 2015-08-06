package net.sophomatics.sandbox;

import net.sophomatics.markov_predictor.MarkovPredictor;
import net.sophomatics.markov_predictor.MarkovPredictorFactory;
import net.sophomatics.util.Tuple;

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
        MarkovPredictorFactory<Tuple<Integer, Void>, Integer> mFak = new MarkovPredictorFactory<>();

        MarkovPredictor<Tuple<Integer, Void>, Integer> mp0 = mFak.newInstance();
        mp0.store(new Tuple<Integer, Void>(0, null), 1);

        MarkovPredictor<Tuple<Integer, Void>, Integer> mp1 = mFak.newInstance();
        mp1.store(new Tuple<Integer, Void>(0, null), 1);
        mp1.store(new Tuple<Integer, Void>(0, null), 2);
        mp1.store(new Tuple<Integer, Void>(0, null), 2);

        System.out.println(mp0.print());
        System.out.println();
        System.out.println(mp1.print());
        System.out.println();
        System.out.println(mp0.getMatch(mp1));
        System.out.println(mp1.getMatch(mp0));
    }
}
