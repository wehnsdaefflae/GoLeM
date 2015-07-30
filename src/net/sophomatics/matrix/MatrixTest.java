package net.sophomatics.matrix;

import junit.framework.TestCase;
import net.sophomatics.util.Timer;

import java.util.Random;

/**
 * Created by mark on 12.07.15.
 */
public class MatrixTest extends TestCase {
    private static final Random r = new Random(7331);

    private static int[] getRandomArray(int size) {
        int totalIterations = size * size;
        Timer t = new Timer(totalIterations, "Generating random array.");
        int[] randomArray = new int[totalIterations];
        for (int i = 0; i < totalIterations; i++) {
            randomArray[i] = r.nextInt(10);
            t.tick();
        }
        t.finished();
        return randomArray;
    }

    private static void fillMatrix(Matrix<Integer, Integer, Integer> matrix, int[] randomArray, int size) {
        int totalIterations = size * size;
        Timer t = new Timer(totalIterations, "Filling matrix.");
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                matrix.put(x, y, randomArray[x * size + y]);
                t.tick();
            }
        }
        t.finished();
    }

    private static void checkMatrix(Matrix<Integer, Integer, Integer> matrix, int[] randomArray, int size){
        int totalIterations = size * size;
        Timer t = new Timer(totalIterations, "Checking matrix.");
        int value, expected;
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                value = matrix.get(x, y);
                expected = randomArray[x * size + y];
                assertEquals(value, expected);
                t.tick();
            }
        }
        t.finished();
    }

    public static void main(String[] args) {
        int size = 5000;
        int[] randomArray = getRandomArray(size);
        int[] wrongArray = new int[randomArray.length];
        System.arraycopy(randomArray, 0, wrongArray, 0, randomArray.length);
        wrongArray[wrongArray.length-1] = -1;

        Matrix<Integer, Integer, Integer> m0 = new NestedMapMatrix<>();
        fillMatrix(m0, randomArray, size);
        checkMatrix(m0, randomArray, size);

        Matrix<Integer, Integer, Integer> m1 = new NestedMapMatrix<>(100);
        fillMatrix(m1, randomArray, size);
        checkMatrix(m1, randomArray, size);

        Matrix<Integer, Integer, Integer> m2 = new NestedMapMatrix<>();
        m2.integrate(m0);
        checkMatrix(m2, randomArray, size);

    }

}
