/*
 * SupervisedRunner.java
 * Wraps a worker Runnable in a self-healing supervisor loop with exponential
 * backoff and a restart budget. If a worker throws an exception it is logged,
 * the supervisor waits (backoff doubles each time, capped at 5 s), and the
 * worker is restarted. After 10 consecutive seconds of clean running the
 * backoff resets. If the worker fails 5 times within any 30-second window
 * the supervisor gives up permanently.
 */
import java.util.function.BooleanSupplier;

public class SupervisedRunner implements Runnable {

    private static final long INITIAL_BACKOFF_MS  = 100;
    private static final long MAX_BACKOFF_MS       = 5_000;
    private static final long RESET_AFTER_MS       = 10_000;  // reset backoff after 10s clean run
    private static final int  MAX_RESTARTS         = 5;
    private static final long RESTART_WINDOW_MS    = 30_000;  // restart budget window

    private final String         workerName;
    private final Runnable       work;
    private final BooleanSupplier isRunning;

    /**
     * @param workerName  Human-readable name used in log messages.
     * @param work        The task to supervise.
     * @param isRunning   Returns false when the simulation is shutting down;
     *                    the supervisor exits its loop cleanly when this is false.
     */
    public SupervisedRunner(String workerName, Runnable work, BooleanSupplier isRunning) {
        this.workerName = workerName;
        this.work       = work;
        this.isRunning  = isRunning;
    }

    @Override
    public void run() {
        long backoffMs          = INITIAL_BACKOFF_MS;
        int  restartsInWindow   = 0;
        long windowStart        = System.currentTimeMillis();

        while (isRunning.getAsBoolean()) {
            long startTime = System.currentTimeMillis();

            try {
                work.run();
                // work.run() returned normally (only happens on clean shutdown)
                return;

            } catch (Exception e) {
                long now = System.currentTimeMillis();

                // Log the failure with full stack trace
                System.err.println("[SupervisedRunner] Worker \"" + workerName
                        + "\" threw an exception: " + e.getMessage());
                e.printStackTrace();

                // Reset the restart window counter if the window has expired
                if (now - windowStart > RESTART_WINDOW_MS) {
                    restartsInWindow = 0;
                    windowStart      = now;
                }

                restartsInWindow++;

                if (restartsInWindow >= MAX_RESTARTS) {
                    System.err.println("[SupervisedRunner] Worker \"" + workerName
                            + "\" exceeded restart budget; will not be restarted.");
                    return; // give up permanently
                }

                // How long did the worker run before crashing?
                long runDuration = now - startTime;
                if (runDuration >= RESET_AFTER_MS) {
                    // Ran cleanly for at least 10 s before this crash — reset backoff
                    backoffMs = INITIAL_BACKOFF_MS;
                    System.out.println("[SupervisedRunner] Worker \"" + workerName
                            + "\" ran for " + (runDuration / 1000) + "s before failing; "
                            + "backoff reset to " + backoffMs + "ms.");
                }

                System.out.println("[SupervisedRunner] Restarting worker \"" + workerName
                        + "\" in " + backoffMs + "ms "
                        + "(restart " + restartsInWindow + "/" + MAX_RESTARTS + ")");

                try {
                    Thread.sleep(backoffMs);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    return;
                }

                // Double the backoff, cap at max
                backoffMs = Math.min(backoffMs * 2, MAX_BACKOFF_MS);
            }
        }
    }
}