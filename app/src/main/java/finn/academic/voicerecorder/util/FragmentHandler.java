package finn.academic.voicerecorder.util;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Build;

public class FragmentHandler {
    public static void refreshFragment(Fragment fragment) {
        FragmentTransaction ft = fragment.getFragmentManager().beginTransaction();
        if (Build.VERSION.SDK_INT >= 26) {
            ft.setReorderingAllowed(false);
        }
        ft.detach(fragment).attach(fragment).commit();
    }
}
