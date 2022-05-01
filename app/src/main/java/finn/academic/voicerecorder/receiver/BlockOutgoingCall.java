package finn.academic.voicerecorder.receiver;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Date;

import finn.academic.voicerecorder.util.PhoneCall;

public class BlockOutgoingCall extends PhoneCallReceiver {
    SharedPreferences settingRef;
    SharedPreferences statusRef;
    @Override
    protected void onIncomingCallReceived(Context ctx, String number, Date start) {
        if (settingRef == null) {
            settingRef = ctx.getSharedPreferences("setting", Context.MODE_PRIVATE);
        }

        if (statusRef == null) {
            statusRef = ctx.getSharedPreferences("status", Context.MODE_PRIVATE);
        }

        if (settingRef.getBoolean("block_call", false)) {
            if (statusRef.getBoolean("is_recording", false)) {
                PhoneCall.endCall(ctx);
            }
        }
    }

    @Override
    protected void onIncomingCallAnswered(Context ctx, String number, Date start) {

    }

    @Override
    protected void onIncomingCallEnded(Context ctx, String number, Date start, Date end) {

    }

    @Override
    protected void onOutgoingCallStarted(Context ctx, String number, Date start) {

    }

    @Override
    protected void onOutgoingCallEnded(Context ctx, String number, Date start, Date end) {

    }

    @Override
    protected void onMissedCall(Context ctx, String number, Date start) {

    }
}
