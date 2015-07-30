package net.sophomatics.util;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by mark on 19.07.15.
 */
public class SegmentSequence {

    public static void main(String[] args) {
        String text = "peter piper picked a peck of pickled peppers ";//a peck of pickled peppers peter piper picked if peter piper picked a peck of pickled peppers wheres the peck of pickled peppers peter piper picked ";
        Map<Character, Character> m = new HashMap<>();

        Character thisChar, predChar, nextChar;
        for (int i = 0; i < 10; i++) {
            for (int p = 0; p < text.length(); p++) {
                thisChar = text.charAt(p);
                predChar = m.get(thisChar);
                nextChar = text.charAt((p+1) % text.length());
                if (predChar == null) {
                    m.put(thisChar, nextChar);
                    System.out.print(nextChar);

                } else if (nextChar == predChar) {
                    System.out.print(nextChar);

                } else {
                    m.clear();
                    m.put(thisChar, nextChar);
                    System.out.print("|" + thisChar);
                    System.out.print(nextChar);
                }
            }
        }
    }
}
