// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2019-2020
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers;

import com.bitwig.extension.controller.api.SettableEnumValue;


/**
 * Helper class for a boolean setting.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class BooleanSetting
{
    /** The two On and Off options. */
    public static final String [] OPTIONS =
    {
        "Off",
        "On"
    };


    /**
     * Constructor. Private due to utility class.
     */
    private BooleanSetting ()
    {
        // Intentionally empty
    }


    /**
     * Test if the setting is true (on).
     *
     * @param enumSetting The setting
     * @return True or false
     */
    public static boolean isTrue (final SettableEnumValue enumSetting)
    {
        return OPTIONS[1].equals (enumSetting.get ());
    }
}
