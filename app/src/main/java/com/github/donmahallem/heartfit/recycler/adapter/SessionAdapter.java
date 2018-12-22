package com.github.donmahallem.heartfit.recycler.adapter;

import android.view.ViewGroup;

import com.github.donmahallem.heartfit.recycler.viewholder.SessionSimpleViewHolder;
import com.google.android.gms.fitness.data.Session;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;
import timber.log.Timber;

/**
 * Created on 07.11.2018.
 */
public class SessionAdapter extends RecyclerView.Adapter<SessionSimpleViewHolder> {

    private SessionSimpleViewHolder.OnSessionInteractListener mOnSessionInteractListener;
    private List<Session> mSessionList = new ArrayList<>();

    @NonNull
    @Override
    public SessionSimpleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final SessionSimpleViewHolder viewHolder = new SessionSimpleViewHolder(parent);
        viewHolder.setOnSessionInteractListener(this.mOnSessionInteractListener);
        return viewHolder;
    }

    public void setOnSessionInteractListener(SessionSimpleViewHolder.OnSessionInteractListener onSessionInteractListener) {
        mOnSessionInteractListener = onSessionInteractListener;
    }

    @Override
    public void onBindViewHolder(@NonNull SessionSimpleViewHolder holder, int position) {
        holder.setSession(this.mSessionList.get(position));
    }

    public void setSessions(@NonNull final List<Session> sessions) {
        DiffUtil.DiffResult result = DiffUtil.calculateDiff(new DiffUtil.Callback() {
            @Override
            public int getOldListSize() {
                return SessionAdapter.this.mSessionList.size();
            }

            @Override
            public int getNewListSize() {
                return sessions.size();
            }

            @Override
            public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                return sessions.get(newItemPosition).getIdentifier().equals(SessionAdapter.this.mSessionList.get(oldItemPosition).getIdentifier());
            }

            @Override
            public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                final Session newSession = sessions.get(newItemPosition);
                final Session oldSession = SessionAdapter.this.mSessionList.get(oldItemPosition);
                if (newSession.isOngoing() != oldSession.isOngoing())
                    return false;
                if (newSession.getStartTime(TimeUnit.SECONDS) != oldSession.getEndTime(TimeUnit.SECONDS))
                    return false;
                if (newSession.getName() != null && newSession.getName().equals(oldSession.getName()))
                    return false;
                if (oldSession.getName() != null && oldSession.getName().equals(newSession.getName()))
                    return false;
                return true;
            }
        }, true);
        this.mSessionList.clear();
        this.mSessionList.addAll(sessions);
        result.dispatchUpdatesTo(this);
        Timber.d("List updated");
        //this.notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return this.mSessionList.size();
    }

}
