/**
 * Copyright 2013 Alex Yanchenko
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 */
package com.deathsnacks.wardroid.utils.preferences;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.preference.ListPreference;
import android.util.AttributeSet;
import android.widget.ListView;

import com.deathsnacks.wardroid.R;
import com.deathsnacks.wardroid.utils.PreferenceUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

// android:defaultValue="entryValue1|entryValue2"
public class MultiSelectListPreference extends ListPreference {

    public CharSequence[] getCheckedEntries() {
        CharSequence[] entries = getEntries();
        ArrayList<CharSequence> checkedEntries = new ArrayList<CharSequence>();
        for (int i = 0; i < entries.length; i++) {
            if (checkedEntryIndexes[i]) {
                checkedEntries.add(entries[i]);
            }
        }
        return checkedEntries.toArray(new String[checkedEntries.size()]);
    }

    // boring stuff

    private boolean[] checkedEntryIndexes;
    private boolean enableToggle;
    private Context context;

    public MultiSelectListPreference(Context context, AttributeSet attrs) {
        super(context, attrs);

        this.context = context;
        enableToggle = true;
        for (int i = 0; i < attrs.getAttributeCount(); i++) {
            String attr = attrs.getAttributeName(i);
            String val = attrs.getAttributeValue(i);
            if (attr.equalsIgnoreCase("toggleAll")) {
                if (val.equalsIgnoreCase("false"))
                    enableToggle = false;
            }
        }
    }

    public MultiSelectListPreference(Context context) {
        super(context);
        this.context = context;
    }

    @Override
    public void setEntries(CharSequence[] entries) {
        super.setEntries(entries);
        updateCheckedEntryIndexes();
    }

    @Override
    public void setValue(String value) {
        super.setValue(value);
        updateCheckedEntryIndexes();
    }

    @Override
    protected void onPrepareDialogBuilder(Builder builder) {
        updateCheckedEntryIndexes();
        List<CharSequence> entriesList = new ArrayList<CharSequence>();
        entriesList.addAll(Arrays.asList(getEntries()));
        CharSequence[] entries = entriesList.toArray(new CharSequence[entriesList.size()]);
        builder.setMultiChoiceItems(entries, checkedEntryIndexes,
                new OnMultiChoiceClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which,
                                        boolean isChecked) {
                        if (enableToggle) {
                            if (which == 0) {
                                ListView list = ((AlertDialog) dialog).getListView();
                                for (int i = 1; i < list.getCount(); i++) {
                                    list.setItemChecked(i, isChecked);
                                    checkedEntryIndexes[i] = isChecked;
                                }
                                return;
                            }
                        }
                        checkedEntryIndexes[which] = isChecked;
                    }
                });
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        if (positiveResult) {
            CharSequence[] entryVals = getEntryValues();
            ArrayList<CharSequence> checkedVals = new ArrayList<CharSequence>();
            for (int i = enableToggle ? 1 : 0; i < entryVals.length; i++) {
                if (checkedEntryIndexes[i]) {
                    checkedVals.add(entryVals[i]);
                }
            }
            String val = PreferenceUtils.toPersistedPreferenceValue(checkedVals
                    .toArray(new CharSequence[checkedVals.size()]));
            if (callChangeListener(val)) {
                setValue(val);
            }
        }
    }

    private void updateCheckedEntryIndexes() {
        CharSequence[] entryVals = getEntryValues();
        checkedEntryIndexes = new boolean[entryVals.length];
        String val = getValue();
        if (val != null) {
            HashSet<String> checkedEntryVals = new HashSet<String>(
                    Arrays.asList(PreferenceUtils.fromPersistedPreferenceValue(val)));
            for (int i = 0; i < entryVals.length; i++) {
                checkedEntryIndexes[i] = checkedEntryVals
                        .contains(entryVals[i]);
            }
        }
    }

    @Override
    public CharSequence[] getEntries() {
        List<CharSequence> entries = new ArrayList<CharSequence>();
        if (enableToggle)
            entries.add(context.getString(R.string.toggle_all));
        entries.addAll(Arrays.asList(super.getEntries()));
        return entries.toArray(new CharSequence[entries.size()]);
    }

    @Override
    public CharSequence[] getEntryValues() {
        List<CharSequence> entries = new ArrayList<CharSequence>();
        if (enableToggle)
            entries.add(context.getString(R.string.toggle_all));
        entries.addAll(Arrays.asList(super.getEntryValues()));
        return entries.toArray(new CharSequence[entries.size()]);
    }
}