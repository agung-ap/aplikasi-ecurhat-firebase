package id.developer.fauzan.ecurhat.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Posting implements Parcelable {
    private String key;
    private String dari;
    private String untuk;
    private String pesan;

    public Posting() {
    }

    protected Posting(Parcel in) {
        key = in.readString();
        dari = in.readString();
        untuk = in.readString();
        pesan = in.readString();
    }

    public static final Creator<Posting> CREATOR = new Creator<Posting>() {
        @Override
        public Posting createFromParcel(Parcel in) {
            return new Posting(in);
        }

        @Override
        public Posting[] newArray(int size) {
            return new Posting[size];
        }
    };

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getDari() {
        return dari;
    }

    public void setDari(String dari) {
        this.dari = dari;
    }

    public String getUntuk() {
        return untuk;
    }

    public void setUntuk(String untuk) {
        this.untuk = untuk;
    }

    public String getPesan() {
        return pesan;
    }

    public void setPesan(String pesan) {
        this.pesan = pesan;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(key);
        parcel.writeString(dari);
        parcel.writeString(untuk);
        parcel.writeString(pesan);
    }
}
