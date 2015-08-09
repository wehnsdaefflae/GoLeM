package net.sophomatics.util;

/**
 * Created by mark on 12.07.15.
 */
public class Timer {
    private final long      targetIterations;
    private final int       timeInterval,
                            targetLen;
    private final double    startTime;
    private long            currentIterations;
    private int             intIterations;
    private double          thisTime,
                            lastTime;
    private String          message;

    public Timer(long maxIterations, final String message) {
        this.targetIterations = maxIterations;
        this.targetLen = Integer.toString((int) this.targetIterations).length();
        this.currentIterations = 0l;
        this.intIterations = 0;
        this.timeInterval = 2000;
        this.startTime = System.currentTimeMillis();
        this.thisTime = -1;
        this.lastTime = this.startTime;

        this.message = message;
    }

    private int[] getTime(double milliseconds) {
        int[] time = new int[3];
        time[0] = (int) ((milliseconds / (1000*60*60)) % 24);
        time[1] = (int) ((milliseconds / (1000*60)) % 60);
        time[2] = (int) ((milliseconds / 1000) % 60);
        return time;
    }

    public void tick(String info) {
        this.tick(1d, info);
    }

    private String getProgressText() {
        String logText;

        if (this.currentIterations < this.targetIterations) {
            double percentDone, restTime, speed;

            percentDone = (100d * this.currentIterations) / this.targetIterations;
            speed = this.currentIterations / (this.thisTime - this.startTime);
            restTime = (this.targetIterations - this.currentIterations) / speed;
            int itemsPerSecond = (int) (speed * 1000);
            int[] time = this.getTime(restTime);

            logText = String.format(
                    "%s %4.1f percent finished (%" + this.targetLen + "d/%d). %02d:%02d:%02d remaining @ %s it/sec.",
                    this.message,
                    percentDone,
                    this.currentIterations,
                    this.targetIterations,
                    time[0],
                    time[1],
                    time[2],
                    itemsPerSecond);
        } else {
            logText = String.format(
                    "%s target no. iterations exceeded: %" + this.targetLen + "d/%d. Remaining time unknown...",
                    this.message,
                    this.currentIterations,
                    this.targetIterations);
        }

        return logText;

    }

    public void tick(final double iterationValue, String info) {
        this.thisTime = System.currentTimeMillis();
        if (this.thisTime - this.lastTime >= this.timeInterval) {
            this.currentIterations += this.intIterations;

            System.out.println(this.getProgressText() + info);

            this.intIterations = 0;
            this.lastTime = this.thisTime;
        }
        this.intIterations += iterationValue;
    }

    public void finished() {
        double totTime = (this.thisTime - this.startTime) / 1000;
        long totalIterations = this.currentIterations + this.intIterations;
        int[] time = this.getTime(totTime);
        String finishedText = String.format(" Completed. Time spent %02d:%02d:%02d for %s iterations.\n", time[0], time[1], time[2], totalIterations);
        System.out.println(this.message + finishedText);
    }
}
