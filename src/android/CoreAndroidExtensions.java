package by.chemerisuk.cordova.coreextensions;

import org.json.JSONArray;
import org.json.JSONException;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;

import android.content.Context;
import android.content.Intent;
import android.view.WindowManager.LayoutParams;


public class CoreAndroidExtensions extends CordovaPlugin {
    public static final String PLUGIN_NAME = "CoreAndroidExtensions";

    /**
     * Executes the request and returns PluginResult.
     *
     * @param action            The action to execute.
     * @param args              JSONArry of arguments for the plugin.
     * @param callbackContext   The callback context from which we were invoked.
     * @return                  A PluginResult object with a status and message.
     */
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        if (action.equals("minimizeApp")) {
            this.minimizeApp(args.optBoolean(0, false));
        } else if (action.equals("resumeApp")) {
            this.resumeApp(args.optBoolean(0, false));
        }

        callbackContext.success();

        return true;
    }

    /**
     * Called when the system is about to start resuming a previous activity.
     *
     * @param multitasking
     *      Flag indicating if multitasking is turned on for app
     */
    @Override
    public void onPause(boolean multitasking) {
        clearWindowFlags();
    }

    private void minimizeApp(boolean moveBack) {
        // try to send it back and back to previous app
        if (moveBack) {
            moveBack = cordova.getActivity().moveTaskToBack(true);
        }

        // if not possible jump to home
        if (!moveBack) {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            cordova.getActivity().startActivity(intent);
        }
    }

    private void resumeApp(boolean force) {
        Context ctx = cordova.getActivity().getApplicationContext();
        Intent intent = new Intent(ctx, cordova.getActivity().getClass());

        intent.setAction(Intent.ACTION_MAIN);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED | Intent.FLAG_FROM_BACKGROUND | Intent.FLAG_ACTIVITY_NO_ANIMATION);

        ctx.startActivity(intent);

        if (force) {
            setupWindowFlags();
        }
    }

    private void setupWindowFlags() {
        cordova.getActivity().runOnUiThread(new Runnable() {
            public void run() {
                cordova.getActivity().getWindow().addFlags(
                    LayoutParams.FLAG_SHOW_WHEN_LOCKED | LayoutParams.FLAG_DISMISS_KEYGUARD | LayoutParams.FLAG_TURN_SCREEN_ON);
            }
        });
    }

    private void clearWindowFlags() {
        cordova.getActivity().getWindow().clearFlags(
            LayoutParams.FLAG_SHOW_WHEN_LOCKED | LayoutParams.FLAG_DISMISS_KEYGUARD | LayoutParams.FLAG_TURN_SCREEN_ON);
    }

}