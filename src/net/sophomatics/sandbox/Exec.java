package net.sophomatics.sandbox;

import net.sophomatics.markov_predictor.MarkovPredictor;
import net.sophomatics.markov_predictor.MarkovPredictorFactory;
import net.sophomatics.util.Tuple;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;

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
        System.out.println(String.format("5 mod 2 =\t%s", 5 % 2));
        System.out.println(String.format("-5 mod 2 =\t%s", -5 % 2));
        System.out.println(String.format("5 mod -2 =\t%s", 5 % -2));
        System.out.println(String.format("-5 mod -2 =\t%s", -5 % -2));

    }
}
