package com.ideabytes.qezytv.genericplayer.channel;

public class ChannelInfo {

	private String channelId;
	private String channelName;
	private String channelLink;
	private String channelLogo;
	private String accessToken;
	private String viewingDeviceId;
	private String deviceId;

	public ChannelInfo(String channelId, String channelName,
			String channelLink, String channelLogo, String accessToken,
			String viewingDeviceId,String deviceId) {
		this.channelId = channelId;
		this.channelName = channelName;
		this.channelLink = channelLink;
		this.channelLogo = channelLogo;
		this.accessToken = accessToken;
		this.viewingDeviceId = viewingDeviceId;
		this.deviceId = deviceId;

	}

	public String getChannelId() {
		return channelId;
	}

	public String getChannelLink() {
		return channelLink;
	}

	public String getChannelLogo() {
		return channelLogo;
	}

	public String getChannelName() {
		return channelName;
	}

	public String getAccessToken() {
		return accessToken;
	}

	public String getViewingDeviceId() {
		return viewingDeviceId;
	}
	public String getDeviceId() {
		return deviceId;
	}
}
