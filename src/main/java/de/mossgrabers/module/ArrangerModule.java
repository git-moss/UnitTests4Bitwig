// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.module;

import de.mossgrabers.TestFramework;

import com.bitwig.extension.controller.api.Arranger;
import com.bitwig.extension.controller.api.ControllerHost;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;


/**
 * Module tests for the Arranger class.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class ArrangerModule extends TestModule
{
    private static final Set<String> PANEL_LAYOUTS   = new HashSet<> ();
    private static final Set<String> DISPLAY_LAYOUTS = new HashSet<> ();

    static
    {
        Collections.addAll (PANEL_LAYOUTS, "ARRANGE", "MIX", "EDIT", "PLAY");
        Collections.addAll (DISPLAY_LAYOUTS, "Single Display (Small)", "Single Display (Large)", "Dual Display (Studio)", "Dual Display (Arranger/Mixer)", "Dual Display (Master/Detail)", "Triple Display", "Tablet");
    }


    /**
     * Constructor.
     */
    public ArrangerModule ()
    {
        super ("Arranger");
    }


    /** {@inheritDoc} */
    @Override
    public void registerTests (final TestFramework tf, final ControllerHost host)
    {
        super.registerTests (tf, host);

        final Arranger arranger = host.createArranger ();
        tf.assertNotNull ("Arranger not created.", arranger);

        tf.testSettableBooleanValue ("arranger.areCueMarkersVisible", arranger.areCueMarkersVisible ());
        tf.testSettableBooleanValue ("arranger.isPlaybackFollowEnabled", arranger.isPlaybackFollowEnabled ());
        tf.testSettableBooleanValue ("arranger.hasDoubleRowTrackHeight", arranger.hasDoubleRowTrackHeight ());
        tf.testSettableBooleanValue ("arranger.isClipLauncherVisible", arranger.isClipLauncherVisible ());
        tf.testSettableBooleanValue ("arranger.isTimelineVisible", arranger.isTimelineVisible ());
        tf.testSettableBooleanValue ("arranger.isIoSectionVisible", arranger.isIoSectionVisible ());
        tf.testSettableBooleanValue ("arranger.areEffectTracksVisible", arranger.areEffectTracksVisible ());
    }
}
