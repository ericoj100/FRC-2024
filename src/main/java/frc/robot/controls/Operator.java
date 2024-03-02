// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.controls;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandScheduler;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.button.Trigger;
import frc.robot.commands.IntakeWithRumble;
import frc.robot.commands.OuttakeAmp;
import frc.robot.commands.Rumbler;
import frc.robot.commands.Shoot;
import frc.robot.commands.gpm.IndexerFeed;
import frc.robot.commands.gpm.IntakeNote;
import frc.robot.commands.gpm.PrepareShooter;
import frc.robot.commands.gpm.ShootKnownPos;
import frc.robot.commands.gpm.ShootKnownPos.ShotPosition;
import frc.robot.constants.ArmConstants;
import frc.robot.constants.Constants;
import frc.robot.subsystems.Drivetrain;
import frc.robot.subsystems.gpm.Arm;
import frc.robot.subsystems.gpm.Intake;
import frc.robot.subsystems.gpm.Shooter;
import frc.robot.subsystems.gpm.Intake.Mode;
import frc.robot.subsystems.gpm.StorageIndex;
import lib.controllers.GameController;
import lib.controllers.GameController.Button;
import lib.controllers.GameController.RumbleStatus;

/** Add your docs here. */
public class Operator {

    private final GameController kDriver = new GameController(Constants.OPERATOR_JOY);

    private Intake intake;
    private Arm arm;
    private StorageIndex index;
    private Shooter shooter;
    private Drivetrain drive;
    
    public Operator(Intake intake, Arm arm, StorageIndex index, Shooter shooter, Drivetrain drive) {
        this.intake = intake;
        this.arm = arm;
        this.index = index;
        this.shooter = shooter;
        this.drive = drive;
    }

    public void configureControls() {
        if (intake != null) {
            Command intakeNote = new IntakeNote(intake, index, arm);
            Command inakeWithRumble =  new IntakeWithRumble(intake, index, arm, (x)->{
                if (x){
                    kDriver.setRumble(RumbleStatus.RUMBLE_ON);
                }
                else{
                    kDriver.setRumble(RumbleStatus.RUMBLE_OFF);
                }
            });
            kDriver.get(Button.X).onTrue(inakeWithRumble);
            //kDriver.get(Button.X).toggleOnTrue(intakeNote);
            kDriver.get(Button.X).onFalse(new InstantCommand(()->inakeWithRumble.cancel()));
            kDriver.get(Button.B).onTrue(new InstantCommand(() -> intake.setMode(Mode.ReverseMotors),intake));
            kDriver.get(Button.B).onFalse(new InstantCommand(() -> intake.setMode(Mode.DISABLED), intake));
        }
        if(index != null){
            kDriver.get(Button.B).onTrue(new InstantCommand(() -> index.ejectBack(), index));
            kDriver.get(Button.B).onFalse(new InstantCommand(() -> index.stopIndex(), index));
        }
        kDriver.get(Button.BACK).onTrue(new InstantCommand(()->{
            if(shooter != null){
                shooter.setTargetRPM(0);
            }
            if(intake != null){
                intake.setMode(Mode.DISABLED);
            }
            if(arm != null){
                arm.setAngle(ArmConstants.stowedSetpoint);
            }
            if(index != null){
                index.stopIndex();
            }
            CommandScheduler.getInstance().cancelAll();
        }));
        if (index != null && arm != null && shooter != null && drive != null) {
            getRightTrigger().onTrue(new Shoot(shooter, arm, drive, index));
        }
        if(shooter != null){
            getLeftTrigger().onTrue(new PrepareShooter(shooter, 1750));
        }
        if(arm != null && shooter != null && index != null){
            kDriver.get(Button.Y).onTrue(new ShootKnownPos(shooter, arm, index, ShotPosition.SUBWOOFER));
            //kDriver.get(Button.Y).onTrue(new IndexerFeed(index));
            kDriver.get(Button.Y).whileTrue(new Rumbler(shooter,(x)->{
                if (x){
                    kDriver.setRumble(RumbleStatus.RUMBLE_ON);
                }
                else{
                    kDriver.setRumble(RumbleStatus.RUMBLE_OFF);
                }
            }));
            // kDriver.get(Button.Y).whileFalse(new Rumbler(shooter,(x)->{
            //     if (x){
            //         kDriver.setRumble(RumbleStatus.RUMBLE_ON);
            //     }
            //     else{
            //         kDriver.setRumble(RumbleStatus.RUMBLE_OFF);
            //     }
            // }));
            kDriver.get(Button.A).onTrue(new OuttakeAmp(arm, index, shooter));
        }
        if(arm != null){
            kDriver.get(Button.RB).onTrue(new InstantCommand(()->arm.setAngle(ArmConstants.preClimbSetpoint), arm));
            kDriver.get(Button.LB).onTrue(new InstantCommand(()->arm.setAngle(ArmConstants.climbSetpoint), arm));
          }
    }
    public Trigger getRightTrigger(){
        return new Trigger(kDriver.RIGHT_TRIGGER_BUTTON);
    }
    public Trigger getLeftTrigger(){
        return new Trigger(kDriver.LEFT_TRIGGER_BUTTON);
    }
}
