package ezviz.ezopensdk.been;

import android.os.Parcel;
import android.os.Parcelable;

public class Temp implements Parcelable {
    //九支渠隆锦路站
    private String temp;//温度
    private String Ph;//ph(6-9)
    private String Oxygen;//溶解氧(>=2)
    private String Nitrogen;//氨氮(<=2)
    private String Permanganate;//高锰酸盐(<=15)
    private String Phosphorus;//总磷(<=0.4)
    private String Potential;//氧化还原定位(>=50)
    private String Time;//时间

    public Temp() {
    }

    public Temp(String temp, String ph, String oxygen, String nitrogen, String permanganate, String phosphorus, String potential, String time) {
        this.temp = temp;
        Ph = ph;
        Oxygen = oxygen;
        Nitrogen = nitrogen;
        Permanganate = permanganate;
        Phosphorus = phosphorus;
        Potential = potential;
        Time = time;
    }

    protected Temp(Parcel in) {
        temp = in.readString();
        Ph = in.readString();
        Oxygen = in.readString();
        Nitrogen = in.readString();
        Permanganate = in.readString();
        Phosphorus = in.readString();
        Potential = in.readString();
        Time = in.readString();
    }

    public static final Creator<Temp> CREATOR = new Creator<Temp>() {
        @Override
        public Temp createFromParcel(Parcel in) {
            return new Temp(in);
        }

        @Override
        public Temp[] newArray(int size) {
            return new Temp[size];
        }
    };

    public String getTemp() {
        return temp;
    }

    public String getPh() {
        return Ph;
    }

    public String getOxygen() {
        return Oxygen;
    }

    public String getNitrogen() {
        return Nitrogen;
    }

    public String getPermanganate() {
        return Permanganate;
    }

    public String getPhosphorus() {
        return Phosphorus;
    }

    public String getPotential() {
        return Potential;
    }

    public String getTime() {
        return Time;
    }

    public void setTemp(String temp) {
        this.temp = temp;
    }

    public void setPh(String ph) {
        Ph = ph;
    }

    public void setOxygen(String oxygen) {
        Oxygen = oxygen;
    }

    public void setNitrogen(String nitrogen) {
        Nitrogen = nitrogen;
    }

    public void setPermanganate(String permanganate) {
        Permanganate = permanganate;
    }

    public void setPhosphorus(String phosphorus) {
        Phosphorus = phosphorus;
    }

    public void setPotential(String potential) {
        Potential = potential;
    }

    public void setTime(String time) {
        Time = time;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(temp);
        parcel.writeString(Ph);
        parcel.writeString(Oxygen);
        parcel.writeString(Nitrogen);
        parcel.writeString(Permanganate);
        parcel.writeString(Phosphorus);
        parcel.writeString(Potential);
        parcel.writeString(Time);
    }
}
