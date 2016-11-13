package lt.tomzmt.coinalbum.data.entity;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import lt.tomzmt.coinalbum.data.CursorReader;

/**
 * Coin entity
 * Created by t.zemaitis on 2014.12.06.
 */
public class Coin implements Entity, Parcelable {

    public static final String TABLE_NAME = Coin.class.getSimpleName();

    public static final String NAME = "name";

    public static final String DENOMINATION = "denomination";

    public static final String YEARS = "years";

    public static final String COUNTRY = "country";

    public static final String COMPOSITION = "composition";

    public static final String DIAMETER = "diameter";

    public static final String MASS = "mass";

    public static final String EDGE = "edge";

    public static final String MINT = "mint";

    public static final String MINTAGE = "mintage";

    public static final String DESCRIPTION = "description";

    private long mId = NOT_SET;

    private String mName;

    private String mDenomination;

    private String mYears;

    private String mCountry;

    private String mComposition;

    private String mDiameter;

    private String mMass;

    private String mEdge;

    private String mMint;

    private String mMintage;

    private String mDescription;

    public static Creator<Coin> CREATOR = new Creator<Coin>() {
        @Override
        public Coin createFromParcel(Parcel source) {
            return new Coin(source);
        }

        @Override
        public Coin[] newArray(int size) {
            return new Coin[size];
        }
    };

    public Coin() {
    }

    public Coin(Parcel in) {
        mId = in.readLong();
        mName = in.readString();
        mDenomination = in.readString();
        mYears = in.readString();
        mCountry = in.readString();
        mComposition = in.readString();
        mDiameter = in.readString();
        mMass = in.readString();
        mEdge = in.readString();
        mMint = in.readString();
        mMintage = in.readString();
        mDescription = in.readString();
    }

    public Coin(Cursor cursor) {
        CursorReader reader = new CursorReader(cursor);
        mId = reader.readLong(ID);
        mName = reader.readString(NAME);
        mDenomination = reader.readString(DENOMINATION);
        mYears = reader.readString(YEARS);
        mCountry = reader.readString(COUNTRY);
        mComposition = reader.readString(COMPOSITION);
        mDiameter = reader.readString(DIAMETER);
        mMass = reader.readString(MASS);
        mEdge = reader.readString(EDGE);
        mMint = reader.readString(MINT);
        mMintage = reader.readString(MINTAGE);
        mDescription = reader.readString(DESCRIPTION);
    }

    @Override
    public long getId() {
        return mId;
    }

    @Override
    public ContentValues getValues() {
        ContentValues values = new ContentValues();
        if (mId != NOT_SET) {
            values.put(ID, mId);
        }

        values.put(NAME, mName);
        values.put(DENOMINATION, mDenomination);
        values.put(YEARS, mYears);
        values.put(COUNTRY, mCountry);
        values.put(COMPOSITION, mComposition);
        values.put(DIAMETER, mDiameter);
        values.put(MASS, mMass);
        values.put(EDGE, mEdge);
        values.put(MINT, mMint);
        values.put(MINTAGE, mMintage);
        values.put(DESCRIPTION, mDescription);

        return values;
    }

    @Override
    public String getTableName() {
        return TABLE_NAME;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeLong(mId);
        out.writeString(mName);
        out.writeString(mDenomination);
        out.writeString(mYears);
        out.writeString(mCountry);
        out.writeString(mComposition);
        out.writeString(mDiameter);
        out.writeString(mMass);
        out.writeString(mEdge);
        out.writeString(mMint);
        out.writeString(mMintage);
        out.writeString(mDescription);
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getDenomination() {
        return mDenomination;
    }

    public void setDenomination(String denomination) {
        mDenomination = denomination;
    }

    public String getYears() {
        return mYears;
    }

    public void setYears(String years) {
        mYears = years;
    }

    public void setId(long id) {
        mId = id;
    }

    public String getCountry() {
        return mCountry;
    }

    public void setCountry(String country) {
        mCountry = country;
    }

    public String getComposition() {
        return mComposition;
    }

    public void setComposition(String composition) {
        mComposition = composition;
    }

    public String getDiameter() {
        return mDiameter;
    }

    public void setDiameter(String diameter) {
        mDiameter = diameter;
    }

    public String getMass() {
        return mMass;
    }

    public void setMass(String mass) {
        mMass = mass;
    }

    public String getEdge() {
        return mEdge;
    }

    public void setEdge(String edge) {
        mEdge = edge;
    }

    public String getMint() {
        return mMint;
    }

    public void setMint(String mint) {
        mMint = mint;
    }

    public String getMintage() {
        return mMintage;
    }

    public void setMintage(String mintage) {
        mMintage = mintage;
    }

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String description) {
        mDescription = description;
    }
}
