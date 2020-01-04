package com.max.memo3.Util;

public class Custom_Notification_Channel {
    public String channel_id;
    public CharSequence name;
    public String description;
    public int importance;

    public Custom_Notification_Channel() {
    }

    public Custom_Notification_Channel(String channel_id, CharSequence name, String description, int importance) {
        this.channel_id = channel_id;
        this.name = name;
        this.description = description;
        this.importance = importance;
    }

    public String getChannel_id() {
        return channel_id;
    }

    public void setChannel_id(String channel_id) {
        this.channel_id = channel_id;
    }

    public CharSequence getName() {
        return name;
    }

    public void setName(CharSequence name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getImportance() {
        return importance;
    }

    public void setImportance(int importance) {
        this.importance = importance;
    }
}
