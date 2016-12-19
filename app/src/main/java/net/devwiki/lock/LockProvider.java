package net.devwiki.lock;

import android.app.PendingIntent;
import android.app.admin.DevicePolicyManager;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

/**
 * 锁屏的Provider
 * Created by DevWiki on 2016/12/19.
 */

public class LockProvider extends AppWidgetProvider {

    private static final String WIDGET_LOCK = "net.devwiki.lock.action.WIDGET_LOCK";

    private int id;

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);
        id = appWidgetIds[0];
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        if (intent.getAction().equals(AppWidgetManager.ACTION_APPWIDGET_UPDATE)) {
            setClickListener(context);
        }
        if (intent.getAction().equals(WIDGET_LOCK)) {
            checkOrLock(context);
        }
    }

    private void setClickListener(Context context) {
        RemoteViews remoteView = new RemoteViews(context.getPackageName(), R.layout.layout_widget);
        Intent intent2 = new Intent(context, LockProvider.class);
        intent2.setAction(WIDGET_LOCK);
        intent2.addCategory(Intent.CATEGORY_ALTERNATIVE);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent2, PendingIntent.FLAG_CANCEL_CURRENT);
        remoteView.setOnClickPendingIntent(R.id.widget_lock_iv, pendingIntent);
        AppWidgetManager.getInstance(context).updateAppWidget(id, remoteView);
    }

    private void checkOrLock(Context context) {
        DevicePolicyManager policyManager = (DevicePolicyManager) context.getSystemService(Context
                .DEVICE_POLICY_SERVICE);
        ComponentName componentName = new ComponentName(context, AdminReceiver.class);
        if (policyManager.isAdminActive(componentName)) {
            policyManager.lockNow();
        } else {
            Intent startIntent = new Intent(context, MainActivity.class);
            startIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(startIntent);
        }
    }
}
