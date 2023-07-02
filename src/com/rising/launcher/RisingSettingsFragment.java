package com.rising.launcher;

import static com.rising.launcher.OverlayCallbackImpl.KEY_ENABLE_MINUS_ONE;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

import androidx.preference.Preference;

import com.android.launcher3.R;
import com.android.launcher3.settings.SettingsActivity.LauncherSettingsFragment;

import com.rising.launcher.hpapps.HpAppsActivity;

public class RisingSettingsFragment extends LauncherSettingsFragment {

    protected static final String GSA_PACKAGE = "com.google.android.googlequicksearchbox";
    public static final String KEY_HP_APPS = "pref_hp_apps";

    private Preference mShowGoogleAppPref;

    @Override
    protected boolean initPreference(Preference preference) {
        switch (preference.getKey()) {
            case KEY_ENABLE_MINUS_ONE:
                mShowGoogleAppPref = preference;
                updateIsGoogleAppEnabled();
                return true;
            case KEY_HP_APPS:
                preference.setOnPreferenceClickListener(p -> {
                    Utils.showSecurePrompt(getActivity(),
                            getString(R.string.hp_apps_manager_name), () -> {
                        Intent intent = new Intent(getActivity(), HpAppsActivity.class);
                        startActivity(intent);
                    });
                    return true;
                });
                return true;
        }

        return super.initPreference(preference);
    }

    private void updateIsGoogleAppEnabled() {
        if (mShowGoogleAppPref != null) {
            mShowGoogleAppPref.setEnabled(isGSAEnabled(getContext()));
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        updateIsGoogleAppEnabled();
    }

    public static boolean isGSAEnabled(Context context) {
        try {
            return context.getPackageManager().getApplicationInfo(GSA_PACKAGE, 0).enabled;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }
}
