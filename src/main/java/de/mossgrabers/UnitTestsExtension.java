// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2019-2020
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers;

import de.mossgrabers.module.ApplicationModule;
import de.mossgrabers.module.ArrangerModule;
import de.mossgrabers.module.BrowserModule;
import de.mossgrabers.module.CursorClipModule;
import de.mossgrabers.module.GrooveModule;
import de.mossgrabers.module.MixerModule;
import de.mossgrabers.module.NoteRepeatModule;
import de.mossgrabers.module.ProjectModule;
import de.mossgrabers.module.SceneModule;
import de.mossgrabers.module.TestModule;
import de.mossgrabers.module.TrackBankModule;
import de.mossgrabers.module.TransportModule;

import com.bitwig.extension.controller.ControllerExtension;
import com.bitwig.extension.controller.api.ControllerHost;
import com.bitwig.extension.controller.api.Preferences;
import com.bitwig.extension.controller.api.SettableEnumValue;

import java.util.ArrayList;
import java.util.List;


/**
 * Extension for testing the Bitwig Studio API.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class UnitTestsExtension extends ControllerExtension
{
    private static final String    CATEGORY_GLOBAL = "Global";
    private static final String [] VALUE_ACCESS    =
    {
        "Getter",
        "Observer",
        "Both"
    };

    private final ConsoleLogger    logger;
    private final ControllerHost   host;
    private final TestFramework    tf;
    private final List<TestModule> modules         = new ArrayList<> ();


    /**
     * Constructor.
     *
     * @param extensionDefinition The extension definition
     * @param host The Bitwig host
     */
    protected UnitTestsExtension (final UnitTestsExtensionDefinition extensionDefinition, final ControllerHost host)
    {
        super (extensionDefinition, host);
        this.host = host;
        this.logger = new ConsoleLogger (host);
        this.tf = new TestFramework (host, this.logger);

        this.modules.add (new ApplicationModule ());
        this.modules.add (new ProjectModule ());
        this.modules.add (new ArrangerModule ());
        this.modules.add (new MixerModule ());
        this.modules.add (new TransportModule ());
        this.modules.add (new GrooveModule ());
        this.modules.add (new SceneModule ());
        this.modules.add (new CursorClipModule ());
        this.modules.add (new BrowserModule ());
        this.modules.add (new TrackBankModule ());
        this.modules.add (new NoteRepeatModule ());
    }


    /** {@inheritDoc} */
    @Override
    public void init ()
    {
        final Preferences preferences = this.host.getPreferences ();
        final SettableEnumValue loggingSetting = preferences.getEnumSetting ("Log all details (fine)", CATEGORY_GLOBAL, BooleanSetting.OPTIONS, BooleanSetting.OPTIONS[1]);
        loggingSetting.markInterested ();
        final SettableEnumValue valueAccessSetting = preferences.getEnumSetting ("Test value access", CATEGORY_GLOBAL, VALUE_ACCESS, VALUE_ACCESS[2]);
        valueAccessSetting.markInterested ();

        this.tf.setSettings (valueAccessSetting);

        this.logger.setLogLevel (loggingSetting);
        this.getHost ().println ("");
        this.logger.header ("Initializing test modules");
        boolean hasTests = false;
        for (final TestModule module: this.modules)
        {
            if (module.registerTests (this.tf, this.host))
                hasTests = true;
        }
        if (!hasTests)
            this.getHost ().println ("All test modules are disabled.");

        this.host.scheduleTask (this.tf::executeScheduler, 1000);
    }


    /** {@inheritDoc} */
    @Override
    public void exit ()
    {
        // Intentionally empty
    }


    /** {@inheritDoc} */
    @Override
    public void flush ()
    {
        // Intentionally empty
    }
}
