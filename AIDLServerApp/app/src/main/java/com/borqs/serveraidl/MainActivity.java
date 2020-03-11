package com.borqs.serveraidl;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static List<String> country;
    private static List<Person> person;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public static List<String> getList() {
        country = new ArrayList<>();
        country.add("India");
        country.add("Bhutan");
        country.add("Nepal");
        country.add("USA");
        country.add("Canada");
        country.add("China");
        return country;
    }

    public static List<Person> getPersons() {
        person = new ArrayList<>();
        person.add(new Person("A", 10));
        person.add(new Person("B", 20));
        person.add(new Person("C", 30));
        person.add(new Person("D", 40));
        person.add(new Person("E", 50));
        person.add(new Person("F", 60));
        return person;
    }


}
