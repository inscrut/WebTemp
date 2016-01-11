package ru.tom.gf.webtemp;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

/**
 * Implementation of App Widget functionality.
 */
public class PogodaWidged extends AppWidgetProvider {

    final String UPDATE_ALL_WIDGETS = "update_all_widgets";

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {

        CharSequence widgetText = context.getString(R.string.appwidget_text);
        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.pogoda_widged);
        String[] data = MainActivity.answer.split(":");

        views.setTextViewText(R.id.tv_temp, "Температура: " + data[1]);
        views.setTextViewText(R.id.tv_hum, "Влажность: " + data[3]);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        if (intent.getAction().equalsIgnoreCase(UPDATE_ALL_WIDGETS)) {
            ComponentName thisAppWidget = new ComponentName(
                    context.getPackageName(), getClass().getName());
            AppWidgetManager appWidgetManager = AppWidgetManager
                    .getInstance(context);
            int ids[] = appWidgetManager.getAppWidgetIds(thisAppWidget);
            for (int appWidgetId : ids) {
                updateAppWidget(context, appWidgetManager, appWidgetId);
            }
        }
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
        super.onEnabled(context);
        Intent intent = new Intent(context, PogodaWidged.class);
        intent.setAction(UPDATE_ALL_WIDGETS);
        PendingIntent pIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
        AlarmManager alarmManager = (AlarmManager) context
                .getSystemService(Context.ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC, System.currentTimeMillis(),
                5000, pIntent);
    }

    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);
        Intent intent = new Intent(context, PogodaWidged.class);
        intent.setAction(UPDATE_ALL_WIDGETS);
        PendingIntent pIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
        AlarmManager alarmManager = (AlarmManager) context
                .getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pIntent);
    }
}

