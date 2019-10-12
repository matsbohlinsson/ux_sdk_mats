package com.dji.ux.sample.functions;

import java.util.Timer;
import java.util.TimerTask;

import dji.common.error.DJIError;
import dji.common.flightcontroller.virtualstick.FlightControlData;
import dji.common.flightcontroller.virtualstick.FlightCoordinateSystem;
import dji.common.flightcontroller.virtualstick.RollPitchControlMode;
import dji.common.flightcontroller.virtualstick.VerticalControlMode;
import dji.common.util.CommonCallbacks;
import dji.sdk.flightcontroller.FlightController;

public class MyVirtualStick {
    private updateVirtualStick updateVirtualStickTask;
    private Timer updateVirtualStickTaskTimer;
    public static int ANGLE = 1;
    public static int VELOCITY = 1;
    int mode = 0;
    float x=0,y=0,z=0,yaw=0;

    final FlightController flightController = Common.getFlightController();

    public MyVirtualStick(int ctrl_mode){
        z=Common.getAircraftLocation().getAltitude();
        setCtrlMode(ctrl_mode);
    }
    private class updateVirtualStick extends TimerTask {
        public void run() {
            Common.getFlightController().sendVirtualStickFlightControlData(
                    new FlightControlData(x,y,yaw,z),
                    new CommonCallbacks.CompletionCallback() {
                        @Override
                        public void onResult(DJIError djiError) {
                        }
                    });
        }
    }

    void setCtrlMode(int ctrlMode) {
        mode = ctrlMode;
        x=0;
        y=0;
        this.setCtrlMode();
    }
    void setCtrlMode(){
        if (mode==ANGLE) {
            flightController.setRollPitchControlMode(RollPitchControlMode.ANGLE);
            flightController.setRollPitchCoordinateSystem(FlightCoordinateSystem.BODY);
            flightController.setVerticalControlMode(VerticalControlMode.POSITION);
        }
        else if (mode==VELOCITY) {
            flightController.setRollPitchControlMode(RollPitchControlMode.VELOCITY);
            flightController.setRollPitchCoordinateSystem(FlightCoordinateSystem.GROUND);
            flightController.setVerticalControlMode(VerticalControlMode.POSITION);
            }
        }

    public void setSpeed(float x,float y,float z,float yaw){
        this.x=x;
        this.y=y;
        this.z=z;
        this.yaw=yaw;
    }

    public void start(){
        flightController.setVirtualStickModeEnabled(true, new CommonCallbacks.CompletionCallback() {
            @Override
            public void onResult(DJIError djiError) {
                setCtrlMode();
                startTimerVirtualStick();
            }

        });
    }


    void startTimerVirtualStick() {
        updateVirtualStickTask = new updateVirtualStick();
        updateVirtualStickTaskTimer = new Timer();
        updateVirtualStickTaskTimer.schedule(updateVirtualStickTask, 2000, 200);
    }
}


/*
            if (mode==ANGLE) {
                x=Limits.ROLL_PITCH_CONTROL_MAX_ANGLE;
                y=Limits.ROLL_PITCH_CONTROL_MAX_ANGLE;
                z=2;
            }
            else {
                x=Limits.ROLL_PITCH_CONTROL_MAX_VELOCITY;
                y=Limits.ROLL_PITCH_CONTROL_MAX_VELOCITY;
                z=2;
            }

 */