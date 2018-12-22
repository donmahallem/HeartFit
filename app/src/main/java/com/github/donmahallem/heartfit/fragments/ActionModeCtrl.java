package com.github.donmahallem.heartfit.fragments;

import android.content.Context;
import android.view.Menu;
import android.view.MenuItem;

import com.github.donmahallem.heartfit.R;

import androidx.appcompat.view.ActionMode;
import androidx.recyclerview.selection.SelectionTracker;

/**
 * Created on 25.11.2018.
 */
public class ActionModeCtrl implements ActionMode.Callback {

    private final Context context;
    private final SelectionTracker selectionTracker;

    public ActionModeCtrl(Context context, SelectionTracker selectionTracker) {
        this.context = context;
        this.selectionTracker = selectionTracker;
    }

    @Override
    public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
        actionMode.getMenuInflater().inflate(R.menu.actionmode_workout_exercises, menu);
        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
        return false;
    }

    @Override
    public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.menu_delete:
                deleteItems();
                return true;
            default:
                return false;
        }
    }

    private void deleteItems() {
    }

    @Override
    public void onDestroyActionMode(ActionMode actionMode) {
        selectionTracker.clearSelection();
    }
}