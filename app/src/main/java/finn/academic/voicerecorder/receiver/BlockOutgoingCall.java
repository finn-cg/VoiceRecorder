package finn.academic.voicerecorder.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class BlockOutgoingCall extends BroadcastReceiver {
    String number;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("before", "Number is -->> " + number);
        number = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
        Log.d("Block", "number: " + number);
//        setResultData(null);
    }
}
