package com.statix.launcher.touch;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.PowerManager;
import android.view.MotionEvent;

import com.android.launcher3.Launcher;
import com.android.launcher3.LauncherPrefs;
import com.android.launcher3.Workspace;
import com.android.launcher3.touch.WorkspaceTouchListener;

public class StatixWorkspaceTouchListener extends WorkspaceTouchListener {

    public static final String KEY_DT_GESTURE = "pref_dt_gesture";

    private final Context mContext;
    private final PowerManager mPowerManager;

    public StatixWorkspaceTouchListener(Launcher launcher, Workspace<?> workspace) {
        super(launcher, workspace);
        mContext = workspace.getContext();
        mPowerManager = (PowerManager) mContext.getSystemService(Context.POWER_SERVICE);
    }

    @Override
    public boolean onDoubleTap(MotionEvent event) {
        if (isDoubleTapEnabled())
            mPowerManager.goToSleep(event.getEventTime());
        return true;
    }

    private boolean isDoubleTapEnabled() {
        SharedPreferences preferences = LauncherPrefs.getPrefs(mContext.getApplicationContext());
        return preferences.getBoolean(KEY_DT_GESTURE, true);
    }

}
