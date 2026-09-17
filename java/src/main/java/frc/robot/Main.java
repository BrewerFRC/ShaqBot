// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.wpilibj.RobotBase;

/**
 * Entry point that hands program startup to WPILib.
 *
 * <p>Keep hardware construction and robot setup in {@link Robot}. WPILib must initialize its
 * runtime before constructing hardware objects; static hardware fields here would run too early.
 */
public final class Main {
  // This class only supplies a static entry point; there is no reason to construct a Main object.
  private Main() {}

  /**
   * Starts WPILib, which constructs the robot and invokes its lifecycle callbacks.
   *
   * <p>{@code Robot::new} is a constructor reference: it supplies a factory rather than
   * constructing the robot immediately. If the robot class is renamed, update that reference. If
   * this entry-point class is renamed or moved, also update {@code ROBOT_MAIN_CLASS} in {@code
   * build.gradle}.
   *
   * @param args command-line arguments; unused by this robot program
   */
  public static void main(String... args) {
    RobotBase.startRobot(Robot::new);
  }
}
