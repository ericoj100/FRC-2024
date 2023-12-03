package frc.robot.commands.drive_comm;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.subsystems.drive.Drivetrain;

import java.util.function.Supplier;

/**
 * Runs the chassis PIDs to move the robot to a specific pose.
 */
public class GoToPosePID extends CommandBase {

    private final Drivetrain drive;

    private final Supplier<Pose2d> pose;

    /**
     * Runs the chassis PIDs to move the robot to a specific pose.
     */
    public GoToPosePID(Supplier<Pose2d> pose, Drivetrain drive) {
        this.drive = drive;
        this.pose = pose;

        addRequirements(drive);
    }

    @Override
    public void execute() {
        drive.driveWithPID(pose.get().getX(), pose.get().getY(), pose.get().getRotation().getRadians());
    }

    @Override
    public boolean isFinished() {
        // TODO: 2024, create instances of the PID controllers in this class
        // TODO: the current PID values don't allow the command to finish 2023
        return drive.getXController().atSetpoint() && drive.getYController().atSetpoint() && drive.getRotationController().atSetpoint();
    }

    @Override
    public void end(boolean interrupted) {
        drive.stop();
    }
}