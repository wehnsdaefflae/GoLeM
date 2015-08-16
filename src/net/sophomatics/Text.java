package net.sophomatics;

import net.sophomatics.util.Timer;

import java.util.HashSet;
import java.util.Set;

public class Text {
    private void start(String text) {
        char[] textArray = text.toCharArray();

        Set<Boolean> actions = new HashSet<>(2);
        actions.add(true);
        //actions.add(false);

        Agent<Character, Boolean> a = new Agent<>(.4f, actions);

        boolean thisAction;
        Character thisChar;
        Character nextChar = null;
        int absPos;
        int thisPos = 0;
        int success = 0;
        float reward = 0;

        int its = 100000;
        Timer t = new Timer(its, "");
        for (int i = 0; i < its; i++) {
            thisChar = textArray[thisPos];
            if (thisChar.equals(nextChar)) {
                success++;
            }
            thisAction = a.interact(thisChar, reward);
            nextChar = a.predict(thisChar, thisAction);
            absPos = i + (thisAction ? 1 : -1);
            reward = thisAction ? 1 : -1;
            thisPos = absPos % textArray.length;
            if (thisPos < 0) {
                thisPos += textArray.length;
            }

            t.tick(a.getStructureString());
        }

        t.finished();

        // TODO: why not anticipate breakdown? 1. check for identical cause, 2. check for breakdown effect

        System.out.println(a.toString());
        System.out.println(String.format("Prediction success rate: %.2f percent", (float) (success * 100) / its));

    }

    public static void main(String[] args) {
        Text m = new Text();
        String text = "peter piper picked a peck of pickled peppers a peck of pickled peppers peter piper picked if peter piper picked a peck of pickled peppers wheres the peck of pickled peppers peter piper picked ";
        //String text = "Just go to your Package Explorer and press F5, or for some laptops fn+F5. The reason is that eclipse thinks that the files are somewhere, but the files are actually somewhere else. By refreshing it, you put them both on the same page. Don't worry, you won't lose anything, but if you want to be extra careful, just back up the files from your java projects folder to somewhere safe.";
        m.start(text);
    }
}
