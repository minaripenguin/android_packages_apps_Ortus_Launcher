/*
 * Copyright (C) 2019 The LineageOS Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.statix.launcher.hpapps;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;

import androidx.annotation.NonNull;

import com.statix.launcher.hpapps.db.HpComponent;
import com.statix.launcher.hpapps.db.HpDatabaseHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LoadHpComponentsTask extends AsyncTask<Void, Integer, List<HpComponent>> {
    @NonNull
    private HpDatabaseHelper mDbHelper;

    @NonNull
    private PackageManager mPackageManager;

    @NonNull
    private Callback mCallback;

    LoadHpComponentsTask(@NonNull HpDatabaseHelper dbHelper,
            @NonNull PackageManager packageManager,
            @NonNull Callback callback) {
        mDbHelper = dbHelper;
        mPackageManager = packageManager;
        mCallback = callback;
    }

    @Override
    protected List<HpComponent> doInBackground(Void... voids) {
        List<HpComponent> list = new ArrayList<>();

        Intent filter = new Intent(Intent.ACTION_MAIN, null);
        filter.addCategory(Intent.CATEGORY_LAUNCHER);

        List<ResolveInfo> apps = mPackageManager.queryIntentActivities(filter,
                PackageManager.GET_META_DATA);

        int numPackages = apps.size();
        for (int i = 0; i < numPackages; i++) {
            ResolveInfo app = apps.get(i);
            try {
                String pkgName = app.activityInfo.packageName;
                String label = mPackageManager.getApplicationLabel(
                        mPackageManager.getApplicationInfo(pkgName,
                                PackageManager.GET_META_DATA)).toString();
                Drawable icon = app.loadIcon(mPackageManager);
                boolean isHidden = mDbHelper.isPackageHidden(pkgName);
                boolean isProtected = mDbHelper.isPackageProtected(pkgName);

                list.add(new HpComponent(pkgName, icon, label, isHidden, isProtected));

                publishProgress(Math.round(i * 100f / numPackages));
            } catch (PackageManager.NameNotFoundException ignored) {
            }
        }
        Collections.sort(list, (a, b) -> a.getLabel().compareTo(b.getLabel()));

        return list;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        if (values.length > 0) {
            mCallback.onLoadListProgress(values[0]);
        }
    }

    @Override
    protected void onPostExecute(List<HpComponent> hpComponents) {
        mCallback.onLoadCompleted(hpComponents);
    }

    interface Callback {
        void onLoadListProgress(int progress);
        void onLoadCompleted(List<HpComponent> result);
    }
}
