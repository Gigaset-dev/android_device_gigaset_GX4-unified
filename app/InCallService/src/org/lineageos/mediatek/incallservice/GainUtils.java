package org.lineageos.mediatek.incallservice;

import android.media.AudioDeviceInfo;
import android.os.SystemProperties;
import android.media.AudioSystem;
import android.util.Log;

public class GainUtils {
    public static final String LOG_TAG = "MediatekInCallService";
    public static final int volSteps = SystemProperties.getInt("ro.config.vc_call_vol_steps", 7);

    /**
     * Sets the gain level for a given audio device.
     * @param audioDevice The audio device to set the gain level for.
     * @param gainIndex The gain level to set.
     * @param streamType The stream type to set the gain level for.
     */
    public static void setGainLevel(int audioDevice, int gainIndex, int streamType) {
        String parameters = String.format("volumeDevice=%d;volumeIndex=%d;volumeStreamType=%d",
                                          audioDevice, Math.min(volSteps, gainIndex), streamType);
        Log.d(LOG_TAG, "Setting audio parameters to: " + parameters);
        AudioSystem.setParameters(parameters);
    }

    /**
     * Sets the gain level for built-in earpiece and bluetooth SCO devices.
     * @param gainIndex The gain level to set.
     */
    public static void setGainLevel(int gainIndex) {
        GainUtils.setGainLevel(AudioDeviceInfo.TYPE_BUILTIN_EARPIECE, gainIndex, AudioSystem.STREAM_VOICE_CALL);
        GainUtils.setGainLevel(AudioDeviceInfo.TYPE_BLUETOOTH_SCO, gainIndex, AudioSystem.STREAM_VOICE_CALL);
    }
}
