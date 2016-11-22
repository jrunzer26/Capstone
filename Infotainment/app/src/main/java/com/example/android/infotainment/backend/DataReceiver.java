package com.example.android.infotainment.backend;

import com.example.android.infotainment.backend.models.UserData;

/**
 * Created by 100520993 on 10/31/2016.
 */

public interface DataReceiver {
    void onReceive(UserData userData);
}
