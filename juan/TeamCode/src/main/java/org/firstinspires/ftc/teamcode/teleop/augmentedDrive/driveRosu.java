package org.firstinspires.ftc.teamcode.teleop.augmentedDrive;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.geometry.Vector2d;
import com.acmerobotics.roadrunner.trajectory.Trajectory;
import com.acmerobotics.roadrunner.util.Angle;
import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.hardware.bosch.JustLoggingAccelerationIntegrator;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.PIDCoefficients;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.navigation.Acceleration;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;
import org.firstinspires.ftc.teamcode.drive.SampleMecanumDrive;
import org.firstinspires.ftc.teamcode.hardware.servo_glisiera;
import org.firstinspires.ftc.teamcode.hardware.servo_outtake1;
import org.firstinspires.ftc.teamcode.hardware.servo_outtake2;
import org.firstinspires.ftc.teamcode.hardware.servo_plug;
import org.firstinspires.ftc.teamcode.hardware.servo_intake;
import org.firstinspires.ftc.teamcode.hardware.servo_wobble1;
import org.firstinspires.ftc.teamcode.hardware.servo_wobble2;
import org.firstinspires.ftc.teamcode.teleop.TuningController;
import org.firstinspires.ftc.teamcode.teleop.VelocityPIDFController;

import static java.lang.Boolean.FALSE;


@TeleOp
//@Disabled
public class driveRosu extends LinearOpMode {

    private double root2 = Math.sqrt(2.0);
    Boolean ok3 = FALSE;
    Boolean ok2 = FALSE;
    Boolean cont = FALSE;
    Boolean contor = FALSE;
    Boolean cont_glisiera = FALSE;
    Boolean cont_glisieraaa = FALSE;

    public static com.acmerobotics.roadrunner.control.PIDCoefficients MOTOR_VELO_PID = new com.acmerobotics.roadrunner.control.PIDCoefficients(0.00038, 0.0000012, 0);

    public static double kV = 1 / TuningController.rpmToTicksPerSecond(TuningController.MOTOR_MAX_RPM);
    public static double kA = 0;
    public static double targetVelo = 0;
    public static double viteza = 1620;
    public static double kStatic = 0;

    public static double POWERSHOT_VELO = 1390;

    private final FtcDashboard dashboard = FtcDashboard.getInstance();

    private final ElapsedTime veloTimer = new ElapsedTime();

    Orientation angles;
    Acceleration gravity;
    double curHeading;


    // Define 2 states, drive control or automatic control
    enum Mode {
        DRIVER_CONTROL,
        AUTOMATIC_CONTROL
    }

    Mode currentMode = Mode.DRIVER_CONTROL;

    // The coordinates we want the bot to automatically go to when we press the A button
    Vector2d targetAVector;
    // The heading we want the bot to end on for targetA
    double targetAHeading;

    // The angle we want to align to when we press Y
    double targetAngle = Math.toRadians(0);

    SampleMecanumDrive drive;

    Vector2d towerVector = new Vector2d(125, 26);

    @Override
    public void runOpMode() {


        // Initialize custom cancelable SampleMecanumDrive class

        DcMotorEx myMotor1 = hardwareMap.get(DcMotorEx.class, "outtake1");
        DcMotorEx myMotor2 = hardwareMap.get(DcMotorEx.class, "outtake2");

        myMotor1.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        myMotor2.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        VelocityPIDFController veloController = new VelocityPIDFController(MOTOR_VELO_PID, kV, kA, kStatic);

        double lastTargetVelo = 0.0;



        DcMotor intake = null;
        intake = hardwareMap.get(DcMotor.class, "intake");
        intake.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        intake.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        intake.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        intake.setDirection(DcMotor.Direction.FORWARD);
        intake.setPower(0.0);



        servo_outtake1 out1 = new servo_outtake1(hardwareMap);
        servo_outtake2 out2 = new servo_outtake2(hardwareMap);
        servo_wobble1 wob_brat = new servo_wobble1(hardwareMap);
        servo_wobble2 wob_cleste = new servo_wobble2(hardwareMap);
        servo_glisiera outg = new servo_glisiera(hardwareMap);
        servo_plug plug = new servo_plug(hardwareMap);
        servo_intake serv_int = new servo_intake(hardwareMap);
        plug.up();
        out1.open();
        out2.open();
        outg.open();
        wob_brat.mid();
        wob_cleste.close();
        serv_int.up();



        drive = new SampleMecanumDrive(hardwareMap);
        drive.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        waitForStart();

        if (isStopRequested()) return;

        while (opModeIsActive() && !isStopRequested()) {


            // Update the drive class
            drive.update();

            // Read pose
            Pose2d poseEstimate = drive.getPoseEstimate();

            // Print pose to telemetry


            // We follow different logic based on whether we are in manual driver control or switch
            // control to the automatic mode
            switch (currentMode) {
                case DRIVER_CONTROL:

                    veloController.setTargetVelocity(targetVelo);
                    veloController.setTargetAcceleration((targetVelo - lastTargetVelo) / veloTimer.seconds());
                    veloTimer.reset();

                    lastTargetVelo = targetVelo;

                    telemetry.addData("targetVelocity", targetVelo);

                    double motorPos = myMotor1.getCurrentPosition();
                    double motorVelo = myMotor1.getVelocity();

                    double power = veloController.update(motorPos, motorVelo);
                    myMotor1.setPower(power);
                    myMotor2.setPower(power);

                    drive.setDrivePower(
                            new Pose2d(
                                    -gamepad1.left_stick_y,
                                    -gamepad1.left_stick_x,
                                    -gamepad1.right_stick_x
                            )
                    );

                    if(gamepad1.y && gamepad1.dpad_up)
                    {
                        resetPositionLine();

                        DcMotorEx finalOuttake1 = myMotor1;
                        DcMotorEx finalOuttake2 = myMotor2;

                        Trajectory trajectory1 = drive.trajectoryBuilder(new Pose2d(63.5, 0, 3.1415))
                                .strafeTo(new Vector2d(56.5, 49))
                                .addTemporalMarker(0.8, () -> {
                                    finalOuttake1.setVelocity(POWERSHOT_VELO);
                                    finalOuttake2.setVelocity(POWERSHOT_VELO);
                                })
                                .build();

                        Trajectory trajectory2 = drive.trajectoryBuilder(trajectory1.end())
                                .strafeTo(new Vector2d(56.5, 54.75))
                                .build();

                        Trajectory trajectory3 = drive.trajectoryBuilder(trajectory2.end())
                                .strafeTo(new Vector2d(56.5, 60))
                                .build();

                        drive.followTrajectory(trajectory1);
                        out1.close();
                        out2.close();
                        sleep(250);
                        outg.cerc1();
                        sleep(250);
                        outg.open();
                        drive.followTrajectory(trajectory2);
                        outg.cerc2();
                        sleep(550);
                        outg.open();
                        drive.followTrajectory(trajectory3);
                        outg.close();
                        sleep(1000);
                        outg.open();
                        sleep(250);
                        out1.open();
                        out2.open();
                    }

                    if(gamepad1.x && gamepad1.dpad_right)
                    {
                        resetPositionLine();

                        DcMotorEx finalOuttake1 = myMotor1;
                        DcMotorEx finalOuttake2 = myMotor2;

                        Trajectory trajectory1 = drive.trajectoryBuilder(new Pose2d(63.5, 0, 3.14159))
                                .lineToSplineHeading(new Pose2d(61, 40, Math.toRadians(184)))
                                .addTemporalMarker(0.1, () -> {
                                    finalOuttake1.setVelocity(-300);
                                    finalOuttake2.setVelocity(-300);
                                    out1.close();
                                    out2.close();
                                })
                                .addTemporalMarker(0.5, () -> {
                                    finalOuttake1.setVelocity(0);
                                    finalOuttake2.setVelocity(0);
                                })
                                .addTemporalMarker(0.9, () -> {
                                    finalOuttake1.setVelocity(POWERSHOT_VELO);
                                    finalOuttake2.setVelocity(POWERSHOT_VELO);
                                })
                                .build();

                        Trajectory trajectory2 = drive.trajectoryBuilder(trajectory1.end())
                                .lineToSplineHeading(new Pose2d(62, 41, Math.toRadians(189)))
                                .build();

                        Trajectory trajectory3 = drive.trajectoryBuilder(trajectory2.end())
                                .lineToSplineHeading(new Pose2d(63, 42, Math.toRadians(194)))
                                .build();

                        drive.followTrajectory(trajectory1);
                        sleep(100);
                        outg.cerc1();
                        sleep(300);
                        outg.open();
                        drive.followTrajectory(trajectory2);
                        sleep(100);
                        outg.cerc2();
                        sleep(500);
                        outg.open();
                        drive.followTrajectory(trajectory3);
                        sleep(100);
                        outg.close();
                        sleep(1050);
                        outg.open();
                        sleep(300);
                        out1.open();
                        out2.open();
                    }

                    if(gamepad1.a)
                        resetPositionCorner();

                    if(gamepad1.b)
                        resetPositionLine();

                    if (gamepad1.right_bumper) {
                        targetAngle = Math.atan2(-poseEstimate.getY() + towerVector.getY(), -poseEstimate.getX() + towerVector.getX());

                        double unghi = Angle.normDelta(targetAngle - poseEstimate.getHeading());

                        if(unghi > Math.PI)
                            unghi = unghi - Math.PI;

                        else if(unghi < Math.PI)
                            unghi = unghi + Math.PI;

                        drive.turnAsync(unghi);

                        currentMode = Mode.AUTOMATIC_CONTROL;
                    }


                    if (gamepad2.left_bumper && !cont_glisiera){
                        out1.close();
                        out2.close();
                        sleep(220);
                        outg.close();
                        cont_glisiera = true;
                    }
                    if (!gamepad2.left_bumper && cont_glisiera){
                        outg.open();
                        sleep(300);
                        out1.open();
                        out2.open();
                        cont_glisiera = false;
                    }


                    if(gamepad2.x){
                        wob_brat.down();
                    }
                    else{
                        wob_brat.up();
                    }
                    if(gamepad2.y){
                        wob_cleste.open();
                    }
                    else{
                        wob_cleste.close();
                    }
                    if(gamepad2.a){
                        plug.down();
                    }
                    if(!gamepad2.a){
                        plug.up();
                    }


                    if (gamepad2.dpad_up && !cont){
                        viteza = viteza + 20;
                        cont = !cont;
                    }
                    if (gamepad2.dpad_down && !contor){
                        viteza = viteza - 20;
                        contor = !contor;
                    }
                    if (!gamepad2.dpad_up){
                        cont = false;
                    }
                    if (!gamepad2.dpad_down){
                        contor = false;
                    }


                    if(gamepad2.right_trigger > 0) {
                        ok2 = !ok2;
                    }
                    else{
                        ok2 = false;
                    }


                    if(gamepad2.right_bumper) {
                        ok3 = true;
                    }
                    else{
                        ok3 = false;
                    }

                    targetVelo = Math.min(gamepad2.left_trigger*2100, viteza);

                    if(ok3){
                        intake.setPower(-0.8);
                    }else{
                        intake.setPower(Math.min(gamepad2.right_trigger, 0.9));
                    }

                    if(gamepad2.right_trigger > 0.1)
                        targetVelo = -300;

                    break;
                case AUTOMATIC_CONTROL:
                    // If x is pressed, we break out of the automatic following
                    if (gamepad1.x) {
                        drive.cancelFollowing();
                        currentMode = Mode.DRIVER_CONTROL;
                    }

                    // If drive finishes its task, cede control to the driver
                    if (!drive.isBusy()) {
                        currentMode = Mode.DRIVER_CONTROL;
                    }
                    break;
            }
            //telemetry.addData("velocity", outtake1.getVelocity());
            //telemetry.addData("velocity", outtake2.getVelocity());
            //telemetry.addData("outtake velocity", HIGH_VELO);
            telemetry.addData("viteza", viteza);
            telemetry.addData("mode", currentMode);
            telemetry.addData("x", poseEstimate.getX());
            telemetry.addData("y", poseEstimate.getY());
            telemetry.addData("heading", Math.toDegrees(poseEstimate.getHeading()));
            telemetry.update();

        }
    }

    void resetPositionCorner()
    {
        drive.setPoseEstimate(new Pose2d(0,0,0));
    }
    void resetPositionLine()
    {
        drive.setPoseEstimate(new Pose2d(63.5,0,3.14159));
    }

}

