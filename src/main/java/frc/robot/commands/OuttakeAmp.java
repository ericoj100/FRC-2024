package frc.robot.commands;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Transform2d;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.DriverStation.Alliance;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;
import edu.wpi.first.wpilibj2.command.PrintCommand;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import edu.wpi.first.wpilibj2.command.WaitCommand;
import edu.wpi.first.wpilibj2.command.WaitUntilCommand;
import frc.robot.constants.ArmConstants;
import frc.robot.constants.ShooterConstants;
import frc.robot.constants.StorageIndexConstants;
import frc.robot.constants.miscConstants.VisionConstants;
import frc.robot.subsystems.Drivetrain;
import frc.robot.subsystems.gpm.Arm;
import frc.robot.subsystems.gpm.Shooter;
import frc.robot.subsystems.gpm.StorageIndex;
import frc.robot.util.Vision;

/**
 * Scores in the amp
 */
public class OuttakeAmp extends SequentialCommandGroup {
  private Pose2d ampPose;
  private Pose2d ampPose2;

  /**
   * Scores in the amp
   * 
   * @param arm     The arm
   * @param index   The indexer
   * @param shooter The shooter
   */
  public OuttakeAmp(Arm arm, StorageIndex index, Shooter shooter) {
    addCommands(
        // Get the shooter to the right speed while moving arm
        new ParallelCommandGroup (new InstantCommand(() -> index.runIndex()),  new InstantCommand(() -> shooter.setTargetRPM(ShooterConstants.AMP_OUTTAKE_RPM), shooter)),
        // Move arm
        new ArmToPos(arm, ArmConstants.ampSetpoint),
        // Score in amp
        new InstantCommand(() -> index.ejectAmpFront(), index),
        // Wait until note is scored
        new WaitCommand(StorageIndexConstants.ejectAmpFrontTimeout),
        // Set everything back to default state
        new InstantCommand(() -> shooter.setTargetRPM(0)),
        new InstantCommand(() -> index.stopIndex()),
        new InstantCommand(() -> arm.setAngle(ArmConstants.stowedSetpoint)));
  }

  /**
   * Aligns to the amp and scores
   * 
   * @param arm     The arm
   * @param index   The indexer
   * @param shooter The shooter
   * @param drive   The drivetrain
   */
  public OuttakeAmp(Arm arm, StorageIndex index, Shooter shooter, Drivetrain drive) {
    addCommands(
      new InstantCommand(()->getPoses()),
      new SequentialCommandGroup(
          // Wait until the robot is close enough to the amp to start scoring
          new WaitUntilCommand((() -> {
            Transform2d dist = drive.getPose().minus(ampPose);
            return dist.getTranslation().getNorm() < VisionConstants.AMP_TOLERANCE_DISTANCE
                && Math.abs(dist.getRotation().getRadians()) < VisionConstants.AMP_TOLERANCE_ANGLE;
          })),
          // Run the other version of this command to score in the amp
          new OuttakeAmp(arm, index, shooter)).deadlineWith(
              // Go to the pose and stay at it until the command finishes
              new SequentialCommandGroup(
                new GoToPose(ampPose2, drive).until(()->{
                  return drive.getPose().getTranslation().getDistance(ampPose2.getTranslation()) < VisionConstants.AMP_TOLERANCE_DISTANCE;
                }),
                new GoToPose(ampPose, drive))
              ));
  }

  public OuttakeAmp(Drivetrain drive){
    addCommands(
      new InstantCommand(()->printcall()),
      new InstantCommand(()->getPoses()),
      new InstantCommand(()->printAmpPose()),
      new GoToPose(VisionConstants.RED_AMP_POSE_3, drive).until(()->{
        return drive.getPose().getTranslation().getDistance(VisionConstants.RED_AMP_POSE_3.getTranslation()) < VisionConstants.AMP_TOLERANCE_DISTANCE;
      }),
      new GoToPose(VisionConstants.RED_AMP_POSE_2, drive).until(()->{
        return drive.getPose().getTranslation().getDistance(VisionConstants.RED_AMP_POSE_2.getTranslation()) < VisionConstants.AMP_TOLERANCE_DISTANCE;
      }),
      new GoToPose(VisionConstants.RED_AMP_POSE, drive)
    );
  }

  public void getPoses(){
    ampPose = DriverStation.getAlliance().get() == Alliance.Red ? VisionConstants.RED_AMP_POSE
        : VisionConstants.BLUE_AMP_POSE;
    ampPose2 = DriverStation.getAlliance().get() == Alliance.Red ? VisionConstants.RED_AMP_POSE_2
        : VisionConstants.BLUE_AMP_POSE_2;
  }

  public void printAmpPose(){
    System.out.println("ampPose: "+ampPose); 
    System.out.println("ampPose2: "+ampPose2); 
 
  }
  public void printcall(){
    System.out.println("HELLOOOOOOOOOOOOOOOOOOOOOO!!!");
    System.out.println("HELLOOOOOOOOOOOOOOOOOOOOOO!!!");
    System.out.println("HELLOOOOOOOOOOOOOOOOOOOOOO!!!");
    System.out.println("HELLOOOOOOOOOOOOOOOOOOOOOO!!!");
    System.out.println("HELLOOOOOOOOOOOOOOOOOOOOOO!!!");
    System.out.println("HELLOOOOOOOOOOOOOOOOOOOOOO!!!");
    System.out.println("HELLOOOOOOOOOOOOOOOOOOOOOO!!!");
    System.out.println("HELLOOOOOOOOOOOOOOOOOOOOOO!!!");
    System.out.println("HELLOOOOOOOOOOOOOOOOOOOOOO!!!");
    System.out.println("HELLOOOOOOOOOOOOOOOOOOOOOO!!!");
    System.out.println("HELLOOOOOOOOOOOOOOOOOOOOOO!!!");
    System.out.println("HELLOOOOOOOOOOOOOOOOOOOOOO!!!");
    System.out.println("HELLOOOOOOOOOOOOOOOOOOOOOO!!!");
    System.out.println("HELLOOOOOOOOOOOOOOOOOOOOOO!!!");
    System.out.println("HELLOOOOOOOOOOOOOOOOOOOOOO!!!");
    System.out.println("HELLOOOOOOOOOOOOOOOOOOOOOO!!!");
    System.out.println("HELLOOOOOOOOOOOOOOOOOOOOOO!!!");
    System.out.println("HELLOOOOOOOOOOOOOOOOOOOOOO!!!");
    System.out.println("HELLOOOOOOOOOOOOOOOOOOOOOO!!!");
    System.out.println("HELLOOOOOOOOOOOOOOOOOOOOOO!!!");
    System.out.println("HELLOOOOOOOOOOOOOOOOOOOOOO!!!");
    System.out.println("HELLOOOOOOOOOOOOOOOOOOOOOO!!!");

  }
}
