package com.example.pickme_nebula0;

import android.content.Context;
import android.provider.Settings;

public class DeviceManager {

    // modified based on code produced by ChatGPT-4o by OpenAI in response to the prompt:
    // "in android studio using java, I want to get the user's device id"
    // generated on Oct 20th, 2024
    public static String getDeviceId(Context context) {
        return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

}
