// IAdd.aidl
package com.borqs.serveraidl;
import com.borqs.serveraidl.Person;

// Declare any non-default types here with import statements

interface IAdd {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    int add(int number1, int number2);
    List<String> getStringList();
    List<Person> getPersonList();
    void placeCall(String number);
}
