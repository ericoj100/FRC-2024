package frc.robot.controls;

import edu.wpi.first.math.MathUtil;
import edu.wpi.first.networktables.GenericEntry;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import frc.robot.constants.miscConstants.OIConstants;
import frc.robot.subsystems.Drivetrain.DriveConstants;
import frc.robot.subsystems.Drivetrain.DrivetrainImpl;
import frc.robot.util.DynamicSlewRateLimiter;
import frc.robot.util.MathUtils;

/**
 * Abstract class for different controller types.
 */
public abstract class BaseDriverConfig {

    private final DrivetrainImpl drive;

    private final boolean shuffleboardUpdates;

    private final ShuffleboardTab controllerTab;
    private GenericEntry translationalSensitivityEntry, translationalExpoEntry, translationalDeadbandEntry, translationalSlewrateEntry;
    private GenericEntry rotationSensitivityEntry, rotationExpoEntry, rotationDeadbandEntry, rotationSlewrateEntry;
    private GenericEntry headingSensitivityEntry, headingExpoEntry, headingDeadbandEntry;

    // Some of these are not currently used, but we might want them later
    @SuppressWarnings("unused")
    private double translationalSensitivity = OIConstants.TRANSLATIONAL_SENSITIVITY;
    @SuppressWarnings("unused")
    private double translationalExpo = OIConstants.TRANSLATIONAL_EXPO;
    @SuppressWarnings("unused")
    private double translationalDeadband = OIConstants.TRANSLATIONAL_DEADBAND;
    private double translationalSlewrate = OIConstants.TRANSLATIONAL_SLEWRATE;

    @SuppressWarnings("unused")
    private double rotationSensitivity = OIConstants.ROTATION_SENSITIVITY;
    @SuppressWarnings("unused")
    private double rotationExpo = OIConstants.ROTATION_EXPO;
    @SuppressWarnings("unused")
    private double rotationDeadband = OIConstants.ROTATION_DEADBAND;
    private double rotationSlewrate = OIConstants.ROTATION_SLEWRATE;

    private double headingSensitivity = OIConstants.HEADING_SENSITIVITY;
    private double headingExpo = OIConstants.HEADING_EXPO;
    private double headingDeadband = OIConstants.HEADING_DEADBAND;
    private double previousHeading = 0;

    @SuppressWarnings("unused")
    private final DynamicSlewRateLimiter xSpeedLimiter = new DynamicSlewRateLimiter(translationalSlewrate);
    @SuppressWarnings("unused")
    private final DynamicSlewRateLimiter ySpeedLimiter = new DynamicSlewRateLimiter(translationalSlewrate);
    @SuppressWarnings("unused")
    private final DynamicSlewRateLimiter rotLimiter = new DynamicSlewRateLimiter(rotationSlewrate);
    private final DynamicSlewRateLimiter headingLimiter = new DynamicSlewRateLimiter(headingSensitivity);

    /**
     * @param drive               the drivetrain instance
     * @param controllerTab       the shuffleboard controller tab
     * @param shuffleboardUpdates whether to update the shuffleboard
     */
    public BaseDriverConfig(DrivetrainImpl drive, ShuffleboardTab controllerTab, boolean shuffleboardUpdates) {
        headingLimiter.setContinuousLimits(-Math.PI, Math.PI);
        headingLimiter.enableContinuous(true);
        this.controllerTab = controllerTab;
        this.shuffleboardUpdates = shuffleboardUpdates;
        this.drive = drive;
    }

    public double getForwardTranslation() {
        return -MathUtils.expoMS(MathUtil.applyDeadband(getRawForwardTranslation(), OIConstants.DEADBAND), 2) * DriveConstants.kMaxSpeed * 1;
    }

    public double getSideTranslation() {
        return -MathUtils.expoMS(MathUtil.applyDeadband(getRawSideTranslation(), OIConstants.DEADBAND), 2) * DriveConstants.kMaxSpeed * 1;
    }

    public double getRotation() {
        return -MathUtils.expoMS(MathUtil.applyDeadband(getRawRotation(), OIConstants.DEADBAND), 2) * DriveConstants.kMaxAngularSpeed * 1;
    }

    public double getHeading() {
        if (getRawHeadingMagnitude() <= headingDeadband) return headingLimiter.calculate(previousHeading, 1e-6);
        previousHeading = headingLimiter.calculate(getRawHeadingAngle(), MathUtils.expoMS(getRawHeadingMagnitude(), headingExpo) * headingSensitivity);
        return previousHeading;
    }

    protected DrivetrainImpl getDrivetrain() {
        return drive;
    }


    /**
     * Sets up shuffleboard values for the controller.
     */
    public void setupShuffleboard() {
        if (!shuffleboardUpdates) return;

        translationalSensitivityEntry = controllerTab.add("translationalSensitivity", OIConstants.TRANSLATIONAL_SENSITIVITY).getEntry();
        translationalExpoEntry = controllerTab.add("translationalExpo", OIConstants.TRANSLATIONAL_EXPO).getEntry();
        translationalDeadbandEntry = controllerTab.add("translationalDeadband", OIConstants.TRANSLATIONAL_DEADBAND).getEntry();
        translationalSlewrateEntry = controllerTab.add("translationalSlewrate", OIConstants.TRANSLATIONAL_SLEWRATE).getEntry();
        rotationSensitivityEntry = controllerTab.add("rotationSensitivity", OIConstants.ROTATION_SENSITIVITY).getEntry();
        rotationExpoEntry = controllerTab.add("rotationExpo", OIConstants.ROTATION_EXPO).getEntry();
        rotationDeadbandEntry = controllerTab.add("rotationDeadband", OIConstants.ROTATION_DEADBAND).getEntry();
        rotationSlewrateEntry = controllerTab.add("rotationSlewrate", OIConstants.ROTATION_SLEWRATE).getEntry();
        headingSensitivityEntry = controllerTab.add("headingSensitivity", OIConstants.HEADING_SENSITIVITY).getEntry();
        headingExpoEntry = controllerTab.add("headingExpo", OIConstants.HEADING_EXPO).getEntry();
        headingDeadbandEntry = controllerTab.add("headingDeadband", OIConstants.HEADING_DEADBAND).getEntry();
    }

    /**
     * Updates the controller settings from shuffleboard.
     */
    public void updateSettings() { //updates the shuffleboard data
        if (!shuffleboardUpdates) return;

        translationalSensitivity = translationalSensitivityEntry.getDouble(OIConstants.TRANSLATIONAL_SENSITIVITY);
        translationalExpo = translationalExpoEntry.getDouble(OIConstants.TRANSLATIONAL_EXPO);
        translationalDeadband = translationalDeadbandEntry.getDouble(OIConstants.TRANSLATIONAL_DEADBAND);
        translationalSlewrate = translationalSlewrateEntry.getDouble(OIConstants.TRANSLATIONAL_SLEWRATE);

        rotationSensitivity = rotationSensitivityEntry.getDouble(OIConstants.ROTATION_SENSITIVITY);
        rotationExpo = rotationExpoEntry.getDouble(OIConstants.ROTATION_EXPO);
        rotationDeadband = rotationDeadbandEntry.getDouble(OIConstants.ROTATION_DEADBAND);
        rotationSlewrate = rotationSlewrateEntry.getDouble(OIConstants.ROTATION_SLEWRATE);

        headingSensitivity = headingSensitivityEntry.getDouble(OIConstants.HEADING_SENSITIVITY);
        headingExpo = headingExpoEntry.getDouble(OIConstants.HEADING_EXPO);
        headingDeadband = headingDeadbandEntry.getDouble(OIConstants.HEADING_DEADBAND);
    }

    /**
     * Configures the controls for the controller.
     */
    public abstract void configureControls();

    public abstract double getRawSideTranslation();

    public abstract double getRawForwardTranslation();

    public abstract double getRawRotation();

    public abstract double getRawHeadingAngle();

    public abstract double getRawHeadingMagnitude();

    public abstract boolean getIsSlowMode();

    public abstract boolean getIsAlign();
}