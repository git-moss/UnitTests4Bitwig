// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2019-2020
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.module;

import de.mossgrabers.TestFramework;

import com.bitwig.extension.controller.api.ControllerHost;
import com.bitwig.extension.controller.api.Groove;


/**
 * Module tests for the Groove class.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class GrooveModule extends TestModule
{
    /**
     * Constructor.
     */
    public GrooveModule ()
    {
        super ("Groove");
    }


    /** {@inheritDoc} */
    @Override
    public boolean registerTests (final TestFramework tf, final ControllerHost host)
    {
        if (!super.registerTests (tf, host))
            return false;

        final Groove groove = host.createGroove ();
        tf.assertNotNull ("Groove not created.", groove);

        tf.testParameter ("groove.getEnabled", groove.getEnabled (), Double.valueOf (0.0), Double.valueOf (0.0), Double.valueOf (1.0), Double.valueOf (1.0), "Off", "Groove on/off");
        tf.testParameter ("groove.getShuffleAmount", groove.getShuffleAmount (), Double.valueOf (0.5), Double.valueOf (0.0), Double.valueOf (1.0), Double.valueOf (0.6), "50.0 %", "Shuffle amount");
        tf.testParameter ("groove.getShuffleRate", groove.getShuffleRate (), Double.valueOf (1.0), Double.valueOf (0.0), Double.valueOf (1.0), Double.valueOf (1.0), "1/16", "Shuffle rate");
        tf.testParameter ("groove.getAccentAmount", groove.getAccentAmount (), Double.valueOf (1.0), Double.valueOf (0.0), Double.valueOf (1.0), Double.valueOf (1.0), "100 %", "Accent amount");
        tf.testParameter ("groove.getAccentRate", groove.getAccentRate (), Double.valueOf (0.0), Double.valueOf (0.0), Double.valueOf (1.0), Double.valueOf (1.0), "1/4", "Accent rate");
        tf.testParameter ("groove.getAccentPhase", groove.getAccentPhase (), Double.valueOf (0.5), Double.valueOf (0.0), Double.valueOf (1.0), Double.valueOf (1.0), "0.00 %", "Accent phase");

        return true;
    }
}
