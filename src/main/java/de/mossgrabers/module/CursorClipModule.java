// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2019-2020
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.module;

import de.mossgrabers.TestFramework;

import com.bitwig.extension.controller.api.Clip;
import com.bitwig.extension.controller.api.ClipLauncherSlotBank;
import com.bitwig.extension.controller.api.ControllerHost;
import com.bitwig.extension.controller.api.Track;
import com.bitwig.extension.controller.api.TrackBank;


/**
 * Test module for the cursor clip.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class CursorClipModule extends TestModule
{
    private static final int       NUM_TRACKS                 = 2;
    private static final String [] LAUNCH_QUANTIZATION_VALUES =
    {
        "default",
        "none",
        "8",
        "4",
        "2",
        "1",
        "1/2",
        "1/4",
        "1/8",
        "1/16"
    };

    private static final String [] LAUNCH_MODE_VALUES         =
    {
        "play_with_quantization",
        "continue_immediately",
        "continue_with_quantization"
    };


    /**
     * Constructor.
     */
    public CursorClipModule ()
    {
        super ("Cursor Clip");
    }


    /** {@inheritDoc} */
    @Override
    public boolean registerTests (final TestFramework tf, final ControllerHost host)
    {
        if (!super.registerTests (tf, host))
            return false;

        final Clip clip = host.createLauncherCursorClip (8, 8);
        tf.assertNotNull ("Clip not created.", clip);

        final TrackBank trackBank = host.createTrackBank (NUM_TRACKS, 0, 3);
        tf.assertNotNull ("TrackBank not created.", trackBank);

        // Make sure that the first slot is selected for testing
        tf.scheduleFunction ( () -> {
            final Track track = trackBank.getItemAt (0);
            track.selectInEditor ();
            track.selectInMixer ();
            final ClipLauncherSlotBank slotBank = track.clipLauncherSlotBank ();
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

        // API 8
        tf.testSettableEnumValue ("clip.launchQuantization", clip.launchQuantization (), LAUNCH_QUANTIZATION_VALUES, LAUNCH_QUANTIZATION_VALUES[0]);
        tf.testSettableBooleanValue ("clip.useLoopStartAsQuantizationReference", clip.useLoopStartAsQuantizationReference ());

        // API 9
        tf.testEnumValue ("clip.launchMode", clip.launchMode (), LAUNCH_MODE_VALUES, LAUNCH_MODE_VALUES[0], LAUNCH_MODE_VALUES[1], LAUNCH_MODE_VALUES[2]);

        return true;
    }
}
