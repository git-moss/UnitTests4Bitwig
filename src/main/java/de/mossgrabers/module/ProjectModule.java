// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2019-2020
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.module;

import de.mossgrabers.TestFramework;

import com.bitwig.extension.controller.api.ControllerHost;
import com.bitwig.extension.controller.api.Project;


/**
 * Module tests for the Project class.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class ProjectModule extends TestModule
{
    /**
     * Constructor.
     */
    public ProjectModule ()
    {
        super ("Project");
    }


    /** {@inheritDoc} */
    @Override
    public boolean registerTests (final TestFramework tf, final ControllerHost host)
    {
        if (!super.registerTests (tf, host))
            return false;

        final Project project = host.getProject ();
        tf.assertNotNull ("Project not created.", project);

        tf.testParameter ("project.cueVolume", project.cueVolume (), Double.valueOf (0.7937005259840999), Double.valueOf (0), Double.valueOf (1.0), Double.valueOf (0.25), "0.0 dB", "Cue Level", null, null, null);
        tf.testParameter ("project.cueMix", project.cueMix (), Double.valueOf (1.0), Double.valueOf (0), Double.valueOf (1.0), Double.valueOf (0.25), "100 %", "Cue Mix", null, null, null);

        tf.testBooleanValue ("project.hasSoloedTracks", project.hasSoloedTracks ());
        tf.testBooleanValue ("project.hasMutedTracks", project.hasMutedTracks ());
        tf.testBooleanValue ("project.hasArmedTracks", project.hasArmedTracks ());

        return true;
    }
}
