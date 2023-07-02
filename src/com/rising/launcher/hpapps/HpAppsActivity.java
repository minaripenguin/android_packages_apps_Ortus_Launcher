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
package com.rising.launcher.hpapps;

import static com.rising.launcher.hpapps.db.HpComponent.Kind.HIDDEN;
import static com.rising.launcher.hpapps.db.HpComponent.Kind.PROTECTED;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.launcher3.LauncherAppState;
import com.android.launcher3.LauncherPrefs;
import com.android.launcher3.R;

import com.rising.launcher.Utils;
import com.rising.launcher.hpapps.db.HpComponent;
import com.rising.launcher.hpapps.db.HpDatabaseHelper;

import java.util.List;

public class HpAppsActivity extends Activity implements
        HpAppsAdapter.Listener,
        LoadHpComponentsTask.Callback,
        UpdateItemTask.UpdateCallback {

    private static final String KEY_TRUST_ONBOARDING = "pref_trust_onboarding";

    private RecyclerView mRecyclerView;
    private LinearLayout mLoadingView;
    private ProgressBar mProgressBar;

    private HpDatabaseHelper mDbHelper;
    private HpAppsAdapter mAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstance) {
        super.onCreate(savedInstance);

        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        setContentView(R.layout.activity_hidden_apps);
        mRecyclerView = findViewById(R.id.hidden_apps_list);
        mLoadingView = findViewById(R.id.hidden_apps_loading);
        mLoadingView.setVisibility(View.VISIBLE);
        mProgressBar = findViewById(R.id.hidden_apps_progress_bar);

        final boolean hasSecureKeyguard = Utils.hasSecureKeyguard(this);
        mAdapter = new HpAppsAdapter(this, hasSecureKeyguard);
        mDbHelper = HpDatabaseHelper.getInstance(this);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(mAdapter);

        showOnBoarding(false);

        new LoadHpComponentsTask(mDbHelper, getPackageManager(), this).execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_hp_apps, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            return true;
        } else if (id == R.id.menu_hp_help) {
            showOnBoarding(true);
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onHiddenItemChanged(@NonNull HpComponent component) {
        new UpdateItemTask(mDbHelper, this, HIDDEN).execute(component);
    }

    @Override
    public void onProtectedItemChanged(@NonNull HpComponent component) {
        new UpdateItemTask(mDbHelper, this, PROTECTED).execute(component);
    }

    @Override
    public void onUpdated(boolean result) {
        LauncherAppState state = LauncherAppState.getInstanceNoCreate();
        if (state != null) {
            state.getModel().forceReload();
        }
    }

    @Override
    public void onLoadListProgress(int progress) {
        mProgressBar.setProgress(progress);
    }

    @Override
    public void onLoadCompleted(List<HpComponent> result) {
        mLoadingView.setVisibility(View.GONE);
        mRecyclerView.setVisibility(View.VISIBLE);
        mAdapter.update(result);
    }

    private void showOnBoarding(boolean forceShow) {
        SharedPreferences preferenceManager = LauncherPrefs.getPrefs(this);
        if (!forceShow && preferenceManager.getBoolean(KEY_TRUST_ONBOARDING, false)) {
            return;
        }

        preferenceManager.edit()
                .putBoolean(KEY_TRUST_ONBOARDING, true)
                .apply();

        new AlertDialog.Builder(this)
                .setView(R.layout.dialog_hp_info)
                .setPositiveButton(android.R.string.ok, null)
                .show();
    }
}
