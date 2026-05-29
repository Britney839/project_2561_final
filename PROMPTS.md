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