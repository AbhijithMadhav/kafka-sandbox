package org.am.producers.test;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;

final public class AppEvent {

    private final String appEventGuid;
    private final String billingId;
    private final Long deviceId;
    private final Platform platform;

    @JsonCreator
    public AppEvent(@JsonProperty("appEventGuid") String appEventGuid,
                    @JsonProperty("billingId")String billingID,
                    @JsonProperty("deviceId") Long deviceId,
                    @JsonProperty("platform") String platform) {
        this.appEventGuid = appEventGuid;
        this.billingId = billingID;
        this.deviceId = deviceId;
        this.platform = Platform.toPlatform(platform);
    }

    public String getAppEventGuid() {
        return appEventGuid;
    }
    public String getBillingId() {
        return billingId;
    }
    public Long getDeviceId() {
        return deviceId;
    }
    public Platform getPlatform() {
        return platform;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", AppEvent.class.getSimpleName() + "[", "]")
                .add("appEventGuid='" + appEventGuid + "'")
                .add("billingId='" + billingId + "'")
                .add("deviceId=" + deviceId)
                .add("platform=" + platform)
                .toString();
    }

    public enum Platform {
        IOS, ANDROID, WINDOWS, MAC;

        private static Map<String, Platform> map = new HashMap<>();
        static {
            Arrays.asList(Platform.values()).forEach(p -> map.put(p.name(), p));
        }

        public static Platform toPlatform(String platformString) {
            return map.get(platformString);
        }
    }
}
