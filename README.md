# ShaqBot

Java robot code for ShaqBot, using WPILib **2024.3.2**, Java **17**, and the
`TimedRobot` lifecycle. The Gradle project is in [`java/`](java/).

## Start here

Read [`Robot.java`](java/src/main/java/frc/robot/Robot.java) alongside the
[student guide](docs/STUDENT_GUIDE.md). The [code review](docs/CODE_REVIEW.md)
explains the existing behavior, changes in this branch, and future improvements.

| Connection | Configuration |
| --- | --- |
| Left drive motor | PWM channel 0, inverted |
| Right drive motor | PWM channel 1, not inverted |
| Xbox controller | Driver Station USB slot 0 |
| Forward/backward input | Negated left-stick Y, multiplied by 0.6 |
| Turning input | Negated right-stick X, multiplied by 0.6 |

**Current behavior:** controller driving runs in `robotPeriodic()` in every mode,
including autonomous and test. The two autonomous choices contain no additional
actions. Disabled mode still executes the Java callback; the roboRIO's disabled
state inhibits physical motor output. This refactor preserves that behavior.

## Build and formatting

Use a Java 17 JDK or the WPILib 2024 development environment. Open `java/` as the
project in WPILib VS Code. From PowerShell:

```powershell
cd C:\miscdev\repos\ShaqBot\java
.\gradlew.bat build -PteamNumber=0
.\gradlew.bat spotlessApply -PteamNumber=0
.\gradlew.bat spotlessCheck -PteamNumber=0
```

`0` is a placeholder for local builds, because this repository does not include
team preferences. Use the team's actual number in its local WPILib preferences
or with `-PteamNumber=YOUR_TEAM_NUMBER` when configuring deployment. The commands
above do not deploy. On macOS/Linux, use `bash ./gradlew` in place of
`.\gradlew.bat`.

The first build requires internet access to download Gradle and dependencies.
`build` includes the formatting check. `spotlessApply` edits Java formatting;
`spotlessCheck` only checks it. Spotless 6.25.0 uses google-java-format 1.19.2,
with two-space Java indentation and a 100-column target. The formatter is a
development tool and is not included in the robot runtime. `.editorconfig`
provides matching editor defaults and four-space indentation for Gradle files.

For generated API documentation, run:

```powershell
.\gradlew.bat javadoc -PteamNumber=0
```

Open `java/build/docs/javadoc/index.html` from the repository root afterward.
See the code review for the checks performed on this branch and their limits.
