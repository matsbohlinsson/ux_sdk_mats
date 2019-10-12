package com.dji.ux.sample.functions;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import dji.common.error.DJIError;
import dji.common.flightcontroller.LocationCoordinate3D;
import dji.common.gimbal.Attitude;
import dji.common.gimbal.Rotation;
import dji.common.mission.waypoint.WaypointMission;
import dji.common.util.CommonCallbacks;
import dji.sdk.flightcontroller.FlightAssistant;
import dji.sdk.flightcontroller.FlightController;
import dji.sdk.mission.MissionControl;
import dji.sdk.mission.timeline.TimelineElement;
import dji.sdk.mission.timeline.TimelineEvent;
import dji.sdk.mission.timeline.TimelineMission;
import dji.sdk.mission.timeline.actions.GimbalAttitudeAction;

public class TimeLineMission {
    private MissionControl missionControl;
    private FlightController flightController;
    private TimelineEvent preEvent;
    private TimelineElement preElement;
    public static final double ONE_METER_OFFSET = 0.00000899322;
    Context context;

    public TimeLineMission(Context contxt)
    {
        context=contxt;
    }

    public void run(List<LocationCoordinate3D> waypointList, float flySpeed, float flyHeight, float hoverHeight, float gimbalAngle, long delayTime, MissionControl.Listener listener) {
        List<TimelineElement> elements = new ArrayList<>();
        missionControl = MissionControl.getInstance();


        System.out.println("QQQQtrace " + flyHeight + "," + hoverHeight + "," + gimbalAngle + "," + delayTime + "," + listener) ;
        FlightAssistant flightAssistant = new FlightAssistant();
        flightAssistant.setPrecisionLandingEnabled(true,new CommonCallbacks.CompletionCallback() {
            @Override
            public void onResult(DJIError djiError) {
                if (djiError!=null)
                System.out.println("djiError " + djiError);
            }
        });

        elements.add(new myMissionAction(delayTime, context));
        //elements.add(new TakeOffAction());
        MyWayPointMission mission = new MyWayPointMission();
        WaypointMission wayMission = mission.build(waypointList, flySpeed, flyHeight , hoverHeight,  gimbalAngle);
        TimelineElement waypointMission = TimelineMission.elementFromWaypointMission(wayMission);
        elements.add(waypointMission );

        missionControl.unscheduleEverything();
        missionControl.scheduleElements(elements);
        if (listener!=null)
            missionControl.addListener(listener);
        missionControl.startTimeline();
    }

    public void abortMission() {
        myMissionAction.abort();
        missionControl.stopTimeline();
        missionControl.unscheduleEverything();
    }

    @NonNull
    private GimbalAttitudeAction gimbalAction(float pitchAngle, double completionTime) {
        Attitude attitude = new Attitude(pitchAngle, Rotation.NO_ROTATION, Rotation.NO_ROTATION);
        GimbalAttitudeAction gimbalAction = new GimbalAttitudeAction(attitude);
        gimbalAction.setCompletionTime(completionTime);
        return gimbalAction;
    }

    public static double cosForDegree(double degree) {
        return Math.cos(degree * Math.PI / 180.0f);
    }

    public static double calcLongitudeOffset(double latitude) {
        return ONE_METER_OFFSET / cosForDegree(latitude);
    }





}
