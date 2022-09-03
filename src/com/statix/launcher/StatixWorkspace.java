package com.statix.launcher;

import android.content.Context;
import android.util.AttributeSet;

import com.android.launcher3.Launcher;
import com.android.launcher3.Workspace;

import com.statix.launcher.touch.StatixWorkspaceTouchListener;

public class StatixWorkspace extends Workspace {

    /**
     * Used to inflate the StatixWorkspace from XML.
     *
     * @param context The application's context.
     * @param attrs The attributes set containing the Workspace's customization values.
     */
    public StatixWorkspace(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    /**
     * Used to inflate the StatixWorkspace from XML.
     *
     * @param context The application's context.
     * @param attrs The attributes set containing the Workspace's customization values.
     * @param defStyle Unused.
     */
    public StatixWorkspace(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setOnTouchListener(new StatixWorkspaceTouchListener(Launcher.getLauncher(context), this));
    }
}
