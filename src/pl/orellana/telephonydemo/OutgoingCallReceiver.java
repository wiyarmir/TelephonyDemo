package pl.orellana.telephonydemo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.Toast;

public class OutgoingCallReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		if (intent.getAction().equals(Intent.ACTION_NEW_OUTGOING_CALL)) {
			SharedPreferences sp = PreferenceManager
					.getDefaultSharedPreferences(context);
			if (sp.getBoolean("blockeractivated", false)) {
				String phoneNumber = intent.getExtras().getString(
						Intent.EXTRA_PHONE_NUMBER);
				String ABORT_PHONE_NUMBER = sp.getString("blockednr", "");
				if ((phoneNumber != null)
						&& phoneNumber.equals(ABORT_PHONE_NUMBER)) {
					Toast.makeText(
							context,
							"NEW OUTGOING CALL intercepted to number "
									+ ABORT_PHONE_NUMBER + " - aborting call",
							Toast.LENGTH_LONG).show();
					setResultData(null);
					abortBroadcast();
				}
			}
		}
	}
}
