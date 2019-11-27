package org.am.producers.maas;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 
 * @author DineshChaudhari
 *
 */
final public class AppsEventModel {
	
	private String appEventGuid;
	private Long billingID;
	private Long deviceId;

	@JsonCreator
	public AppsEventModel(@JsonProperty("appEventGuid") String appEventGuid,
                          @JsonProperty("billingId")Long billingID,
                          @JsonProperty("deviceId") Long deviceId) {
		this.appEventGuid = appEventGuid;
		this.billingID = billingID;
		this.deviceId = deviceId;
	}

	public String getAppEventGuid() {
		return appEventGuid;
	}
	public Long getBillingID() {
		return billingID;
	}
	public Long getDeviceId() {
		return deviceId;
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder("AppsEventModel{");
		sb.append("appEventGuid='").append(appEventGuid).append('\'');
		sb.append(", billingID=").append(billingID);
		sb.append(", deviceId=").append(deviceId);
		sb.append('}');
		return sb.toString();
	}
}
