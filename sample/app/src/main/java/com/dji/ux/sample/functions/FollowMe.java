package com.dji.ux.sample.functions;

import android.location.Location;
import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;

import dji.common.error.DJIError;
import dji.common.mission.followme.FollowMeHeading;
import dji.common.mission.followme.FollowMeMission;
import dji.common.mission.followme.FollowMeMissionState;
import dji.common.model.LocationCoordinate2D;
import dji.common.util.CommonCallbacks;
import dji.keysdk.FlightControllerKey;
import dji.keysdk.KeyManager;

import static com.dji.ux.sample.functions.Common.getFollowMeMissionOperator;

public class FollowMe {
    static int nbr=0;
    Common common;
    private Gps gps;

    public FollowMe(Gps gp, Common com) {
        gps = gp;
        common = com;
    }

    public void startFollowMe() {
        common.debugPrint("Start follow me");
        float height = (float) KeyManager.getInstance().getValue(FlightControllerKey.create(FlightControllerKey.ALTITUDE));
        double la = (double) KeyManager.getInstance().getValue(FlightControllerKey.create(FlightControllerKey.AIRCRAFT_LOCATION_LATITUDE));
        double lo = (double) KeyManager.getInstance().getValue(FlightControllerKey.create(FlightControllerKey.AIRCRAFT_LOCATION_LONGITUDE));
        Log.d("la", ""+ la);
        Log.d("lo", "" +lo);
        common.debugPrint("Height:" + height);
        common.debugPrint("lat:" + la);
        common.debugPrint("lon:" + lo);

        if (getFollowMeMissionOperator().getCurrentState() == FollowMeMissionState.READY_TO_EXECUTE) {
            common.debugPrint("READY_TO_EXECUTE" + la +" "+lo);

            FollowMeMission ffm = new FollowMeMission(FollowMeHeading.TOWARD_FOLLOW_POSITION, la, lo, 0);

            getFollowMeMissionOperator().startMission(ffm, new CommonCallbacks.CompletionCallback() {
                @Override
                public void onResult(DJIError error) {
                    common.debugPrint("FollowMeMission onResult", error);
                    startFollowMeTimer();
                }
            });
        } else {
            common.debugPrint("Unable to start mission: Current State not READY_TO_EXECUTE");
        }
    }

        private void startFollowMeTimer() {
            Timer updateFollowMeTaskTimer;
            updateFollowMe updateFollowMeTask;
            updateFollowMeTask = new updateFollowMe();
            updateFollowMeTaskTimer = new Timer();
            Log.d("timer", "schedule");
            updateFollowMeTaskTimer.schedule(updateFollowMeTask, 1000, 200);    }

    private class updateFollowMe extends TimerTask {
        @Override
        public void run() {
            if (getFollowMeMissionOperator().getCurrentState() == FollowMeMissionState.EXECUTING) {
                Location loc = gps.getLatestPosition();
                double la = loc.getLatitude();
                double lo = loc.getLatitude();
                Log.d("Location changed", "Lat: " + la + " Lng: " + lo);
                getFollowMeMissionOperator().updateFollowingTarget(new LocationCoordinate2D(la, lo), new CommonCallbacks.CompletionCallback() {
                    @Override
                    public void onResult(DJIError error) {
                    }
                });
            }
        }
    }
}
