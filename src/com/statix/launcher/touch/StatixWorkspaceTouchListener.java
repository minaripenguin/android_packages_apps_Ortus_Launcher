package com.statix.launcher.touch;

import android.content.Context;
import android.os.PowerManager;
import android.view.MotionEvent;

import com.android.launcher3.Launcher;
import com.android.launcher3.Workspace;
import com.android.launcher3.touch.WorkspaceTouchListener;

public class StatixWorkspaceTouchListener extends WorkspaceTouchListener {

    private final PowerManager mPowerManager;

    public StatixWorkspaceTouchListener(Launcher launcher, Workspace<?> workspace) {
        super(launcher, workspace);
        mPowerManager = (PowerManager) workspace.getContext().getSystemService(Context.POWER_SERVICE);
    }

    @Override
    public boolean onDoubleTap(MotionEvent event) {
        mPowerManager.goToSleep(event.getEventTime());
        return true;
    }

}
