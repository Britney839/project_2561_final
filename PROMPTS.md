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