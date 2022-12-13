package com.socialtravel.utils;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;

import com.socialtravel.providers.AuthProvider;
import com.socialtravel.providers.UserProvider;

import java.util.List;

//Solución parcial para el mensaje que aparece en el chat de "En línea" "Hace un momento"... Documentación: "Crea una presencia en Cloud Firestore."
public class ViewedMessageHelper {

    public static void updateOnline(boolean status, final Context context) {
        UserProvider userProvider = new UserProvider();
        AuthProvider authProvider = new AuthProvider();
        if(authProvider.getUid()!= null) {
            if(isApplicationSendToBackground(context)) {
                userProvider.updateOnline(authProvider.getUid(), status);
            }
            else if (status){
                userProvider.updateOnline(authProvider.getUid(), status);
            }
        }
    }

    public static boolean isApplicationSendToBackground(final Context context) { //Función que indica que el usuario dió a la tecla de inicio sin cerrar la app.
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> tasks = activityManager.getRunningTasks(1);

        if(!tasks.isEmpty()) {
            ComponentName topActivity = tasks.get(0).topActivity;
            if(!topActivity.getPackageName().equals(context.getPackageName())) {
                return true;
            }
        }
        return false;
    }
}
