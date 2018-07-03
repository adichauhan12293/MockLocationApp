package adichauhan.com.mocklocationapplication.app.utils;

import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

import adichauhan.com.mocklocationapplication.app.MockLocationApplication;

/**
 * Created by adityachauhan on 10/4/17.
 *
 */

public class PermissionUtils {

    public static Boolean checkPermisssion(String permission) {
        return ContextCompat.checkSelfPermission(MockLocationApplication.getInstance().getApplicationContext(),
                permission) == PackageManager.PERMISSION_GRANTED;
    }

    public static void getPermissions(AppCompatActivity activity, Integer requestCode, String[] permissions) {
        ActivityCompat.requestPermissions(activity, permissions, requestCode);
    }
}