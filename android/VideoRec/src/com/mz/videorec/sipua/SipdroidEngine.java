/*
 * Copyright (C) 2009 The Sipdroid Open Source Project
 * Copyright (C) 2008 Hughes Systique Corporation, USA (http://www.hsc.com)
 * 
 * This file is part of Sipdroid (http://www.sipdroid.org)
 * 
 * Sipdroid is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This source code is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this source code; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package com.mz.videorec.sipua;

import java.io.IOException;
import java.net.UnknownHostException;

import org.zoolu.net.IpAddress;
import org.zoolu.net.SocketAddress;
import org.zoolu.sip.address.NameAddress;
import org.zoolu.sip.provider.SipProvider;
import org.zoolu.sip.provider.SipStack;

import com.mz.videorec.net.KeepAliveSip;
import com.mz.videorec.sipua.ui.ChangeAccount;
import com.mz.videorec.sipua.ui.LoopAlarm;
import com.mz.videorec.sipua.ui.Receiver;
import com.mz.videorec.sipua.ui.Settings;
import com.mz.videorec.sipua.ui.Sipdroid;

import android.content.Context;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.PowerManager;
import android.os.SystemClock;
import android.preference.PreferenceManager;

public class SipdroidEngine implements RegisterAgentListener {

	public static final int UNINITIALIZED = 0x0;
	public static final int INITIALIZED = 0x2;

	/** User Agent */
	// public UserAgent uas;
	public UserAgent ua;

	/** Register Agent */
	public RegisterAgent ras;

	private KeepAliveSip kas;

	/** UserAgentProfile */
	public UserAgentProfile user_profile;

	public SipProvider sip_providers;

	static PowerManager.WakeLock wl;
	public static PowerManager.WakeLock pwl;
	static WifiManager.WifiLock wwl;

	UserAgentProfile getUserAgentProfile(String suffix) {
		UserAgentProfile user_profile = new UserAgentProfile(null);

		user_profile.username = PreferenceManager.getDefaultSharedPreferences(
				getUIContext()).getString(Settings.PREF_USERNAME + suffix,
				Settings.DEFAULT_USERNAME); // modified
		user_profile.passwd = PreferenceManager.getDefaultSharedPreferences(
				getUIContext()).getString(Settings.PREF_PASSWORD + suffix,
				Settings.DEFAULT_PASSWORD);
		if (PreferenceManager
				.getDefaultSharedPreferences(getUIContext())
				.getString(Settings.PREF_DOMAIN + suffix,
						Settings.DEFAULT_DOMAIN).length() == 0) {
			user_profile.realm = PreferenceManager.getDefaultSharedPreferences(
					getUIContext()).getString(Settings.PREF_SERVER + suffix,
					Settings.DEFAULT_SERVER);
		} else {
			user_profile.realm = PreferenceManager.getDefaultSharedPreferences(
					getUIContext()).getString(Settings.PREF_DOMAIN + suffix,
					Settings.DEFAULT_DOMAIN);
		}
		user_profile.realm_orig = user_profile.realm;
		if (PreferenceManager
				.getDefaultSharedPreferences(getUIContext())
				.getString(Settings.PREF_FROMUSER + suffix,
						Settings.DEFAULT_FROMUSER).length() == 0) {
			user_profile.from_url = user_profile.username;
		} else {
			user_profile.from_url = PreferenceManager
					.getDefaultSharedPreferences(getUIContext()).getString(
							Settings.PREF_FROMUSER + suffix,
							Settings.DEFAULT_FROMUSER);
		}

		// MMTel configuration (added by mandrajg)
		user_profile.qvalue = PreferenceManager.getDefaultSharedPreferences(
				getUIContext()).getString(Settings.PREF_MMTEL_QVALUE,
				Settings.DEFAULT_MMTEL_QVALUE);
		user_profile.mmtel = PreferenceManager.getDefaultSharedPreferences(
				getUIContext()).getBoolean(Settings.PREF_MMTEL,
				Settings.DEFAULT_MMTEL);

		user_profile.pub = PreferenceManager.getDefaultSharedPreferences(
				getUIContext()).getBoolean(Settings.PREF_EDGE + suffix,
				Settings.DEFAULT_EDGE)
				|| PreferenceManager
						.getDefaultSharedPreferences(getUIContext())
						.getBoolean(Settings.PREF_3G + suffix,
								Settings.DEFAULT_3G);
		return user_profile;
	}

	public boolean StartEngine() {
		PowerManager pm = (PowerManager) getUIContext().getSystemService(
				Context.POWER_SERVICE);
		WifiManager wm = (WifiManager) getUIContext().getSystemService(
				Context.WIFI_SERVICE);
		if (wl == null) {
			if (!PreferenceManager.getDefaultSharedPreferences(getUIContext())
					.contains(com.mz.videorec.sipua.ui.Settings.PREF_KEEPON)) {
				Editor edit = PreferenceManager.getDefaultSharedPreferences(
						getUIContext()).edit();

				edit.putBoolean(com.mz.videorec.sipua.ui.Settings.PREF_KEEPON,
						true);
				edit.commit();
			}
			wl = null;
			pwl = null;
			wwl = null;
		}

		ras = null;
		kas = null;
		lastmsgs = null;
		sip_providers = null;
		user_profile = getUserAgentProfile("");

		SipStack.init(null);

		if (wl == null) {
			wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
					"Sipdroid.SipdroidEngine");
			pwl = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK
					| PowerManager.ACQUIRE_CAUSES_WAKEUP,
					"Sipdroid.SipdroidEngine");
			if (!PreferenceManager.getDefaultSharedPreferences(getUIContext())
					.getBoolean(com.mz.videorec.sipua.ui.Settings.PREF_KEEPON,
							com.mz.videorec.sipua.ui.Settings.DEFAULT_KEEPON)) {
				wwl = wm.createWifiLock(3, "Sipdroid.SipdroidEngine");
				wwl.setReferenceCounted(false);
			}
		}

		try {
			SipStack.debug_level = 0;
			// SipStack.log_path = "/data/data/org.sipdroid.sipua";
			SipStack.max_retransmission_timeout = 4000;
			SipStack.default_transport_protocols = new String[1];
			SipStack.default_transport_protocols[0] = PreferenceManager
					.getDefaultSharedPreferences(getUIContext())
					.getString(
							Settings.PREF_PROTOCOL + (""),
							user_profile.realm.equals(Settings.DEFAULT_SERVER) ? "tcp"
									: "udp");

			String version = "Sipdroid/" + Sipdroid.getVersion() + "/"
					+ Build.MODEL;
			SipStack.ua_info = version;
			SipStack.server_info = version;

			IpAddress.setLocalIpAddress();
			sip_providers = new SipProvider(IpAddress.localIpAddress, 0);
			user_profile.contact_url = getContactURL(user_profile.username,
					sip_providers);

			if (user_profile.from_url.indexOf("@") < 0) {
				user_profile.from_url += "@" + user_profile.realm;
			}

			CheckEngine();

			// added by mandrajg
			String icsi = null;
			if (user_profile.mmtel == true) {
				icsi = "\"urn%3Aurn-7%3A3gpp-service.ims.icsi.mmtel\"";
			}

			ua = new UserAgent(sip_providers, user_profile);
			ras = new RegisterAgent(
					sip_providers,
					user_profile.from_url, // modified
					user_profile.contact_url, user_profile.username,
					user_profile.realm, user_profile.passwd, this,
					user_profile, user_profile.qvalue, icsi, user_profile.pub); // added
																				// by
																				// mandrajg
			kas = new KeepAliveSip(sip_providers, 100000);
		} catch (Exception E) {
		}

		register();
		listen();

		return true;
	}

	private String getContactURL(String username, SipProvider sip_provider) {
		int i = username.indexOf("@");
		if (i != -1) {
			// if the username already contains a @
			// strip it and everthing following it
			username = username.substring(0, i);
		}

		return username
				+ "@"
				+ IpAddress.localIpAddress
				+ (sip_provider.getPort() != 0 ? ":" + sip_provider.getPort()
						: "") + ";transport="
				+ sip_provider.getDefaultTransport();
	}

	void setOutboundProxy(SipProvider sip_provider) {
		try {
			if (sip_provider != null)
				sip_provider.setOutboundProxy(new SocketAddress(IpAddress
						.getByName(PreferenceManager
								.getDefaultSharedPreferences(getUIContext())
								.getString(Settings.PREF_DNS,
										Settings.DEFAULT_DNS)), Integer
						.valueOf(PreferenceManager.getDefaultSharedPreferences(
								getUIContext()).getString(
								Settings.PREF_PORT + (""),
								Settings.DEFAULT_PORT))));
		} catch (Exception e) {
		}
	}

	public void CheckEngine() {
		if (sip_providers != null && !sip_providers.hasOutboundProxy())
			setOutboundProxy(sip_providers);
	}

	public Context getUIContext() {
		return Receiver.mContext;
	}

	public int getRemoteVideo() {
		return ua.remote_video_port;
	}

	public int getLocalVideo() {
		return ua.local_video_port;
	}

	public String getRemoteAddr() {
		return ua.remote_media_address;
	}

	public void expire() {
		Receiver.expire_time = 0;

		RegisterAgent ra = ras;
		if (ra != null && ra.CurrentState == RegisterAgent.REGISTERED) {
			ra.CurrentState = RegisterAgent.UNREGISTERED;
			Receiver.onText(Receiver.REGISTER_NOTIFICATION, null, 0, 0);
		}

		register();
	}

	public void unregister() {
		if (user_profile == null || user_profile.username.equals("")
				|| user_profile.realm.equals(""))
			return;

		RegisterAgent ra = ras;
		if (ra != null && ra.unregister()) {
			Receiver.alarm(0, LoopAlarm.class);
			Receiver.onText(Receiver.REGISTER_NOTIFICATION, getUIContext()
					.getString(R.string.reg), R.drawable.sym_presence_idle, 0);
			wl.acquire();
		} else
			Receiver.onText(Receiver.REGISTER_NOTIFICATION, null, 0, 0);
	}

	public void registerMore() {
		IpAddress.setLocalIpAddress();
		RegisterAgent ra = ras;
		try {
			if (user_profile == null || user_profile.username.equals("")
					|| user_profile.realm.equals("")) {
				return;
			}
			user_profile.contact_url = getContactURL(user_profile.from_url,
					sip_providers);

			if (ra != null && !ra.isRegistered() && Receiver.isFast()
					&& ra.register()) {
				Receiver.onText(Receiver.REGISTER_NOTIFICATION, getUIContext()
						.getString(R.string.reg), R.drawable.sym_presence_idle,
						0);
				wl.acquire();
			}
		} catch (Exception ex) {

		}

	}

	public void register() {
		IpAddress.setLocalIpAddress();

		RegisterAgent ra = ras;
		try {
			if (user_profile == null || user_profile.username.equals("")
					|| user_profile.realm.equals("")) {
				return;
			}
			user_profile.contact_url = getContactURL(user_profile.from_url,
					sip_providers);

			if (!Receiver.isFast()) {
				unregister();
			} else {
				if (ra != null && ra.register()) {
					Receiver.onText(Receiver.REGISTER_NOTIFICATION,
							getUIContext().getString(R.string.reg),
							R.drawable.sym_presence_idle, 0);
					wl.acquire();
				}
			}
		} catch (Exception ex) {

		}

	}

	public void registerUdp() {
		IpAddress.setLocalIpAddress();
		int i = 0;
		RegisterAgent ra = ras;
		try {
			if (user_profile == null || user_profile.username.equals("")
					|| user_profile.realm.equals("") || sip_providers == null
					|| sip_providers.getDefaultTransport() == null
					|| sip_providers.getDefaultTransport().equals("tcp")) {
				i++;
				continue;
			}
			user_profile.contact_url = getContactURL(user_profile.from_url,
					sip_providers);

			if (!Receiver.isFast()) {
				unregister();
			} else {
				if (ra != null && ra.register()) {
					Receiver.onText(Receiver.REGISTER_NOTIFICATION + i,
							getUIContext().getString(R.string.reg),
							R.drawable.sym_presence_idle, 0);
					wl.acquire();
				}
			}
		} catch (Exception ex) {

		}

	}

	public void halt() { // modified
		long time = SystemClock.elapsedRealtime();

		RegisterAgent ra = ras;
		unregister();
		while (ra != null && ra.CurrentState != RegisterAgent.UNREGISTERED
				&& SystemClock.elapsedRealtime() - time < 2000)
			try {
				Thread.sleep(100);
			} catch (InterruptedException e1) {
			}
		if (wl.isHeld()) {
			wl.release();
			if (pwl != null && pwl.isHeld())
				pwl.release();
			if (wwl != null && wwl.isHeld())
				wwl.release();
		}
		if (kas != null) {
			Receiver.alarm(0, LoopAlarm.class);
			kas.halt();
		}
		Receiver.onText(Receiver.REGISTER_NOTIFICATION, null, 0, 0);
		if (ra != null)
			ra.halt();
		if (ua != null)
			ua.hangup();
		if (sip_providers != null)
			sip_providers.halt();

	}

	public boolean isRegistered() {
		RegisterAgent ra = ras;
		if (ra != null && ra.isRegistered())
			return true;
		return false;
	}

	public void onUaRegistrationSuccess(RegisterAgent reg_ra,
			NameAddress target, NameAddress contact, String result) {

		RegisterAgent ra = ras;
		if (ra == reg_ra)
			return;

		if (isRegistered()) {
			if (Receiver.on_wlan)
				Receiver.alarm(60, LoopAlarm.class);
			Receiver.onText(Receiver.REGISTER_NOTIFICATION, getUIContext()
					.getString(R.string.regpref),
					R.drawable.sym_presence_available, 0);
			reg_ra.subattempts = 0;
			reg_ra.startMWI();
			Receiver.registered();
		} else
			Receiver.onText(Receiver.REGISTER_NOTIFICATION, null, 0, 0);
		if (wl.isHeld()) {
			wl.release();
			if (pwl != null && pwl.isHeld())
				pwl.release();
			if (wwl != null && wwl.isHeld())
				wwl.release();
		}
	}

	String[] lastmsgs;

	static long lasthalt, lastpwl;

	/** When a UA failed on (un)registering. */
	public void onUaRegistrationFailure(RegisterAgent reg_ra,
			NameAddress target, NameAddress contact, String result) {
		boolean retry = false;
		int i = 0;
		RegisterAgent ra = ras;
		if (ra == reg_ra)
			return;
		if (isRegistered()) {
			reg_ra.CurrentState = RegisterAgent.UNREGISTERED;
			Receiver.onText(Receiver.REGISTER_NOTIFICATION, null, 0, 0);
		} else {
			retry = true;
			Receiver.onText(Receiver.REGISTER_NOTIFICATION, getUIContext()
					.getString(R.string.regfailed) + " (" + result + ")",
					R.drawable.sym_presence_away, 0);
		}
		if (retry) {
			retry = false;
			if (SystemClock.uptimeMillis() > lastpwl + 45000) {
				if (pwl != null && !pwl.isHeld()) {
					if ((!Receiver.on_wlan && Build.MODEL.contains("HTC One"))
							|| (Receiver.on_wlan && wwl == null)) {
						pwl.acquire();
						retry = true;
					}
				}
				if (wwl != null && !wwl.isHeld() && Receiver.on_wlan) {
					wwl.acquire();
					retry = true;
				}
			}
		}
		if (retry) {
			lastpwl = SystemClock.uptimeMillis();
			if (wl.isHeld()) {
				wl.release();
			}
			register();
			if (!wl.isHeld() && pwl != null && pwl.isHeld())
				pwl.release();
			if (!wl.isHeld() && wwl != null && wwl.isHeld())
				wwl.release();
		} else if (wl.isHeld()) {
			wl.release();
			if (pwl != null && pwl.isHeld())
				pwl.release();
			if (wwl != null && wwl.isHeld())
				wwl.release();
		}
		if (SystemClock.uptimeMillis() > lasthalt + 45000) {
			lasthalt = SystemClock.uptimeMillis();
			sip_providers.haltConnections();
		}
		if (!Thread.currentThread().getName().equals("main"))
			updateDNS();
		reg_ra.stopMWI();
		WifiManager wm = (WifiManager) Receiver.mContext
				.getSystemService(Context.WIFI_SERVICE);
		wm.startScan();
	}

	public void updateDNS() {
		Editor edit = PreferenceManager.getDefaultSharedPreferences(
				getUIContext()).edit();

		SipProvider sip_provider = sip_providers;
		try {
			edit.putString(
					Settings.PREF_DNS,
					IpAddress.getByName(
							PreferenceManager.getDefaultSharedPreferences(
									getUIContext()).getString(
									Settings.PREF_SERVER, "")).toString());
		} catch (UnknownHostException e1) {

		}
		edit.commit();
		setOutboundProxy(sip_provider);

	}

	/** Receives incoming calls (auto accept) */
	public void listen() {

		if (ua != null) {
			ua.printLog("UAS: WAITING FOR INCOMING CALL");

			if (!ua.user_profile.audio && !ua.user_profile.video) {
				ua.printLog("ONLY SIGNALING, NO MEDIA");
			}

			ua.listen();
		}

	}

	public void info(char c, int duration) {
		ua.info(c, duration);
	}

	/** Makes a new call */
	public boolean call(String target_url, boolean force) {

		boolean found = false;

		if (isRegistered() && Receiver.isFast())
			found = true;
		else {

			if (isRegistered() && Receiver.isFast()) {
				found = true;
			}
			if (!found && force) {

				if (Receiver.isFast())
					found = true;
				else

				if (Receiver.isFast()) {
					found = true;
				}
			}
		}

		if (!found || (ua) == null) {
			if (PreferenceManager.getDefaultSharedPreferences(getUIContext())
					.getBoolean(Settings.PREF_CALLBACK,
							Settings.DEFAULT_CALLBACK)
					&& PreferenceManager
							.getDefaultSharedPreferences(getUIContext())
							.getString(Settings.PREF_POSURL,
									Settings.DEFAULT_POSURL).length() > 0) {
				Receiver.url("n=" + Uri.decode(target_url));
				return true;
			}
			return false;
		}

		ua.printLog("UAC: CALLING " + target_url);

		if (!ua.user_profile.audio && !ua.user_profile.video) {
			ua.printLog("ONLY SIGNALING, NO MEDIA");
		}
		return ua.call(target_url, false);
	}

	public void answercall() {
		Receiver.stopRingtone();
		ua.accept();
	}

	public void rejectcall() {
		ua.printLog("UA: HANGUP");
		ua.hangup();
	}

	public void togglehold() {
		ua.reInvite(null, 0);
	}

	public void transfer(String number) {
		ua.callTransfer(number, 0);
	}

	public void togglemute() {
		if (ua.muteMediaApplication())
			Receiver.onText(Receiver.CALL_NOTIFICATION, getUIContext()
					.getString(R.string.menu_mute),
					android.R.drawable.stat_notify_call_mute,
					Receiver.ccCall.base);
		else
			Receiver.progress();
	}

	public int speaker(int mode) {
		int ret = ua.speakerMediaApplication(mode);
		Receiver.progress();
		return ret;
	}

	public void keepAlive() {
		KeepAliveSip ka = kas;
		if (ka != null && Receiver.on_wlan && isRegistered())
			try {
				ka.sendToken();
				Receiver.alarm(60, LoopAlarm.class);
			} catch (IOException e) {
				if (!Sipdroid.release)
					e.printStackTrace();
			}
	}

	@Override
	public void onMWIUpdate(RegisterAgent ra, boolean voicemail, int number,
			String vmacc) {
		// TODO Auto-generated method stub

	}
}
