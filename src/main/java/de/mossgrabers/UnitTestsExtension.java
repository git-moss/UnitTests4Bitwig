// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers;

import de.mossgrabers.module.ApplicationModule;
import de.mossgrabers.module.ArrangerModule;
import de.mossgrabers.module.BrowserModule;
import de.mossgrabers.module.CursorClipModule;
import de.mossgrabers.module.GrooveModule;
import de.mossgrabers.module.MixerModule;
import de.mossgrabers.module.NoteRepeatModule;
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
        final SettableEnumValue getterSetting = preferences.getEnumSetting ("Test value getters", CATEGORY_GLOBAL, BooleanSetting.OPTIONS, BooleanSetting.OPTIONS[1]);
        getterSetting.markInterested ();
        final SettableEnumValue observerSetting = preferences.getEnumSetting ("Test value observers", CATEGORY_GLOBAL, BooleanSetting.OPTIONS, BooleanSetting.OPTIONS[1]);
        observerSetting.markInterested ();

        this.tf.setSettings (getterSetting, observerSetting);

        this.logger.setLogLevel (loggingSetting);
        this.logger.infoLine ();
        for (final TestModule module: this.modules)
            module.registerTests (this.tf, this.host);
        this.logger.infoLine ();

        this.getHost ().println ("Initialized.");

        this.host.scheduleTask (this.tf::executeScheduler, 1000);
    }


    /** {@inheritDoc} */
    @Override
    public void exit ()
    {
        this.getHost ().println ("Exited.");
    }


    /** {@inheritDoc} */
    @Override
    public void flush ()
    {
        // Intentionally empty
    }
}
