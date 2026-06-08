## Session 1 – 2026-05-25
Task: Task 1 (Maneuver script from file)
Tool: GitHub Copilot
Prompt (verbatim):
> I need to understand what the existing createAutomatedDemoThread does before
> I change it. Can you summarize what the hard-coded maneuver sequence in
> Main.java is doing, step by step?

Suggestion summary:
Copilot walked through each Thread.sleep / setTargetValue block and described
the sequence: 8s level, 12s right turn at roll=2/yaw=2, 8s level, 12s left
turn at roll=-2/yaw=-2, 10s gentle climb at pitch=-5, 8s level, 10s descent
at pitch=3, 10s level, then loops.

Decision: Accepted as written
Why: This was a reading/comprehension step, not code generation. Confirmed
the sequence before writing default_maneuvers.csv so the CSV reproduces
it exactly.


## Session 2 - 2026-05-26
Tool: Github Copilot 
Prompt: > Why isn't my maneuvers being used in line 29? (Error we encountered)

Suggestion Summary: Your maneuvers list is declared and initialized, but you never add any Maneuver objects to it inside your constructor. After parsing each valid line from the CSV, you should create a Maneuver object and add it to the maneuvers list. 
Decision: It gave us code to fix our issue, we decided to populate the list ourselves and used it as a guide to help us along.


## Session 3 - 2026-05-27
Tool: Github Copilot
Prompt: How would I fix the error the code is giving me?

Suggestion Summary: The code had an error where we previously wrote this.maneuvers where Maneuver was a private final list, it could not be grabbed without referencing it with "this." However, it was giving an error once we added helper methods to continue the CSV file task.
Decision: Accepted as written, we created a local variable as per copilot suggestions to debug and assign the unmodifiable list to the final field at the end.

## Session 4 – 2026-05-29
Tool: GitHub Copilot

Prompt (verbatim):
The parser currently throws on the first error it finds. I want it to collect
all errors from the whole file first, print them all, then throw once at the
end. How do I modify the ManeuverScript constructor to do this?

Suggestion summary:
Copilot changed the constructor to use a List errors collector, replaced
all throw statements inside the loop with errors.add(...) and continue, and
added a block after the loop that prints all errors with forEach and throws
once with a summary message.

Decision: Accepted with modifications
Why: The original suggestion used a single large try/catch per row which would
have stopped parsing remaining fields on that row if the first field failed.
Changed it to four separate try/catch blocks per row so all four fields are
attempted independently, giving the most complete error report per line.

## Session 5 – 2026-06-01
Task: Task 1 (Maneuver script from file)
Tool: GitHub Copilot
Prompt (verbatim):
> How do I modify Main.java to load a ManeuverScript using the --script flag
> and replace createAutomatedDemoThread with a short loop that iterates
> through the script?

Suggestion summary:
Copilot showed how to replace the old createAutomatedDemoThread call with a
script-loading block and rewrite the method to loop through the maneuvers
using modulo to wrap back to the start.

Decision: Accepted with modifications
Why: Had to declare the script variable before the try block so it was
accessible when passed into createAutomatedDemoThread.

## Session 6 – 2026-06-02
Task: Task 2 (Observer pattern for direction updates)
Tool: Github Copilot
Prompts (verbatim):
> Does this fall under the requirements needed for task 2?
> Why is my getCurrentValue underlined in my DirectionControl.java file?

Decision: I understood that the getCurrentValue was duplicated and fixed the issue. 

## Session 6 – 2026-06-08
Task: Task 3 (Self-healing worker threads)
Tool: Copilot
Prompt:
> Write a Java class called SupervisedRunner that implements Runnable, takes a
> worker name, a Runnable, and a BooleanSupplier. It should run the work in a
> loop, catch exceptions, log them with the worker name and stack trace, sleep
> with exponential backoff starting at 100ms doubling each time capped at 5
> seconds, reset backoff after 10 consecutive seconds of clean running, and
> give up permanently after 5 restarts within 30 seconds.

Suggestion summary:
Copilot produced the full SupervisedRunner class with the backoff logic,
restart budget window tracking, clean shutdown on InterruptedException,
and all log messages including the permanent give-up message.

Decision: Accepted with modifications
Why: The original tracked restart count as a simple counter that never
reset. Changed it to reset restartsInWindow and windowStart whenever the
30-second window expires, so a worker that crashes once every 40 seconds
never hits the budget cap as the brief intends.

---

## Session 7 – 2026-06-08
Task: Task 3 (Self-healing worker threads)
Tool: Copilot
Prompt:
> How do I apply SupervisedRunner to the turbulence thread, demo thread, and
> resource monitor in Main.java, and add an --inject-failures flag that makes
> the turbulence thread throw at 3, 6, and 9 seconds?

Suggestion summary:
Copilot showed converting createTurbulenceThread and createAutomatedDemoThread
to return Runnable instead of Thread, wrapping each in a new Thread with a
SupervisedRunner, doing the same for the resource monitor, and adding an
elapsed-time check inside the turbulence loop that throws a RuntimeException
at the 3, 6, and 9 second marks when the flag is set.

Decision: Accepted with modifications
Why: Copilot's inject-failures check used System.currentTimeMillis() divided
by 1000 compared with == which could miss the exact second if the thread
slept through it. Changed it to check elapsed >= 3 && elapsed < 4 style
intervals to make the triggers reliable regardless of sleep ti