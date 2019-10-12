package com.dji.ux.sample.functions;

import android.content.Context;

import androidx.annotation.NonNull;
import dji.common.Stick;
import dji.common.remotecontroller.HardwareState;
import dji.sdk.remotecontroller.RemoteController;

public class MySticks {
    private RemoteController remoteController;
    Stick rightStick;
    Stick leftStick;
    Context context;
    HardwareState.HardwareStateCallback hardwareStateCallback;

    public MySticks(Context context) {
        this.context = context;
        this.init();
    }

    public MySticks() {
        this.init();
    }

    public void setCallback(HardwareState.HardwareStateCallback hardwareStateCallback) {
        this.hardwareStateCallback = hardwareStateCallback;
    }

    private void init(){
        remoteController = Common.getRemoteController();
        remoteController.setHardwareStateCallback(new HardwareState.HardwareStateCallback() {
            @Override
            public void onUpdate(@NonNull HardwareState rcHardwareState) {
                rightStick = rcHardwareState.getRightStick();
                leftStick = rcHardwareState.getLeftStick();
                if (hardwareStateCallback!=null)
                    hardwareStateCallback.onUpdate(rcHardwareState);
            }
        });
    }


    public Stick getRightStick(){
        return rightStick;
    }
    public Stick getLeftStick(){
        return leftStick;
    }


}

