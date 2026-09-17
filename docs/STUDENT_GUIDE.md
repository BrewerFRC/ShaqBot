# Learning the ShaqBot program

This guide describes the code in this repository, using the project's existing
WPILib 2024.3.2 APIs. Examples below explain existing code or suggest exercises;
they do not add robot actions.

## Follow the program

1. `Main.main()` passes `Robot::new` to WPILib. That method reference is a factory:
   WPILib decides when to construct the robot, after its startup work.
2. `Robot` creates its hardware objects once. Each motor owns one PWM channel;
   the controller reads a Driver Station USB slot.
3. `robotInit()` publishes the autonomous chooser and inverts the left motor.
4. WPILib calls the appropriate mode callbacks and then `robotPeriodic()` on its
   normal 20 ms loop, about 50 times per second.

`Init` callbacks run on entry to a mode. `Periodic` callbacks repeat. Return
promptly from each callback: a long loop or `Timer.delay()` would delay other
robot work. The empty hooks are deliberate extension points. Some inherited
WPILib periodic methods print a message, so retaining these overrides also
preserves the current console behavior.

See the [2024 lifecycle implementation](https://github.com/wpilibsuite/allwpilib/blob/v2024.3.2/wpilibj/src/main/java/edu/wpi/first/wpilibj/IterativeRobotBase.java)
and [TimedRobot implementation](https://github.com/wpilibsuite/allwpilib/blob/v2024.3.2/wpilibj/src/main/java/edu/wpi/first/wpilibj/TimedRobot.java).

## Read names and types

`LEFT_DRIVE_PWM_CHANNEL` is a constant: `static final` gives the class one value
that cannot be reassigned. Its name says what the number means. PWM channel 0
and controller USB slot 0 are separate numbering systems.

`private final PWMSparkMax leftDriveMotor` keeps the hardware reference inside
`Robot` and prevents reassignment. `final` does not freeze the motor object;
`leftDriveMotor.set(...)` can still update its output.

`leftDriveMotor::set` supplies a function to `DifferentialDrive`. Conceptually it
means `output -> leftDriveMotor.set(output)`. Passing the function does not run
the motor immediately. The drive helper invokes it when computing outputs.
`SendableRegistry.addChild(...)` associates those hardware objects with the
drive helper for WPILib's dashboard tooling.

`PWMSparkMax` controls the SPARK MAX through PWM here. These constants are not
CAN IDs, and this project does not configure REV CAN features or closed-loop
velocity control. See [WPILib's motor controller overview](https://docs.wpilib.org/en/2024/docs/software/hardware-apis/motors/using-motor-controllers.html).

## Trace a joystick input

The left stick supplies translation and the right stick supplies turning:

```java
double forwardInput = -driverController.getLeftY() * DRIVE_INPUT_SCALE;
double turnInput = -driverController.getRightX() * DRIVE_INPUT_SCALE;
robotDrive.arcadeDrive(forwardInput, turnInput);
```

For left Y = `-0.5` and right X = `0.0`, the helper receives `0.3` and `0.0`.
These are normalized requests, not meters per second or measured motor speed.
The negative signs preserve the existing controller direction mapping.

The two-argument `arcadeDrive` call applies the default deadband and squares
inputs while preserving their signs. For straight driving at raw left Y =
`-1.0`, the request is `0.6`; with the 2024 default deadband of `0.02`, the
resulting magnitude is approximately `((0.6 - 0.02) / 0.98)^2 = 0.3503` before
PWM quantization. The left motor's inversion reverses its electrical sign.
Thus, multiplying the input by `0.6` is not the same as setting a 60% final
output limit. Moving that scale after the input shaping changes the feel.

For background, see [WPILib drive classes](https://docs.wpilib.org/en/2024/docs/software/hardware-apis/motors/wpi-drive-classes.html)
and the exact [2024 arcade-drive implementation](https://github.com/wpilibsuite/allwpilib/blob/v2024.3.2/wpilibj/src/main/java/edu/wpi/first/wpilibj/drive/DifferentialDrive.java).

## Understand the autonomous chooser

`SendableChooser<String>` separates the visible dashboard label from the value
the program receives:

| Visible label | Selected value |
| --- | --- |
| Default Auto | Default |
| My Auto | My Auto |

`autonomousInit()` reads the selection once and prints it. Changing the chooser
after that does not change the saved selection until the next autonomous entry.
Both switch branches currently do nothing. Controller driving still executes
afterward in `robotPeriodic()`.

If a future lesson adds a third routine, students need both a chooser option
and a matching switch case. They must also decide how it interacts with the
existing all-mode drive call; a motor command in `autonomousPeriodic()` could
otherwise be overwritten later in the same loop.

## Practice without editing robot behavior

- Predict the two helper inputs for left Y = `0.5`, right X = `-0.25`.
  Answer: forward `-0.3`, turn `0.15`.
- Find where `Default Auto` becomes `Default`. Explain why replacing a visible
  label is different from renaming a private Java variable.
- Trace the callbacks when entering autonomous twice. Which call refreshes the
  saved choice, and which repeats every loop?
- Explain why creating a new `PWMSparkMax(0)` every loop would conflict with
  the existing hardware object instead of providing a fresh reading.

For a later feature branch, explore separating a drivetrain subsystem from
commands. WPILib's command-based framework uses subsystem requirements to
coordinate access to hardware and requires the scheduler to run regularly.
This small `TimedRobot` project has no scheduler today; adding a command class
alone would not make it execute. See [command-based project structure](https://docs.wpilib.org/en/2024/docs/software/commandbased/structuring-command-based-project.html).

Keep future lessons such as autonomous movement, mode-specific driving, and
sensor feedback separate from this behavior-preserving cleanup. Verify each
intentional control change in simulation and on the team's robot.
