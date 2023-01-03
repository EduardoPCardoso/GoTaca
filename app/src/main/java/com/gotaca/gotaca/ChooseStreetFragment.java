package com.gotaca.gotaca;

import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.PreferenceFragment;
import android.util.Log;

public class ChooseStreetFragment extends PreferenceFragment {

    private static String currentEntry;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.choose_street_fragment);
        //getPreference();
    }

    /*@Override
    public void onPause() {
        super.onPause();
        Intent intent = new Intent(getActivity(), MainActivity.class);
        getActivity().startActivity(intent);
    }*/

    //Receber a preferência escolhida.

    @Override
    public void onPause() {
        super.onPause();
        getPreference();
        Log.d("TESTE2", "onPause");
    }

    private void getPreference() {
        ListPreference listPreference = (ListPreference) findPreference ("choose_street");
        CharSequence currentEntryChar = listPreference.getEntry();
        currentEntry = String.valueOf(currentEntryChar);
    }

    /*@Override
    public void onDestroy(){
        super.onDestroy();
        backtomain();
    }

    private void backtomain() {
        Intent intent = new Intent(getActivity(), MainActivity.class);
        getActivity().startActivity(intent);
    }*/

    public static String getCurrentEntry(){
        return currentEntry;
    }
}

