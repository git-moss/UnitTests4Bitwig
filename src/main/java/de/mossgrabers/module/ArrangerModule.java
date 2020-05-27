// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2019-2020
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.module;

import de.mossgrabers.TestFramework;

import com.bitwig.extension.controller.api.Arranger;
import com.bitwig.extension.controller.api.ControllerHost;
import com.bitwig.extension.controller.api.CueMarker;
import com.bitwig.extension.controller.api.CueMarkerBank;

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
    public boolean registerTests (final TestFramework tf, final ControllerHost host)
    {
        if (!super.registerTests (tf, host))
            return false;

        final Arranger arranger = host.createArranger ();
        tf.assertNotNull ("Arranger not created.", arranger);

        tf.testSettableBooleanValue ("arranger.areCueMarkersVisible", arranger.areCueMarkersVisible ());
        tf.testSettableBooleanValue ("arranger.isPlaybackFollowEnabled", arranger.isPlaybackFollowEnabled ());
        tf.testSettableBooleanValue ("arranger.hasDoubleRowTrackHeight", arranger.hasDoubleRowTrackHeight ());
        tf.testSettableBooleanValue ("arranger.isClipLauncherVisible", arranger.isClipLauncherVisible ());
        tf.testSettableBooleanValue ("arranger.isTimelineVisible", arranger.isTimelineVisible ());
        tf.testSettableBooleanValue ("arranger.isIoSectionVisible", arranger.isIoSectionVisible ());
        tf.testSettableBooleanValue ("arranger.areEffectTracksVisible", arranger.areEffectTracksVisible ());

        // Test markers
        final CueMarkerBank markerBank = arranger.createCueMarkerBank (2);
        tf.assertNotNull ("Marker Bank not created.", markerBank);

        final CueMarker firstMarker = markerBank.getItemAt (0);
        final CueMarker secondMarker = markerBank.getItemAt (1);

        tf.testBooleanValue ("arranger.markers[0].exists", firstMarker.exists (), Boolean.TRUE);
        tf.testStringValue ("arranger.markers[0].name", firstMarker.getName (), "My Mark");
        tf.testColorValue ("arranger.markers[0].color", firstMarker.getColor (), Double.valueOf (0.8999999761581421), Double.valueOf (0.8999999761581421), Double.valueOf (0));

        tf.testBooleanValue ("arranger.markers[1].exists", secondMarker.exists (), Boolean.FALSE);
        tf.testStringValue ("arranger.markers[1].name", secondMarker.getName (), "");
        tf.testColorValue ("arranger.markers[1].color", secondMarker.getColor (), Double.valueOf (0), Double.valueOf (0), Double.valueOf (0));

        return true;
    }
}
