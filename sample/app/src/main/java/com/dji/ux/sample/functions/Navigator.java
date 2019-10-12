package com.dji.ux.sample.functions;

public class Navigator {
    final MyVirtualStick virtualThread = new MyVirtualStick(MyVirtualStick.VELOCITY);

    public Navigator(){
        virtualThread.start();
    }

}
