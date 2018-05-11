// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.module;

import de.mossgrabers.TestFramework;

import com.bitwig.extension.controller.api.Clip;
import com.bitwig.extension.controller.api.ClipLauncherSlotBank;
import com.bitwig.extension.controller.api.ControllerHost;
import com.bitwig.extension.controller.api.TrackBank;


/**
 * Test module for the cursor clip.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class CursorClipModule extends TestModule
{
    private static final int NUM_TRACKS = 2;


    /**
     * Constructor.
     */
    public CursorClipModule ()
    {
        super ("Cursor Clip");
    }


    /** {@inheritDoc} */
    @Override
    public void registerTests (final TestFramework tf, final ControllerHost host)
    {
        super.registerTests (tf, host);

        final Clip clip = host.createLauncherCursorClip (8, 8);
        tf.assertNotNull ("Clip not created.", clip);

        // Make sure that the first slot is selected for testing
        final TrackBank trackBank = host.createTrackBank (NUM_TRACKS, 0, 3);
        tf.assertNotNull ("TrackBank not created.", trackBank);

        tf.scheduleFunction ( () -> {
            final ClipLauncherSlotBank slotBank = trackBank.getItemAt (0).clipLauncherSlotBank ();
            slotBank.select (0);
            slotBank.showInEditor (0);
            clip.scrollToKey (36);
        });

        tf.testBooleanValue ("clip.canScrollKeysUp", clip.canScrollKeysUp (), Boolean.TRUE);
        tf.testBooleanValue ("clip.canScrollKeysDown", clip.canScrollKeysDown (), Boolean.TRUE);
        tf.testBooleanValue ("clip.canScrollStepsBackwards", clip.canScrollStepsBackwards (), Boolean.FALSE);
        tf.testBooleanValue ("clip.canScrollStepsForwards", clip.canScrollStepsForwards (), Boolean.FALSE);
        tf.testIntegerValue ("clip.playingStep ()", clip.playingStep (), Integer.valueOf (-1));
        tf.testSettableBooleanValue ("clip.getShuffle", clip.getShuffle (), Boolean.FALSE, null, null);
        tf.testSettableRangedValue ("clip.getAccent", clip.getAccent (), Double.valueOf (0.5), Double.valueOf (0.0), Double.valueOf (1.0), Double.valueOf (0.6), "0.00 %");
        tf.testSettableBeatTimeValue ("clip.getPlayStart", clip.getPlayStart (), Double.valueOf (0.0), Double.valueOf (0.0), Double.valueOf (1.0), Double.valueOf (1.0), "001:01:01:00");
        tf.testSettableBeatTimeValue ("clip.getPlayStop", clip.getPlayStop (), Double.valueOf (10.0), Double.valueOf (4.0), Double.valueOf (4.0), Double.valueOf (4.0), "002:01:01:00");
        tf.testSettableBooleanValue ("clip.isLoopEnabled", clip.isLoopEnabled (), Boolean.TRUE, null, null);
        tf.testSettableBeatTimeValue ("clip.getLoopStart", clip.getLoopStart (), Double.valueOf (0.0), Double.valueOf (0.0), Double.valueOf (1.0), Double.valueOf (10.0), "001:01:01:00");
        tf.testSettableBeatTimeValue ("clip.getLoopLength", clip.getLoopLength (), Double.valueOf (4.0), Double.valueOf (1.0), Double.valueOf (4.0), Double.valueOf (10.0), "001:00:00:00");
        tf.testColorValue ("clip.color", clip.color (), Double.valueOf (0.0), Double.valueOf (0.6000000238418579), Double.valueOf (0.8509804010391235));
        tf.testStringValue ("clip.getTrack.name", clip.getTrack ().name (), "Polysynth");
    }
}
