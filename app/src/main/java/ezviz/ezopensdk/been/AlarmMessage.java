package ezviz.ezopensdk.been;

import android.os.Parcel;
import android.os.Parcelable;

public class AlarmMessage implements Parcelable {
    /**
     * 预警信息内容
     */
    private String message;
    /**
     * 消息类型 0:渣土车识别定位跟踪 1:违法乱建  2:违章种植  3:秸秆焚烧  4:河道监测  5:园区企业监管
     */
    private String type;
    /**
     * 设备通道号
     */
    private String channelNumber;
    /**
     * 纬度
     */
    private String latitude;
    /**
     * 经度
     */
    private String longitude;
    /**
     * 高程
     */
    private String altitude;
    /**
     * 地址
     */
    private String address;
    /**
     * 图片地址
     */
    private String imgPath;
    /**
     * 创建时间
     */
    private String createTime;
    /**
     * 开始时间
     */
    private String startTime;
    /**
     * 结束时间
     */
    private String endTime;

    private String videoPath;

    private String isPush;
    private String id;

    public AlarmMessage() {
    }

    public AlarmMessage(String message, String type, String latitude, String longitude, String altitude, String address, String imgPath, String createTime,
                        String startTime, String endTime, String videoPath, String channelNumber) {
        this.message = message;
        this.type = type;
        this.latitude = latitude;
        this.longitude = longitude;
        this.altitude = altitude;
        this.address = address;
        this.imgPath = imgPath;
        this.createTime = createTime;
        this.startTime = startTime;
        this.endTime = endTime;
        this.videoPath = videoPath;
        this.channelNumber = channelNumber;
    }

    public AlarmMessage(String message, String type , String latitude, String longitude, String altitude,
                        String address, String imgPath, String createTime, String startTime, String endTime, String videoPath, String isPush, String id, String channelNumber) {
        this.message = message;
        this.type = type;
        this.latitude = latitude;
        this.longitude = longitude;
        this.altitude = altitude;
        this.address = address;
        this.imgPath = imgPath;
        this.createTime = createTime;
        this.startTime = startTime;
        this.endTime = endTime;
        this.videoPath = videoPath;
        this.isPush = isPush;
        this.channelNumber = channelNumber;
    }

    protected AlarmMessage(Parcel in) {
        message = in.readString();
        type = in.readString();
        channelNumber = in.readString();
        latitude = in.readString();
        longitude = in.readString();
        altitude = in.readString();
        address = in.readString();
        imgPath = in.readString();
        createTime = in.readString();
        startTime = in.readString();
        endTime = in.readString();
        videoPath = in.readString();
        isPush = in.readString();
        id = in.readString();
    }

    public static final Creator<AlarmMessage> CREATOR = new Creator<AlarmMessage>() {
        @Override
        public AlarmMessage createFromParcel(Parcel in) {
            return new AlarmMessage(in);
        }

        @Override
        public AlarmMessage[] newArray(int size) {
            return new AlarmMessage[size];
        }
    };

    public String getChannelNumber() {
        return channelNumber;
    }

    public String getMessage() {
        return message;
    }

    public String getVideoPath() {
        return videoPath;
    }

    public String getId() {
        return id;
    }

    public String getIsPush() {
        return isPush;
    }

    public void setIsPush(String isPush) {
        this.isPush = isPush;
    }

    public String getType() {
        return type;
    }


    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public String getAltitude() {
        return altitude;
    }

    public String getAddress() {
        return address;
    }

    public String getImgPath() {
        return imgPath;
    }

    public String getCreateTime() {
        return createTime;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setType(String type) {
        this.type = type;
    }


    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public void setAltitude(String altitude) {
        this.altitude = altitude;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setImgPath(String imgPath) {
        this.imgPath = imgPath;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public void setVideoPath(String videoPath) {
        this.videoPath = videoPath;
    }

    public void setChannelNumber(String channelNumber) {
        this.channelNumber = channelNumber;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(message);
        parcel.writeString(type);
        parcel.writeString(channelNumber);
        parcel.writeString(latitude);
        parcel.writeString(longitude);
        parcel.writeString(altitude);
        parcel.writeString(address);
        parcel.writeString(imgPath);
        parcel.writeString(createTime);
        parcel.writeString(startTime);
        parcel.writeString(endTime);
        parcel.writeString(videoPath);
        parcel.writeString(isPush);
        parcel.writeString(id);
    }
}
