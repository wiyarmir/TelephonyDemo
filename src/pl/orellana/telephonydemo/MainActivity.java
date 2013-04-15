package pl.orellana.telephonydemo;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ListFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.telephony.CellInfo;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

public class MainActivity extends PreferenceActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Add a button to the header list.
		if (hasHeaders()) {
			Button button = new Button(this);
			button.setText("Show student info");
			button.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					AlertDialog.Builder b = new AlertDialog.Builder(
							MainActivity.this);
					b.setTitle("Student info")
							.setMessage(
									"Guillermo Orellana\nNr. Albumu 206485\nINEA112 Lab")
							.setNeutralButton("OK", null).create().show();
				}
			});
			setListFooter(button);
		}
	}

	/**
	 * Populate the activity with the top-level headers.
	 */
	@Override
	public void onBuildHeaders(List<Header> target) {
		loadHeadersFromResource(R.xml.preference_headers, target);
	}

	/**
	 * This fragment contains a second-level set of preference that you can get
	 * to by tapping an item in the first preferences fragment.
	 */
	public static class PhoneStateFragment extends ListFragment {
		private TelephonyManager tmgr;

		@SuppressLint("NewApi")
		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);

			// Can retrieve arguments from preference XML.
			Log.i("args", "Arguments: " + getArguments());

			tmgr = (TelephonyManager) getActivity().getSystemService(
					Context.TELEPHONY_SERVICE);

			List<String> l = new ArrayList<String>();

			// Get connected network country ISO code
			l.add("Network Country: " + tmgr.getNetworkCountryIso());

			// Get the connected network operator ID (MCC + MNC)
			l.add("Network Operator ID: " + tmgr.getNetworkOperator());

			// Get the connected network operator name
			l.add("Network Operator Name: " + tmgr.getNetworkOperatorName());

			// Get the type of network you are connected with
			int networkType = tmgr.getNetworkType();
			String s = "Network Type: ";
			switch (networkType) {
			case (TelephonyManager.NETWORK_TYPE_1xRTT):
				s += "RTT";
				break;
			case (TelephonyManager.NETWORK_TYPE_CDMA):
				s += "CDMA";
				break;
			case (TelephonyManager.NETWORK_TYPE_EDGE):
				s += "EDGE";
				break;
			case (TelephonyManager.NETWORK_TYPE_EVDO_0):
				s += "EVDO";
				break;
			case (TelephonyManager.NETWORK_TYPE_HSPA):
				s += "HSPA";
				break;
			case (TelephonyManager.NETWORK_TYPE_HSDPA):
				s += "HSDPA";
				break;
			case (TelephonyManager.NETWORK_TYPE_HSUPA):
				s += "HSUPA";
				break;
			case (TelephonyManager.NETWORK_TYPE_UMTS):
				s += "UMTS";
				break;
			default:
				s += "Unknown";
				Toast.makeText(getActivity(), "" + networkType,
						Toast.LENGTH_SHORT).show();
				break;
			}
			l.add(s);

			GsmCellLocation loc = (GsmCellLocation) tmgr.getCellLocation();
			if (loc != null) {
				l.add("Cell ID: " + loc.getCid());
				l.add("LAC Number: " + loc.getLac());
			}
			
			String deviceid = tmgr.getDeviceId();
			if (networkType == TelephonyManager.NETWORK_TYPE_CDMA) {
				l.add("MEID: " + deviceid);
			} else {
				l.add("IMEI: " + deviceid);
			}

			l.add("Subscriber ID: " + tmgr.getSubscriberId());

			int callstate = tmgr.getCallState();
			s = "Call state: ";
			switch (callstate) {
			case TelephonyManager.CALL_STATE_IDLE:
				s += "Idle";
				break;
			case TelephonyManager.CALL_STATE_OFFHOOK:
				s += "Off Hook";
				break;
			case TelephonyManager.CALL_STATE_RINGING:
				s += "Ringing";
				break;
			}
			l.add(s);

			int simstate = tmgr.getSimState();
			s = "SIM State: ";
			switch (simstate) {
			case TelephonyManager.SIM_STATE_ABSENT:
				s += "Absent";
				break;
			case TelephonyManager.SIM_STATE_NETWORK_LOCKED:
				s += "Locked";
				break;
			case TelephonyManager.SIM_STATE_PIN_REQUIRED:
				s += "PIN Required";
				break;
			case TelephonyManager.SIM_STATE_PUK_REQUIRED:
				s += "PUK Required";
				break;
			case TelephonyManager.SIM_STATE_READY:
				s += "Ready";
				break;
			case TelephonyManager.SIM_STATE_UNKNOWN:
			default:
				s += "Unknown";
				break;
			}

			l.add(s);

			l.add("SIM ISO Country Code: " + tmgr.getSimCountryIso());
			l.add("SIM Operator Name: " + tmgr.getSimOperatorName());
			l.add("SIM Serial Number: " + tmgr.getSimSerialNumber());

			setListAdapter(new ArrayAdapter<String>(getActivity(),
					android.R.layout.simple_list_item_1, l));

		}
	}

	/**
	 * This fragment shows the preferences for the second header.
	 */
	public static class BlockCallFragment extends PreferenceFragment {
		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);

			// Can retrieve arguments from headers XML.
			Log.i("args", "Arguments: " + getArguments());

			// Load the preferences from an XML resource
			addPreferencesFromResource(R.xml.blockcall_preferences);
		}
	}

	public static class SMSFragment extends PreferenceFragment {
		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);

			// Can retrieve arguments from headers XML.
			Log.i("args", "Arguments: " + getArguments());

			// Load the preferences from an XML resource
			addPreferencesFromResource(R.xml.sms_preferences);
		}
	}

	public static class MonitorFragment extends ListFragment {
		private ArrayAdapter<String> adapter;
		private PhoneStateListener phoneStateListener;

		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);

		}

		@Override
		public void onActivityCreated(Bundle savedInstanceState) {
			super.onActivityCreated(savedInstanceState);

			getListView().setTranscriptMode(
					ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);

			adapter = new ArrayAdapter<String>(getActivity(),
					android.R.layout.simple_list_item_1);

			setListAdapter(adapter);

			adapter.add("Starting monitoring of phone state.");

			final TelephonyManager telMgr = (TelephonyManager) getActivity()
					.getSystemService(Context.TELEPHONY_SERVICE);
			phoneStateListener = new PhoneStateListener() {
				public void onCallStateChanged(int state, String incomingNumber) {
					String s = "N/A";
					switch (state) {
					case TelephonyManager.CALL_STATE_IDLE:
						s = "Idle";
						break;
					case TelephonyManager.CALL_STATE_OFFHOOK:
						s = "Off Hook";
						break;
					case TelephonyManager.CALL_STATE_RINGING:
						s = "Ringing";
						break;
					default:
						s = "onCallStateChanged:" + state + ":"
								+ incomingNumber;
					}
					adapter.add(s);
				}

				@Override
				public void onCellInfoChanged(List<CellInfo> cellInfo) {
				}

				@Override
				public void onSignalStrengthsChanged(
						SignalStrength signalStrength) {
					String s = "Signal Strength: ";
					if (signalStrength.isGsm()) {
						s += "GSM strength:"
								+ signalStrength.getGsmSignalStrength()
								+ " BER:" + signalStrength.getGsmBitErrorRate();
					} else {

					}

					adapter.add(s);
				}

				@Override
				public void onDataConnectionStateChanged(int state,
						int networkType) {
					String s = "";
					switch (state) {
					case TelephonyManager.DATA_DISCONNECTED:
						s = "Data disconnected";
						break;
					case TelephonyManager.DATA_CONNECTING:
						s = "Data connecting";
						break;
					case TelephonyManager.DATA_CONNECTED:
						s = "Data connected";
						break;
					case TelephonyManager.DATA_SUSPENDED:
						s = "Data suspended";
						break;
					default:
						s = "onDataConnectionStateChanged:" + state + ":"
								+ networkType;
					}
					adapter.add(s);
				}

				@Override
				public void onDataActivity(int direction) {
					String s = "Data activity: ";
					switch (direction) {
					case TelephonyManager.DATA_ACTIVITY_NONE:
						s += "None";
						break;
					case TelephonyManager.DATA_ACTIVITY_IN:
						s += "In";
						break;
					case TelephonyManager.DATA_ACTIVITY_OUT:
						s += "Out";
						break;
					case TelephonyManager.DATA_ACTIVITY_INOUT:
						s += "In/Out";
						break;
					case TelephonyManager.DATA_ACTIVITY_DORMANT:
						s += "Dormant";
						break;
					}

					adapter.add(s);
				}
			};
			telMgr.listen(phoneStateListener,
					PhoneStateListener.LISTEN_DATA_ACTIVITY
							| PhoneStateListener.LISTEN_DATA_CONNECTION_STATE
							| PhoneStateListener.LISTEN_SIGNAL_STRENGTHS
							| PhoneStateListener.LISTEN_CELL_INFO);
		}

		@Override
		public void onDestroy() {
			final TelephonyManager telMgr = (TelephonyManager) getActivity()
					.getSystemService(Context.TELEPHONY_SERVICE);
			telMgr.listen(phoneStateListener, PhoneStateListener.LISTEN_NONE);
			super.onDestroy();
		}
	}

}
