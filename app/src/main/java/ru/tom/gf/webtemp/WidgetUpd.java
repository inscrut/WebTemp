package ru.tom.gf.webtemp;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class WidgetUpd extends Service {
    public WidgetUpd() {

    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return null;
    }
}
