package com.rising.launcher;

import android.content.Context;
import android.util.AttributeSet;

import com.android.launcher3.Launcher;
import com.android.launcher3.Workspace;

import com.rising.launcher.touch.RisingWorkspaceTouchListener;

public class RisingWorkspace extends Workspace {

    /**
     * Used to inflate the RisingWorkspace from XML.
     *
     * @param context The application's context.
     * @param attrs The attributes set containing the Workspace's customization values.
     */
    public RisingWorkspace(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    /**
     * Used to inflate the RisingWorkspace from XML.
     *
     * @param context The application's context.
     * @param attrs The attributes set containing the Workspace's customization values.
     * @param defStyle Unused.
     */
    public RisingWorkspace(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setOnTouchListener(new RisingWorkspaceTouchListener(Launcher.getLauncher(context), this));
    }
}
