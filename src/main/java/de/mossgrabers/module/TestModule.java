// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.module;

import de.mossgrabers.BooleanSetting;
import de.mossgrabers.TestFramework;

import com.bitwig.extension.controller.api.ControllerHost;
import com.bitwig.extension.controller.api.Preferences;
import com.bitwig.extension.controller.api.SettableEnumValue;


/**
 * Base class for test modules.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public abstract class TestModule
{
    protected static final int ANSWER_DELAY = 100;

    private String             moduleName;


    /**
     * Constructor.
     *
     * @param moduleName The name of the module
     */
    public TestModule (final String moduleName)
    {
        this.moduleName = moduleName;
    }


    /**
     * Register the tests of a module.
     *
     * @param tf The API test framework
     * @param host The controller host
     */
    public void registerTests (final TestFramework tf, final ControllerHost host)
    {
        final Preferences preferences = host.getPreferences ();
        final SettableEnumValue setting = preferences.getEnumSetting ("All", this.moduleName, BooleanSetting.OPTIONS, BooleanSetting.OPTIONS[1]);
        setting.markInterested ();
        tf.beginModuleTest (this.moduleName, setting);
    }
}
