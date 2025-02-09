package org.firstinspires.ftc.teamcode.auto;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.geometry.Vector2d;
import com.acmerobotics.roadrunner.trajectory.Trajectory;
import com.acmerobotics.roadrunner.trajectory.constraints.AngularVelocityConstraint;
import com.acmerobotics.roadrunner.trajectory.constraints.MecanumVelocityConstraint;
import com.acmerobotics.roadrunner.trajectory.constraints.MinVelocityConstraint;
import com.acmerobotics.roadrunner.trajectory.constraints.ProfileAccelerationConstraint;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.PIDCoefficients;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.teamcode.drive.DriveConstants;
import org.firstinspires.ftc.teamcode.drive.SampleMecanumDrive;
import org.firstinspires.ftc.teamcode.hardware.servo_glisiera;
import org.firstinspires.ftc.teamcode.hardware.servo_outtake1;
import org.firstinspires.ftc.teamcode.hardware.servo_outtake2;
import org.firstinspires.ftc.teamcode.hardware.servo_plug;
import org.firstinspires.ftc.teamcode.hardware.servo_intake;
import org.firstinspires.ftc.teamcode.hardware.servo_wobble1;
import org.firstinspires.ftc.teamcode.hardware.servo_wobble2;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import org.openftc.easyopencv.OpenCvCamera;
import org.openftc.easyopencv.OpenCvCameraFactory;
import org.openftc.easyopencv.OpenCvCameraRotation;
import org.openftc.easyopencv.OpenCvPipeline;

import java.util.Arrays;

@Autonomous
//@Disabled
public class red_full_mid extends LinearOpMode
{

    public double HIGH_VELO = 1560;
    public double POWERSHOT_VELO = 1380;

    public static double zero = 128;
    public static double unu = 136;
    public static double patru = 143;

    Boolean cont = Boolean.FALSE;
    Boolean contor = Boolean.FALSE;

    OpenCvCamera webcam;
    SkystoneDeterminationPipeline pipeline;

    @Override
    public void runOpMode()
    {

        Gamepad gp1 = gamepad1;

        int cameraMonitorViewId =
                hardwareMap.appContext.getResources().getIdentifier("cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName());
        webcam = OpenCvCameraFactory.getInstance().createWebcam(hardwareMap.get(WebcamName.class, "Webcam 1"), cameraMonitorViewId);
        //int cameraMonitorViewId = hardwareMap.appContext.getResources().getIdentifier("cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName());
        //phoneCam = OpenCvCameraFactory.getInstance().createInternalCamera(OpenCvInternalCamera.CameraDirection.BACK, cameraMonitorViewId);
        pipeline = new SkystoneDeterminationPipeline();
        //phoneCam.setPipeline(pipeline);
        webcam.setPipeline(pipeline);

        // We set the viewport policy to optimized view so the preview doesn't appear 90 deg
        // out when the RC activity is in portrait. We do our actual image processing assuming
        // landscape orientation, though.

        webcam.setViewportRenderingPolicy(OpenCvCamera.ViewportRenderingPolicy.OPTIMIZE_VIEW);

        webcam.openCameraDeviceAsync(new OpenCvCamera.AsyncCameraOpenListener()
        {
            @Override
            public void onOpened()
            {
                webcam.startStreaming(320,240, OpenCvCameraRotation.UPSIDE_DOWN);
            }
        });



        servo_outtake1 out1 = new servo_outtake1(hardwareMap);
        servo_outtake2 out2 = new servo_outtake2(hardwareMap);
        servo_wobble1 wob_brat = new servo_wobble1(hardwareMap);
        servo_wobble2 wob_cleste = new servo_wobble2(hardwareMap);
        servo_glisiera outg = new servo_glisiera(hardwareMap);
        servo_plug plug = new servo_plug(hardwareMap);
        servo_intake serv_int = new servo_intake(hardwareMap);
        serv_int.down();
        plug.up();
        out1.close();
        out2.close();
        outg.open();
        wob_brat.up();
        wob_cleste.close();


        DcMotor intake = null; // Intake motor
        intake = hardwareMap.get(DcMotor.class, "intake");
        intake.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        intake.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        intake.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        intake.setDirection(DcMotor.Direction.FORWARD);
        intake.setPower(0.0);



        DcMotorEx motorOuttake1 = hardwareMap.get(DcMotorEx.class, "outtake1");
        DcMotorEx motorOuttake2 = hardwareMap.get(DcMotorEx.class, "outtake2");

        motorOuttake1.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        motorOuttake2.setMode(DcMotor.RunMode.RUN_USING_ENCODER);


        SampleMecanumDrive drive = new SampleMecanumDrive(hardwareMap);

        DcMotor finalIntake = intake;



        // *****************************  ZERO RINGS  ***************************** \\


        Trajectory trajectory1 = drive.trajectoryBuilder(new Pose2d(), true)
                .splineTo(new Vector2d(-53.5, -2), Math.toRadians(180))
                .addTemporalMarker(1.2, () -> {
                    motorOuttake1.setVelocity(POWERSHOT_VELO);
                    motorOuttake2.setVelocity(POWERSHOT_VELO);
                })
                .build();

        Trajectory trajectory11 = drive.trajectoryBuilder(trajectory1.end(), true)
                //.splineTo(new Vector2d(-53.5, -2), Math.toRadians(186))
                .lineToSplineHeading(new Pose2d(-54, -2.5, Math.toRadians(8)))
                .build();

        Trajectory trajectory111 = drive.trajectoryBuilder(trajectory11.end(), true)
                //.splineTo(new Vector2d(-53.5, -2), Math.toRadians(174))
                .lineToSplineHeading(new Pose2d(-54.5, -3, Math.toRadians(-8)))
                .build();


        Trajectory trajectory2 = drive.trajectoryBuilder(trajectory111.end()) //.plus(new Pose2d(0, 0, Math.toRadians(10))))
                .lineToSplineHeading(new Pose2d(-120, -8, Math.toRadians(130)))
                .addTemporalMarker(0.1, () -> {
                    outg.open();
                    motorOuttake1.setVelocity(0);
                    motorOuttake2.setVelocity(0);
                })
                .addTemporalMarker(0.55, () -> {
                    out1.open();
                    out2.open();
                    plug.down();
                })
                .addTemporalMarker(0.95, () -> {
                    serv_int.up();
                })
                .build();


        Trajectory trajectory3 = drive.trajectoryBuilder(trajectory2.end())
                .lineToSplineHeading(new Pose2d(-119, 22.5, Math.toRadians(140)))
                .splineTo(new Vector2d(-63.5, 34), Math.toRadians(0))
                .addTemporalMarker(0.01, () -> {
                    finalIntake.setPower(0.93);
                    motorOuttake1.setVelocity(-300);
                    motorOuttake2.setVelocity(-300);
                })
                .addTemporalMarker(2.45, () -> {
                    finalIntake.setPower(0);
                    out1.close();
                    out2.close();
                    plug.up();
                })
                .addTemporalMarker(3.8, () -> {
                    motorOuttake1.setVelocity(0);
                    motorOuttake2.setVelocity(0);
                    wob_brat.down();
                })
                .build();


        Trajectory trajectory4 = drive.trajectoryBuilder(trajectory3.end(), true)
                .lineToSplineHeading(new Pose2d(-55, 17.5, Math.toRadians(0)))
                .addTemporalMarker(0.3, () -> {
                    motorOuttake1.setVelocity(HIGH_VELO);
                    motorOuttake2.setVelocity(HIGH_VELO);
                    wob_brat.up();
                    wob_cleste.close();
                })
                .build();

        Trajectory trajectory5 = drive.trajectoryBuilder(trajectory4.end())
                .lineToSplineHeading(new Pose2d(-24, 26, Math.toRadians(-40)))
                .addTemporalMarker(0.2, () -> {
                    motorOuttake1.setVelocity(0);
                    motorOuttake2.setVelocity(0);
                    wob_brat.down();
                    wob_cleste.open();
                    outg.open();
                })
                .addTemporalMarker(0.75, () -> {
                    //out1.open();
                    //out2.open();
                })
                .build();

        Trajectory trajectory6 = drive.trajectoryBuilder(trajectory5.end(), true)
                .splineTo(new Vector2d(-72.5, 33), Math.toRadians(205))
                .build();

        Trajectory trajectory7 = drive.trajectoryBuilder(trajectory6.end())
                .lineToSplineHeading(new Pose2d(-74, -5, Math.toRadians(0)))
                .addTemporalMarker(0.2, () -> {
                    wob_brat.up();
                    wob_cleste.close();
                })
                .build();





        // *****************************  ONE RING  ***************************** \\


        Trajectory trajectoryy1 = drive.trajectoryBuilder(new Pose2d(), true)
                .splineTo(new Vector2d(-53.5, -2), Math.toRadians(180))
                .addTemporalMarker(0.25, () -> {
                    plug.down();
                })
                .addTemporalMarker(0.5, () -> {
                    serv_int.up();
                })
                .addTemporalMarker(0.9, () -> {
                    plug.up();
                })
                .addTemporalMarker(1.2, () -> {
                    motorOuttake1.setVelocity(POWERSHOT_VELO);
                    motorOuttake2.setVelocity(POWERSHOT_VELO);
                })
                .build();


        Trajectory trajectoryy2 = drive.trajectoryBuilder(trajectoryy1.end().plus(new Pose2d(0, 0, Math.toRadians(10))))
                .lineToSplineHeading(new Pose2d(-120, -8, Math.toRadians(130)))
                .addTemporalMarker(0.1, () -> {
                    outg.open();
                    motorOuttake1.setVelocity(0);
                    motorOuttake2.setVelocity(0);
                })
                .addTemporalMarker(0.6, () -> {
                    out1.open();
                    out2.open();
                    plug.down();
                })
                .build();


        Trajectory trajectoryy3 = drive.trajectoryBuilder(trajectoryy2.end())
                .lineToSplineHeading(new Pose2d(-119, 22.5, Math.toRadians(140)))
                .splineTo(new Vector2d(-84, 24), Math.toRadians(180))
                .addTemporalMarker(0.01, () -> {
                    finalIntake.setPower(0.93);
                    motorOuttake1.setVelocity(-300);
                    motorOuttake2.setVelocity(-300);
                })
                .addTemporalMarker(2.45, () -> {
                    finalIntake.setPower(0);
                    out1.close();
                    out2.close();
                    plug.up();
                })
                .addTemporalMarker(3.3, () -> {
                    motorOuttake1.setVelocity(0);
                    motorOuttake2.setVelocity(0);
                    wob_brat.down();
                })
                .build();


        Trajectory trajectoryy4 = drive.trajectoryBuilder(trajectoryy3.end(), true)
                .lineToSplineHeading(new Pose2d(-55, 17.5, Math.toRadians(0)))
                .addTemporalMarker(0.4, () -> {
                    motorOuttake1.setVelocity(HIGH_VELO);
                    motorOuttake2.setVelocity(HIGH_VELO);
                    wob_brat.up();
                    wob_cleste.close();
                })
                .build();

        Trajectory trajectoryy5 = drive.trajectoryBuilder(trajectoryy4.end())
                .lineToSplineHeading(new Pose2d(-22.5, 20, Math.toRadians(-40)))
                .splineToConstantHeading(new Vector2d(-34, 20), Math.toRadians(0),
                        new MinVelocityConstraint(
                                Arrays.asList(
                                        new AngularVelocityConstraint(DriveConstants.MAX_ANG_VEL),
                                        new MecanumVelocityConstraint(13, DriveConstants.TRACK_WIDTH)
                                )
                        ),
                        new ProfileAccelerationConstraint(DriveConstants.MAX_ACCEL)
                )
                .splineTo(new Vector2d(-22.5, 25), Math.toRadians(-40),
                        new MinVelocityConstraint(
                                Arrays.asList(
                                        new AngularVelocityConstraint(DriveConstants.MAX_ANG_VEL),
                                        new MecanumVelocityConstraint(20, DriveConstants.TRACK_WIDTH)
                                )
                        ),
                        new ProfileAccelerationConstraint(DriveConstants.MAX_ACCEL)
                )
                .addTemporalMarker(0.1, () -> {
                    motorOuttake1.setVelocity(0);
                    motorOuttake2.setVelocity(0);
                    wob_brat.down();
                    wob_cleste.open();
                    outg.open();
                })
                .addTemporalMarker(0.6, () -> {
                    out1.open();
                    out2.open();
                    finalIntake.setPower(0.92);
                })
                .addTemporalMarker(3.8, () -> {
                    out1.close();
                    out2.close();
                    finalIntake.setPower(0);
                })
                .build();

        Trajectory trajectoryy6 = drive.trajectoryBuilder(trajectoryy5.end(), true)
                .splineTo(new Vector2d(-54, 18), Math.toRadians(180))
                .addTemporalMarker(0.9, () -> {
                    motorOuttake1.setVelocity(HIGH_VELO);
                    motorOuttake2.setVelocity(HIGH_VELO);
                })
                .build();

        Trajectory trajectoryy7 = drive.trajectoryBuilder(trajectoryy6.end(), true)
                .splineTo(new Vector2d(-83, 24), Math.toRadians(180))
                .addTemporalMarker(0.1, () -> {
                    motorOuttake1.setVelocity(0);
                    motorOuttake2.setVelocity(0);
                })
                .build();

        Trajectory trajectoryy8 = drive.trajectoryBuilder(trajectoryy7.end())
                .splineTo(new Vector2d(-73, 24), Math.toRadians(0))
                .addTemporalMarker(0.2, () -> {
                    wob_brat.up();
                    wob_cleste.close();
                })
                .build();



        // *****************************  FOUR RINGS  ***************************** \\


        /*
        Trajectory trajectoryyy1 = drive.trajectoryBuilder(new Pose2d())
                .strafeTo(new Vector2d(-52.5, -0.5))
                .addTemporalMarker(0.5, () -> {
                    finalOuttake.setVelocity(POWERSHOT_VELO);
                })
                .build();


        Trajectory trajectoryyy2 = drive.trajectoryBuilder(trajectoryyy1.end().plus(new Pose2d(0, 0, Math.toRadians(17.5))), true)
                .splineToSplineHeading(new Pose2d(-112, 35, Math.toRadians(30)), Math.toRadians(0))
                .addTemporalMarker(1.6, () -> {
                    wob_brat.down();
                    outg.open();
                    finalOuttake.setVelocity(0);
                })
                .build();

        Trajectory trajectoryyy3 = drive.trajectoryBuilder(trajectoryyy2.end())
                .splineToSplineHeading(new Pose2d(-52, 21.75, Math.toRadians(0)), Math.toRadians(-30))
                .splineToConstantHeading(new Vector2d(-40.5, 21.75), Math.toRadians(0),
                        new MinVelocityConstraint(
                                Arrays.asList(
                                        new AngularVelocityConstraint(DriveConstants.MAX_ANG_VEL),
                                        new MecanumVelocityConstraint(12, DriveConstants.TRACK_WIDTH)
                                )
                        ),
                        new ProfileAccelerationConstraint(DriveConstants.MAX_ACCEL)
                )
                .addTemporalMarker(0.32, () -> {
                    plug.down();
                    wob_cleste.close();
                    out1.open();
                    out2.open();
                    finalOuttake.setVelocity(-450);
                    finalIntake.setPower(0.95);
                })
                .build();

        Trajectory trajectoryyy4 = drive.trajectoryBuilder(trajectoryyy3.end())
                .splineToConstantHeading(new Vector2d(-23.25, 20), Math.toRadians(0),
                        new MinVelocityConstraint(

                                Arrays.asList(
                                        new AngularVelocityConstraint(DriveConstants.MAX_ANG_VEL),
                                        new MecanumVelocityConstraint(11, DriveConstants.TRACK_WIDTH)
                                )
                        ),
                        new ProfileAccelerationConstraint(DriveConstants.MAX_ACCEL)
                )
                .splineToConstantHeading(new Vector2d(-19, 10), Math.toRadians(0),
                        new MinVelocityConstraint(
                                Arrays.asList(
                                        new AngularVelocityConstraint(DriveConstants.MAX_ANG_VEL),
                                        new MecanumVelocityConstraint(30, DriveConstants.TRACK_WIDTH)
                                )
                        ),
                        new ProfileAccelerationConstraint(DriveConstants.MAX_ACCEL)
                )
                .splineToConstantHeading(new Vector2d(-10.25, 20.75), Math.toRadians(0),
                        new MinVelocityConstraint(
                                Arrays.asList(
                                        new AngularVelocityConstraint(DriveConstants.MAX_ANG_VEL),
                                        new MecanumVelocityConstraint(15, DriveConstants.TRACK_WIDTH)
                                )
                        ),
                        new ProfileAccelerationConstraint(DriveConstants.MAX_ACCEL)
                )
                .addTemporalMarker(0.01, () -> {
                    finalIntake.setPower(0.95);
                    finalOuttake.setVelocity(-200);
                })
                .addTemporalMarker(2.05, () -> {
                    finalIntake.setPower(0);
                    out1.close();
                    out2.close();
                })
                .build();


        Trajectory trajectoryyy6 = drive.trajectoryBuilder(trajectoryyy4.end())
                .strafeTo(new Vector2d(-47, 27.5))
                .addTemporalMarker(0.02, () -> {
                    finalOuttake.setVelocity(HIGH_VELO+50);
                })
                .build();

        Trajectory trajectoryyy7 = drive.trajectoryBuilder(trajectoryyy6.end())
                .strafeTo(new Vector2d(-118, 30))
                .addTemporalMarker(0.2, () -> {
                    finalOuttake.setVelocity(0);
                    outg.open();
                })
                .addTemporalMarker(1.9, () -> {
                    out1.open();
                    out2.open();
                    wob_cleste.open();
                })
                .build();

        Trajectory trajectoryyy8 = drive.trajectoryBuilder(trajectoryyy7.end())
                .strafeTo(new Vector2d(-70, 30))
                .build();

         */


        while(!isStarted())
        {
            telemetry.addData("Analysis", pipeline.avg1);
            telemetry.addData("Position", pipeline.position);
            telemetry.addData("press x for 0 -", zero);
            telemetry.addData("press y for 1 -", unu);
            telemetry.addData("press a for 4 -", patru);
            telemetry.update();

            if(gp1.x)
            {
                while(gp1.b == false)
                {
                    telemetry.addData("value for 0 -", zero);
                    telemetry.addData("press dpad up to increase by", 1);
                    telemetry.addData("press dpad down to decrease by", 1);
                    telemetry.addData("press b to save and exit", "");
                    telemetry.update();

                    if (gp1.dpad_up && !cont){
                        zero = zero + 1;
                        cont = !cont;
                    }
                    if (gp1.dpad_down && !contor){
                        zero = zero - 1;
                        contor = !contor;
                    }
                    if (!gp1.dpad_up){
                        cont = false;
                    }
                    if (!gp1.dpad_down){
                        contor = false;
                    }
                }
            }

            if(gp1.y)
            {
                while(gp1.b == false)
                {
                    telemetry.addData("value for 1 -", unu);
                    telemetry.addData("press dpad up to increase by", 1);
                    telemetry.addData("press dpad down to decrease by", 1);
                    telemetry.addData("press b to save and exit", "");
                    telemetry.update();

                    if (gp1.dpad_up && !cont){
                        unu = unu + 1;
                        cont = !cont;
                    }
                    if (gp1.dpad_down && !contor){
                        unu = unu - 1;
                        contor = !contor;
                    }
                    if (!gp1.dpad_up){
                        cont = false;
                    }
                    if (!gp1.dpad_down){
                        contor = false;
                    }
                }
            }

            if(gp1.a)
            {
                while(gp1.b == false)
                {
                    telemetry.addData("value for 4 -", patru);
                    telemetry.addData("press dpad up to increase by", 1);
                    telemetry.addData("press dpad down to decrease by", 1);
                    telemetry.addData("press b to save and exit", "");
                    telemetry.update();

                    if (gp1.dpad_up && !cont){
                        patru = patru + 1;
                        cont = !cont;
                    }
                    if (gp1.dpad_down && !contor){
                        patru = patru - 1;
                        contor = !contor;
                    }
                    if (!gp1.dpad_up){
                        cont = false;
                    }
                    if (!gp1.dpad_down){
                        contor = false;
                    }
                }
            }
        }

        waitForStart();


        while (opModeIsActive())
        {
            out1.close();
            out2.close();
            wob_brat.mid();
            if(pipeline.zona == 0)
            {

                drive.followTrajectory(trajectory1);
                outg.cerc1();
                sleep(450);
                outg.open();
                drive.followTrajectory(trajectory11);
                outg.cerc2();
                sleep(650);
                outg.open();
                drive.followTrajectory(trajectory111);
                outg.close();
                sleep(850);

                /*
                drive.followTrajectory(trajectory1);
                outg.cerc1();
                sleep(450);
                outg.open();
                drive.turn(Math.toRadians(-11));
                sleep(400);
                outg.cerc2();
                sleep(650);
                outg.open();
                drive.turn(Math.toRadians(20));
                sleep(400);
                outg.close();
                sleep(850);

                 */

                drive.followTrajectory(trajectory2);
                drive.followTrajectory(trajectory3);
                wob_cleste.open();
                sleep(350);
                drive.followTrajectory(trajectory4);
                outg.close();
                sleep(700);

                drive.followTrajectory(trajectory5);
                wob_cleste.close();
                sleep(470);
                drive.followTrajectory(trajectory6);
                wob_cleste.open();
                sleep(350);
                drive.followTrajectory(trajectory7);

            }

            else if(pipeline.zona == 1)
            {
                drive.followTrajectory(trajectoryy1);
                outg.cerc1();
                sleep(450);
                outg.open();
                drive.turn(Math.toRadians(-11));
                sleep(400);
                outg.cerc2();
                sleep(650);
                outg.open();
                drive.turn(Math.toRadians(20));
                sleep(400);
                outg.close();
                sleep(850);

                drive.followTrajectory(trajectoryy2);
                drive.followTrajectory(trajectoryy3);
                wob_cleste.open();
                sleep(350);
                drive.followTrajectory(trajectoryy4);
                outg.close();
                sleep(950);

                drive.followTrajectory(trajectoryy5);
                wob_cleste.close();
                sleep(470);
                drive.followTrajectory(trajectoryy6);
                outg.close();
                sleep(950);
                outg.open();
                drive.followTrajectory(trajectoryy7);
                wob_cleste.open();
                sleep(350);
                drive.followTrajectory(trajectoryy8);
            }

            else if(pipeline.zona == 4)
            {
                /*
                drive.followTrajectory(trajectoryyy1);
                outg.cerc1();
                sleep(340);
                drive.turn(Math.toRadians(-10));
                outg.cerc2();
                sleep(350);
                drive.turn(Math.toRadians(19.75));
                outg.close();
                sleep(340);

                drive.followTrajectory(trajectoryyy2);
                wob_cleste.open();
                drive.followTrajectory(trajectoryyy3);
                out1.close();
                out2.close();
                intake.setPower(0);
                outtake.setVelocity(HIGH_VELO-40);
                ///drive.turn(Math.toRadians(-5));
                sleep(900);
                outg.close();
                sleep(700);
                outtake.setVelocity(0);
                outg.open();
                wob_brat.down();
                wob_cleste.open();
                sleep(250);
                out1.open();
                out2.open();

                drive.followTrajectory(trajectoryyy4);
                //drive.followTrajectory(trajectoryyy5);
                wob_cleste.close();
                sleep(250);
                drive.followTrajectory(trajectoryyy6);
                intake.setPower(0);
                out1.close();
                out2.close();
                sleep(750);
                outg.close();
                sleep(250);
                outg.open();
                sleep(100);
                outg.close();
                sleep(500);
                drive.followTrajectory(trajectoryyy7);
                //drive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
                drive.followTrajectory(trajectoryyy8);

                 */
            }

            out1.close();
            out2.close();
            outg.open();
            stop();

        }


    }

    public static class SkystoneDeterminationPipeline extends OpenCvPipeline
    {
        /*
         * An enum to define the skystone position
         */
        public enum RingPosition
        {
            FOUR,
            ONE,
            NONE
        }

        public double zona = 4;

        /*
         * Some color constants
         */
        static final Scalar BLUE = new Scalar(0, 0, 255);
        static final Scalar GREEN = new Scalar(0, 255, 0);

        /*
         * The core values which define the location and size of the sample regions
         */
        static final Point REGION1_TOPLEFT_ANCHOR_POINT = new Point(260,40);

        static final int REGION_WIDTH = 35;
        static final int REGION_HEIGHT = 30;

        final int FOUR_RING_THRESHOLD = (int) Math.round((patru+unu)/2);
        final int ONE_RING_THRESHOLD = (int) Math.round((zero+unu)/2);

        Point region1_pointA = new Point(
                REGION1_TOPLEFT_ANCHOR_POINT.x,
                REGION1_TOPLEFT_ANCHOR_POINT.y);
        Point region1_pointB = new Point(
                REGION1_TOPLEFT_ANCHOR_POINT.x + REGION_WIDTH,
                REGION1_TOPLEFT_ANCHOR_POINT.y + REGION_HEIGHT);

        /*
         * Working variables
         */
        Mat region1_Cb;
        Mat YCrCb = new Mat();
        Mat Cb = new Mat();
        int avg1;

        // Volatile since accessed by OpMode thread w/o synchronization
        private volatile RingPosition position = RingPosition.FOUR;

        /*
         * This function takes the RGB frame, converts to YCrCb,
         * and extracts the Cb channel to the 'Cb' variable
         */
        void inputToCb(Mat input)
        {
            Imgproc.cvtColor(input, YCrCb, Imgproc.COLOR_RGB2YCrCb);
            Core.extractChannel(YCrCb, Cb, 1);
        }

        @Override
        public void init(Mat firstFrame)
        {
            inputToCb(firstFrame);

            region1_Cb = Cb.submat(new Rect(region1_pointA, region1_pointB));
        }

        @Override
        public Mat processFrame(Mat input)
        {
            inputToCb(input);

            avg1 = (int) Core.mean(region1_Cb).val[0];

            Imgproc.rectangle(
                    input, // Buffer to draw on
                    region1_pointA, // First point which defines the rectangle
                    region1_pointB, // Second point which defines the rectangle
                    BLUE, // The color the rectangle is drawn in
                    -5); // Thickness of the rectangle lines

            position = RingPosition.ONE; // Record our analysis
            if(avg1 > FOUR_RING_THRESHOLD){
                position = RingPosition.FOUR;
                zona = 4;
            }else if (avg1 > ONE_RING_THRESHOLD){
                position = RingPosition.ONE;
                zona = 1;
            }else{
                position = RingPosition.NONE;
                zona = 0;
            }

            Imgproc.rectangle(
                    input, // Buffer to draw on
                    region1_pointA, // First point which defines the rectangle
                    region1_pointB, // Second point which defines the rectangle
                    GREEN, // The color the rectangle is drawn in
                    -5); // Negative thickness means solid fill

            return input;
        }

        public int getAnalysis()
        {
            return avg1;
        }
    }
}