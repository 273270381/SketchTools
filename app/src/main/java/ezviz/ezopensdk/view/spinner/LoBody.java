package ezviz.ezopensdk.view.spinner;

import android.os.Parcel;
import android.os.Parcelable;

public class LoBody implements Parcelable {
    private String name;
    private String address;
    private String Preset;
    private String No;
    private String ip;
    private String port;

    public LoBody() {
    }

    public LoBody(String name, String address, String preset) {
        this.name = name;
        this.address = address;
        Preset = preset;
    }

    public LoBody(String name, String address, String preset, String no, String ip, String port) {
        this.name = name;
        this.address = address;
        Preset = preset;
        No = no;
        this.ip = ip;
        this.port = port;
    }

    protected LoBody(Parcel in) {
        name = in.readString();
        address = in.readString();
        Preset = in.readString();
        No = in.readString();
        ip = in.readString();
        port = in.readString();
    }

    public static final Creator<LoBody> CREATOR = new Creator<LoBody>() {
        @Override
        public LoBody createFromParcel(Parcel in) {
            return new LoBody(in);
        }

        @Override
        public LoBody[] newArray(int size) {
            return new LoBody[size];
        }
    };

    public void setNo(String no) {
        No = no;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getNo() {
        return No;
    }

    public String getIp() {
        return ip;
    }

    public String getPort() {
        return port;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public String getPreset() {
        return Preset;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setPreset(String preset) {
        Preset = preset;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(address);
        dest.writeString(Preset);
        dest.writeString(No);
        dest.writeString(ip);
        dest.writeString(port);
    }
}
