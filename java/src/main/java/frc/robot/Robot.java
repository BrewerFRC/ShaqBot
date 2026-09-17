// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.util.sendable.SendableRegistry;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.motorcontrol.PWMSparkMax;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * ShaqBot's two-motor arcade-drive program, using WPILib's timed lifecycle.
 *
 * <p>WPILib calls initialization methods when modes begin and periodic methods on its normal 20 ms
 * loop. Hardware is constructed once and reused; do not allocate another controller for the same
 * PWM channel inside a periodic method.
 *
 * <p>This program intentionally retains controller driving in {@link #robotPeriodic()}, including
 * autonomous and test. Moving it to {@link #teleopPeriodic()} would change existing behavior. See
 * {@code docs/STUDENT_GUIDE.md} for the control flow and practice exercises.
 */
public class Robot extends TimedRobot {
  // PWM channels on the roboRIO, not CAN device IDs.
  private static final int LEFT_DRIVE_PWM_CHANNEL = 0;
  private static final int RIGHT_DRIVE_PWM_CHANNEL = 1;

  // USB slot assigned to the Xbox controller in the Driver Station.
  private static final int DRIVER_CONTROLLER_PORT = 0;

  // Scale joystick requests BEFORE DifferentialDrive applies deadband and input squaring.
  // This is not a 60% final motor-output limit or a speed in meters per second.
  private static final double DRIVE_INPUT_SCALE = 0.6;

  // Chooser values are separate from their visible dashboard labels.
  private static final String DEFAULT_AUTO = "Default";
  private static final String CUSTOM_AUTO = "My Auto";

  // Capture the selection on autonomous entry, rather than reading it on every loop.
  private String selectedAuto;
  private final SendableChooser<String> autoChooser = new SendableChooser<>();

  // `final` prevents reference reassignment; these objects can still update hardware state.
  private final PWMSparkMax leftDriveMotor = new PWMSparkMax(LEFT_DRIVE_PWM_CHANNEL);
  private final PWMSparkMax rightDriveMotor = new PWMSparkMax(RIGHT_DRIVE_PWM_CHANNEL);
  private final DifferentialDrive robotDrive =
      new DifferentialDrive(leftDriveMotor::set, rightDriveMotor::set);
  private final XboxController driverController = new XboxController(DRIVER_CONTROLLER_PORT);

  /** Associates the motor objects with their drive helper for WPILib dashboard tooling. */
  public Robot() {
    // A method reference such as leftDriveMotor::set lets the helper send a computed output
    // to that motor. Register the child objects explicitly when using these setter functions.
    SendableRegistry.addChild(robotDrive, leftDriveMotor);
    SendableRegistry.addChild(robotDrive, rightDriveMotor);
  }

  /** Publishes autonomous choices and configures motor direction once at program startup. */
  @Override
  public void robotInit() {
    autoChooser.setDefaultOption("Default Auto", DEFAULT_AUTO);
    autoChooser.addOption("My Auto", CUSTOM_AUTO);
    SmartDashboard.putData("Auto choices", autoChooser);

    // Preserve this robot's existing left-side inversion; the right side stays uninverted.
    // Electrical sign alone does not tell us wheel direction without knowing the drivetrain.
    leftDriveMotor.setInverted(true);
  }

  /**
   * Reads driver input and updates arcade drive each loop, after the mode-specific periodic method.
   *
   * <p>For example, left Y = -0.5 and right X = 0.0 become forward = 0.3 and turn = 0.0. The
   * two-argument {@code arcadeDrive} then applies its default deadband and sign-preserving input
   * squaring, so these inputs are not the final PWM outputs.
   *
   * <p>This callback also runs while disabled; the roboRIO inhibits physical motor output in that
   * state. Enabled autonomous and test still receive controller requests. Keep this placement for a
   * behavior-preserving refactor; mode-specific driving is a separate feature change.
   */
  @Override
  public void robotPeriodic() {
    // Keep both signs, the 0.6 scale, and the default arcade-drive input shaping unchanged.
    double forwardInput = -driverController.getLeftY() * DRIVE_INPUT_SCALE;
    double turnInput = -driverController.getRightX() * DRIVE_INPUT_SCALE;
    robotDrive.arcadeDrive(forwardInput, turnInput);
  }

  /**
   * Saves and logs the dashboard choice each time autonomous begins.
   *
   * <p>The visible "Default Auto" label supplies the value "Default". A future routine needs both a
   * chooser option in {@link #robotInit()} and a matching case in {@link #autonomousPeriodic()}.
   */
  @Override
  public void autonomousInit() {
    selectedAuto = autoChooser.getSelected();
    System.out.println("Auto selected: " + selectedAuto);
  }

  /**
   * Dispatches the saved autonomous selection; both routines are currently placeholders.
   *
   * <p>Neither branch commands a motor. Controller driving still runs in {@link #robotPeriodic()}
   * afterward, so adding autonomous motor commands here requires a separate mode-control design.
   */
  @Override
  public void autonomousPeriodic() {
    switch (selectedAuto) {
      case CUSTOM_AUTO:
        // Reserved for a future custom routine; intentionally no action today.
        break;
      case DEFAULT_AUTO:
      default:
        // Default and unrecognized non-null values take the same no-action path.
        break;
    }
  }

  // Keep the empty lifecycle overrides as student extension points. Some inherited periodic
  // implementations print a first-call message; deleting these would change console behavior.

  /** Runs once on teleop entry; no additional setup is currently needed. */
  @Override
  public void teleopInit() {}

  /** Runs each teleop loop; the existing drive update lives in {@link #robotPeriodic()}. */
  @Override
  public void teleopPeriodic() {}

  /** Runs on disabled entry; this program has no additional state to reset. */
  @Override
  public void disabledInit() {}

  /** Runs each disabled loop; no extra disabled-mode work is currently configured. */
  @Override
  public void disabledPeriodic() {}

  /** Runs on test-mode entry; no custom test routine is currently configured. */
  @Override
  public void testInit() {}

  /** Runs each test loop; controller driving still occurs in {@link #robotPeriodic()}. */
  @Override
  public void testPeriodic() {}

  /** Runs once after robot initialization in simulation; no physics model is configured. */
  @Override
  public void simulationInit() {}

  /** Runs each simulation loop; this program does not calculate simulated sensor readings. */
  @Override
  public void simulationPeriodic() {}
}
