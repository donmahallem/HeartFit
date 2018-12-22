package com.github.donmahallem.heartfit.recycler.viewholder;

import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.donmahallem.heartfit.R;
import com.google.android.gms.fitness.data.Session;

import org.threeten.bp.LocalDateTime;
import org.threeten.bp.ZoneOffset;
import org.threeten.bp.format.DateTimeFormatter;

import java.lang.ref.WeakReference;
import java.util.concurrent.TimeUnit;

public class SessionSimpleViewHolder extends LayoutViewHolder implements View.OnLongClickListener, View.OnClickListener {
    private final ImageView mIvIconPackageIcon;
    private final TextView mTxtDateTime;
    private final TextView mTxtName;
    private final TextView mTxtDuration;
    private final TextView mTxtType;
    private final ImageView mIvIcon;
    private WeakReference<OnSessionInteractListener> mOnSessionInteractListenerWeakReference;
    private Session mSession;

    public SessionSimpleViewHolder(ViewGroup parent) {
        super(parent, R.layout.vh_session_simple);
        this.mTxtName = this.itemView.findViewById(R.id.txtTitle);
        this.mTxtDateTime = this.itemView.findViewById(R.id.txtDateTime);
        this.mTxtDuration = this.itemView.findViewById(R.id.txtDuration);
        this.mTxtType = this.itemView.findViewById(R.id.txtType);
        this.itemView.setOnClickListener(this);
        this.itemView.setOnLongClickListener(this);
        this.mIvIcon=this.itemView.findViewById(R.id.ivIcon);
        this.mIvIconPackageIcon=this.itemView.findViewById(R.id.ivIconPackageIcon);
    }

    @Override
    public boolean onLongClick(View view) {
        if (this.mOnSessionInteractListenerWeakReference != null && this.mOnSessionInteractListenerWeakReference.get() != null) {
            this.mOnSessionInteractListenerWeakReference.get().onLongClick(this.mSession);
            return true;
        }
        return false;
    }

    @Override
    public void onClick(View view) {
        if (this.mOnSessionInteractListenerWeakReference != null && this.mOnSessionInteractListenerWeakReference.get() != null) {
            this.mOnSessionInteractListenerWeakReference.get().onClick(this.mSession);
        }
    }

    public void setOnSessionInteractListener(OnSessionInteractListener onSessionInteractListener) {
        this.mOnSessionInteractListenerWeakReference = new WeakReference<>(onSessionInteractListener);
    }

    public void setSession(Session session) {
        this.mSession = session;
        if (session.getName() != null)
            this.mTxtName.setText(session.getName());
        this.mTxtDateTime.setText(LocalDateTime.ofEpochSecond(session.getStartTime(TimeUnit.SECONDS), 0, ZoneOffset.UTC).format(DateTimeFormatter.ISO_DATE_TIME));
        if (session.hasActiveTime()) {
            this.mTxtDuration.setText("" + session.getActiveTime(TimeUnit.MINUTES));
        } else {
            final long diff = session.getEndTime(TimeUnit.SECONDS) - session.getStartTime(TimeUnit.SECONDS);
            if (diff > 0) {
                this.mTxtDuration.setText(String.format("%s:%s", diff / 60, diff % 60));
            } else {
                this.mTxtDuration.setText("");
            }
        }
        this.mTxtType.setText(session.getActivity());
        try {
            Drawable icon = this.itemView.getContext().getPackageManager().getApplicationIcon(session.getAppPackageName());
            this.mIvIconPackageIcon.setImageDrawable(icon);
        } catch (PackageManager.NameNotFoundException e) {
            this.mIvIconPackageIcon.setImageResource(R.drawable.ic_show_chart_white_24dp);
        }
    }

    public interface OnSessionInteractListener {
        void onLongClick(Session session);

        void onClick(Session session);
    }
}
