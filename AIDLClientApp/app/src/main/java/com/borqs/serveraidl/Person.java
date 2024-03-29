package com.borqs.serveraidl;

import android.os.Parcel;
import android.os.Parcelable;

public class Person implements Parcelable {

    public String name;
    public int age;

    public Person() {
    }

    Person(String name, int age) {
        this.name = name;
        this.age = age;
    }

    Person(Parcel in) {
        this.name = in.readString();
        this.age = in.readInt();
    }

    public static final Creator<Person> CREATOR = new Creator<Person>() {
        @Override
        public Person createFromParcel(Parcel in) {
            return new Person(in);
        }

        @Override
        public Person[] newArray(int size) {
            return new Person[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeInt(age);
    }
}
