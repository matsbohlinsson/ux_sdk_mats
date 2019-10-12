package com.dji.ux.sample.functions;

import android.widget.EditText;

import dji.common.battery.BatteryState;
import dji.common.error.DJIError;
import dji.common.flightcontroller.LocationCoordinate3D;
import dji.keysdk.FlightControllerKey;
import dji.keysdk.GimbalKey;
import dji.keysdk.KeyManager;
import dji.sdk.base.BaseProduct;
import dji.sdk.battery.Battery;
import dji.sdk.flightcontroller.FlightAssistant;
import dji.sdk.flightcontroller.FlightController;
import dji.sdk.flightcontroller.Simulator;
import dji.sdk.mission.MissionControl;
import dji.sdk.mission.followme.FollowMeMissionOperator;
import dji.sdk.products.Aircraft;
import dji.sdk.remotecontroller.RemoteController;
import dji.sdk.sdkmanager.DJISDKManager;

public class Common{
    EditText editText;
    static BatteryState lastBatteryState = null;
    public Common(EditText textbox) {
        editText = textbox;
    }

    public static synchronized BaseProduct getProductInstance() {
        return DJISDKManager.getInstance().getProduct();
    }
    public static synchronized Aircraft getAircraftInstance() {
        return (Aircraft) getProductInstance();
    }
    public static FlightController getFlightController() {
        Aircraft aircraft = getAircraftInstance();
        return aircraft.getFlightController();
    }
    public static FlightAssistant getFlightAssistant() {
        return getAircraftInstance().getFlightController().getFlightAssistant();
    }
    public static RemoteController getRemoteController() {
        Aircraft aircraft = getAircraftInstance();
        return aircraft.getRemoteController();
    }
    public static Battery getBattery() {
        Aircraft aircraft = getAircraftInstance();
        return aircraft.getBattery();
    }


    public static Simulator getSimulator() {
        return getFlightController().getSimulator();
    }

    static FollowMeMissionOperator ffm = null;
    public static FollowMeMissionOperator getFollowMeMissionOperator() {
        if (ffm==null)
            ffm= MissionControl.getInstance().getFollowMeMissionOperator();
        return ffm;
    }

    public void debugPrint(String text) {
        editText.append( text+"\n" );
    }

    public void debugPrint(String text, DJIError djiError) {
        String concat_text="";
        if (djiError!=null) {
            concat_text = "Error: " + djiError.getDescription() + "\n";
        }
        debugPrint( concat_text + text);
    }

    public static LocationCoordinate3D getAircraftLocation() {
        double la = (double) KeyManager.getInstance().getValue(FlightControllerKey.create(FlightControllerKey.AIRCRAFT_LOCATION_LATITUDE));
        double lo = (double) KeyManager.getInstance().getValue(FlightControllerKey.create(FlightControllerKey.AIRCRAFT_LOCATION_LONGITUDE));
        //double height = (double) KeyManager.getInstance().getValue(FlightControllerKey.create(FlightControllerKey.));
        //return DJILocationCoordinate3D
        float altitude = (float) KeyManager.getInstance().getValue(FlightControllerKey.create(FlightControllerKey.ALTITUDE));
        return new LocationCoordinate3D(la,lo,altitude);
    }

    public static void InvertGimbalControl(Boolean inverted) {
        KeyManager.getInstance().setValue(GimbalKey.create(GimbalKey.PITCH_INVERTED_CONTROL_ENABLED), inverted, null);
        KeyManager.getInstance().setValue(GimbalKey.create(GimbalKey.RESET_GIMBAL), inverted, null);
    }

    public static dji.common.battery.BatteryState GetBatteryState() {
        if (lastBatteryState == null)
        try {
            getBattery().setStateCallback(new BatteryState.Callback() {
                @Override
                public void onUpdate(BatteryState djiBatteryState) {
                    lastBatteryState = djiBatteryState;
                }
            });
        } catch (Exception ignored) {
        }
        return lastBatteryState;
    }

}

