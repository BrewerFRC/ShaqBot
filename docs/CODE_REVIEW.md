# ShaqBot code review

Reviewed baseline: `a95c175047299a73e4ba1a0b487199b8ca59c8c3` (`main`).
Refactor branch: `feature/bestPracticesAndFormatting`.

## Findings requiring a future behavior change

| Priority | Finding | Implication and future work |
| --- | --- | --- |
| High | Joystick driving is in `Robot.robotPeriodic()`. | It executes in autonomous and test as well as teleop. It can overwrite future autonomous outputs later in the loop. A future change should explicitly decide which mode owns the drivetrain; moving this call now would violate this refactor's scope. |
| Medium | Both autonomous switch branches are empty. | Selecting `My Auto` changes the saved choice and console message but adds no autonomous action. Do not mistake the chooser for a completed routine. |
| Low | The chooser result is not validated before switching on it. | A normal default selection works, but an unknown dashboard selection can produce null. The existing switch would then throw. Adding a fallback would change error behavior, so it is deferred. |

These are findings in the existing program, not new behavior introduced here.

## Cleanup performed

- Replaced unexplained PWM/USB numbers and input scaling with private constants.
- Used consistent Java constant and field names and named local drive inputs.
- Removed the unused `Timer` field/import. It was never started or read and did
  not control any robot action.
- Replaced generic template comments with explanations of the actual robot,
  input shaping, initialization order, method references, and chooser values.
- Kept all lifecycle overrides, including empty methods, to preserve callback
  behavior and suppression of inherited periodic console messages.
- Added build instructions, a student guide, and pinned Java formatting checks.
- Removed the two tracked generated `.class` files under `java/bin/`; Gradle
  builds from `src/main/java`. Added ignores for generated output and caches.
- Added editor and Git line-ending rules. Generated Gradle launch scripts and
  third-party dependency metadata retain their upstream format.

The small program remains a `TimedRobot`. A command-based migration would
introduce scheduling and lifecycle changes beyond this cleanup.

## Behavior-preservation checklist

| Area | Preserved behavior |
| --- | --- |
| Runtime | Java 17, Gradle 8.5, GradleRIO/WPILib 2024.3.2, same entry point |
| Hardware | Left PWM 0, right PWM 1, Xbox USB slot 0; same allocation order |
| Direction | Left motor inverted during `robotInit()`; right unchanged |
| Driving | Negate left Y and right X, multiply each by 0.6, use two-argument `arcadeDrive` |
| Drive defaults | Same deadband, input squaring, maximum output, and motor safety settings |
| Modes | Drive call stays in `robotPeriodic()`; all mode hooks retained |
| Dashboard | `Auto choices` key; `Default Auto` / `Default` and `My Auto` / `My Auto` label/value pairs |
| Autonomous | Read selection once per entry; same log text and no-action switch cases |
| Deployment | Same deployment configuration and vendor dependency versions |

The formatter plugin is used during development only. It is not a robot
runtime dependency. No motors, sensors, commands, or autonomous routines were
added.

## Validation

Completed with a portable Java 17 JDK on Windows:

- Compiled the unmodified baseline successfully before editing Java code.
- Passed `gradlew.bat clean build javadoc -PteamNumber=0`, including
  `spotlessCheck`, Java compilation, JAR assembly, and Javadoc generation.
- Ran the same temporary integration probe against the baseline and refactored
  compiled classes in separate JVMs using the real WPILib desktop HAL. The
  probe exercised the inherited mode-dispatch loop with simulated controller
  and Driver Station data. All **605 motor-output samples** matched to the
  trace precision of 12 decimal places: 121 input pairs in each of disabled,
  teleop, test, default autonomous, and custom autonomous. Inputs included full
  travel, partial travel, zero, and values around the deadband boundary.
- Confirmed matching chooser options/default, autonomous selection log text,
  and the 0.02-second configured period in those traces.
- Compared compiled instructions after normalizing private field names and
  constant-pool references. `Main` and the 11 unchanged robot callbacks match;
  the constructor matches after excluding the unused timer allocation. The
  drive callback now uses named local variables and was covered by the probe.
- Passed `git diff --check` for whitespace errors.

There are no checked-in JUnit tests; Gradle reports `test NO-SOURCE`. The
integration probe is a one-time refactor comparison, not a new permanent test
suite. Its source, baseline classes, and traces are retained locally under
`C:\miscdev\tools\shaqbot-review` for inspection. The build-only team number
`0` was not written into the robot's deployment configuration.

Desktop simulated PWM readings establish consistency of software requests;
they do not verify physical direction, disabled electrical output, real-time
performance, or behavior on the actual drivetrain. No robot deployment or
physical hardware testing was performed.

## Sources used for the review

The review follows the project's pinned 2024 APIs rather than assuming the
latest WPILib release has identical behavior:

- [WPILib 2024 robot program templates](https://docs.wpilib.org/en/2024/docs/software/vscode-overview/creating-robot-program.html)
- [WPILib 2024 drive classes](https://docs.wpilib.org/en/2024/docs/software/hardware-apis/motors/wpi-drive-classes.html)
- [Exact lifecycle source](https://github.com/wpilibsuite/allwpilib/blob/v2024.3.2/wpilibj/src/main/java/edu/wpi/first/wpilibj/IterativeRobotBase.java)
- [Exact differential-drive source](https://github.com/wpilibsuite/allwpilib/blob/v2024.3.2/wpilibj/src/main/java/edu/wpi/first/wpilibj/drive/DifferentialDrive.java)
- [Google Java Style](https://google.github.io/styleguide/javaguide.html)
- [Spotless 6.25.0 Gradle integration](https://github.com/diffplug/spotless/blob/gradle/6.25.0/plugin-gradle/README.md)
