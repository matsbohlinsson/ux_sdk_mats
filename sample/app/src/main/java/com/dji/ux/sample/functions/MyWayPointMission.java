package com.dji.ux.sample.functions;

import java.util.ArrayList;
import java.util.List;

import dji.common.error.DJIError;
import dji.common.flightcontroller.LocationCoordinate3D;
import dji.common.mission.waypoint.Waypoint;
import dji.common.mission.waypoint.WaypointAction;
import dji.common.mission.waypoint.WaypointActionType;
import dji.common.mission.waypoint.WaypointMission;
import dji.common.mission.waypoint.WaypointMissionFinishedAction;
import dji.common.mission.waypoint.WaypointMissionFlightPathMode;
import dji.common.mission.waypoint.WaypointMissionGotoWaypointMode;
import dji.common.mission.waypoint.WaypointMissionHeadingMode;
import dji.common.util.CommonCallbacks;
import dji.sdk.mission.MissionControl;
import dji.sdk.mission.waypoint.WaypointMissionOperator;

public class MyWayPointMission {
    private static final double ONE_METER_OFFSET = 0.00000899322;
    private WaypointMission mission;
    private Common common;


    public WaypointMission build(List<LocationCoordinate3D> waypointList, float flySpeed, float flyHeight , float hoverHeight, float gimbalAngle) {
        WaypointMission.Builder builder = new WaypointMission.Builder();
        builder.autoFlightSpeed(flySpeed);
        builder.maxFlightSpeed(15);
        builder.setExitMissionOnRCSignalLostEnabled(false);
        builder.finishedAction(WaypointMissionFinishedAction.CONTINUE_UNTIL_END);
        builder.flightPathMode(WaypointMissionFlightPathMode.CURVED);
        builder.gotoFirstWaypointMode(WaypointMissionGotoWaypointMode.SAFELY);
        builder.headingMode(WaypointMissionHeadingMode.AUTO);
        builder.repeatTimes(1);

        int i=0;
        Waypoint eachWaypoint=null;
        double lat=0, lon=0;
        List<Waypoint> waypointList2 = new ArrayList<>();

        LocationCoordinate3D loc = common.getAircraftLocation();
        lat = loc.getLatitude();
        lon = loc.getLongitude();
        eachWaypoint = new Waypoint(lat, lon, flyHeight);
        eachWaypoint.addAction(new WaypointAction(WaypointActionType.GIMBAL_PITCH, (int)gimbalAngle));
        eachWaypoint.addAction(new WaypointAction(WaypointActionType.START_RECORD, 0));
        waypointList2.add(eachWaypoint);

        for (LocationCoordinate3D coord : waypointList) {
            lat = coord.getLatitude();
            lon = coord.getLongitude();
            eachWaypoint = new Waypoint(lat, lon, flyHeight);
            //eachWaypoint.cornerRadiusInMeters=25;
            waypointList2.add(eachWaypoint);
        }
        Waypoint lastWaypoint = new Waypoint(lat, lon, hoverHeight);
        //lastWaypoint.addAction(new WaypointAction(WaypointActionType.GIMBAL_PITCH, (int)gimbalAngle));
        lastWaypoint.addAction(new WaypointAction(WaypointActionType.STAY, 0));
        waypointList2.add( lastWaypoint   );
        lastWaypoint.addAction(new WaypointAction(WaypointActionType.CAMERA_FOCUS, 0));

        builder.waypointList(waypointList2).waypointCount(waypointList2.size());
        System.out.println("TotalDistance" +  builder.calculateTotalDistance());
        mission = builder.build();
        return mission;
    }

    void run() {
        final WaypointMissionOperator waypointMissionOperator;
        waypointMissionOperator = MissionControl.getInstance().getWaypointMissionOperator();
        waypointMissionOperator.loadMission(mission);
    }

    public void run2() {
        final WaypointMissionOperator waypointMissionOperator = MissionControl.getInstance().getWaypointMissionOperator();
        waypointMissionOperator.uploadMission(new CommonCallbacks.CompletionCallback() {
            @Override
            public void onResult(DJIError djiError) {
                System.out.println("djiError " + djiError);
                if (djiError!=null)
                    waypointMissionOperator.startMission(new CommonCallbacks.CompletionCallback() {
                        @Override
                        public void onResult(DJIError djiError) {
                            System.out.println("djiError2 " + (djiError));
                        }
                    });
            }
        });
    }

}
