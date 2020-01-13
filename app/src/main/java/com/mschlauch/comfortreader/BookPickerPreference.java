/**
 * This file is part of Comfort Reader.
 * <p>
 * LICENSE
 * Copyright 2014-2017 Michael Schlauch
 * <p>
 * Comfort Reader is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p>
 * Comfort Reader is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License
 * along with Comfort Reader.  If not, see <http://www.gnu.org/licenses/>.>.
 */

package com.mschlauch.comfortreader;

import android.content.Context;
import android.preference.Preference;

import com.github.angads25.filepicker.view.FilePickerPreference;

/**
 * Created by michael on 31.01.17.
 */

public class BookPickerPreference extends FilePickerPreference implements Preference.OnPreferenceChangeListener {

    public BookPickerPreference(Context context) {
        super(context);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object o) {
        if (preference.getKey().equals("your_preference_key")) {
            String value = (String) o;
            String[] arr = value.split(":");

        }
        return false;
    }

}
