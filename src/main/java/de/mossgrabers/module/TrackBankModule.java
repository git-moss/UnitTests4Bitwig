// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.module;

import de.mossgrabers.TestFramework;

import com.bitwig.extension.controller.api.ClipLauncherSlot;
import com.bitwig.extension.controller.api.ClipLauncherSlotBank;
import com.bitwig.extension.controller.api.ControllerHost;
import com.bitwig.extension.controller.api.Mixer;
import com.bitwig.extension.controller.api.SendBank;
import com.bitwig.extension.controller.api.Track;
import com.bitwig.extension.controller.api.TrackBank;

import java.util.Collections;


/**
 * Module tests for the TrackBank and Clip class.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class TrackBankModule extends TestModule
{
    private static final int NUM_TRACKS = 2;
    private static final int NUM_SENDS  = 2;
    private static final int NUM_SCENES = 2;


    /**
     * Constructor.
     */
    public TrackBankModule ()
    {
        super ("Track Bank");
    }


    /** {@inheritDoc} */
    @Override
    public void registerTests (final TestFramework tf, final ControllerHost host)
    {
        super.registerTests (tf, host);

        final Mixer mixer = host.createMixer ();
        tf.assertNotNull ("Mixer not created.", mixer);

        TrackBank trackBank = host.createMainTrackBank (NUM_TRACKS, NUM_SENDS, NUM_SCENES);
        tf.assertNotNull ("Track bank not created.", trackBank);

        for (int i = 0; i < NUM_TRACKS; i++)
        {
            final String trackName = "track" + i;

            final Track track = trackBank.getChannel (i);
            tf.assertNotNull (trackName + " not created.", track);

            // Channel interface
            tf.testBooleanValue (trackName + ".exists", track.exists (), Boolean.TRUE);
            tf.testStringValue (trackName + ".name", track.name (), i == 0 ? "Polysynth" : "Audio 2");
            tf.testBooleanValue (trackName + ".isActivated", track.isActivated (), Boolean.TRUE);
            tf.testBooleanValue (trackName + ".mute", track.mute (), Boolean.FALSE);
            tf.testBooleanValue (trackName + ".solo", track.solo (), Boolean.FALSE);
            tf.testColorValue (trackName + ".color", track.color (), Double.valueOf (i == 0 ? 1.0 : 0.8509804010391235), Double.valueOf (i == 0 ? 0.34117648005485535 : 0.21960784494876862), Double.valueOf (i == 0 ? 0.0235294122248888 : 0.4431372582912445));
            tf.testParameter (trackName + ".volume", track.volume (), Double.valueOf (i == 0 ? 0.7937005259840999 : 0.5407418735600996), Double.valueOf (0), Double.valueOf (1.0), Double.valueOf (0.25), i == 0 ? "+0.000 dB" : "-10.000 dB", "Volume", null, null, null);
            tf.testParameter (trackName + ".pan", track.pan (), Double.valueOf (0.5), Double.valueOf (0), Double.valueOf (1.0), Double.valueOf (0.25), "0.000 %", "Pan", null, null, null);

            final SendBank sendBank = track.sendBank ();
            tf.testParameter ("sendBank.getItemAt", sendBank.getItemAt (0), Double.valueOf (0), Double.valueOf (0), Double.valueOf (1.0), Double.valueOf (0.25), "-Inf dB", "Delay-2", null, null, null);

            // Track interface
            tf.testStringValue (trackName + ".trackType", track.trackType (), i == 0 ? "Instrument" : "Audio");
            tf.testIntegerValue (trackName + ".position", track.position (), Integer.valueOf (i));
            tf.testBooleanValue (trackName + ".isGroup", track.isGroup (), Boolean.FALSE);
            tf.testBooleanValue (trackName + ".arm", track.arm (), Boolean.valueOf (i == 0));
            tf.testBooleanValue (trackName + ".monitor", track.monitor (), Boolean.valueOf (i == 0));
            tf.testBooleanValue (trackName + ".autoMonitor", track.autoMonitor (), Boolean.TRUE);
            tf.testEnumValue (trackName + ".crossFadeMode", track.crossFadeMode (), Collections.singleton (i == 0 ? "AB" : "B"), "AB", "A", "B");
            tf.testBooleanValue (trackName + ".canHoldNoteData", track.canHoldNoteData (), Boolean.valueOf (i == 0));
            tf.testBooleanValue (trackName + ".canHoldAudioData", track.canHoldAudioData (), Boolean.valueOf (i != 0));

            final ClipLauncherSlotBank cs = track.clipLauncherSlotBank ();
            tf.assertNotNull ("ClipLauncherSlotBank " + i + " not created.", cs);

            for (int s = 0; s < NUM_SCENES; s++)
            {
                final ClipLauncherSlot slot = cs.getItemAt (i);

                final String slotName = trackName + ".slot" + s;
                tf.testBooleanValue (slotName + ".exists", slot.exists (), Boolean.TRUE);
                tf.testStringValue (slotName + ".name", slot.name (), i == 0 ? (s == 0 ? "Drummerboy" : "") : (s == 0 ? "" : "Yeah!"));
                tf.testBooleanValue (slotName + ".hasContent", slot.hasContent (), Boolean.valueOf (i == s));
                tf.testIntegerValue (slotName + ".sceneIndex", slot.sceneIndex (), Integer.valueOf (s));
                tf.testColorValue (slotName + ".color", slot.color (), Double.valueOf (i == 0 ? 0.0 : 0.8509804010391235), Double.valueOf (i == 0 ? 0.6000000238418579 : 0.21960784494876862), Double.valueOf (i == 0 ? 0.8509804010391235 : 0.4431372582912445));

                // States
                tf.testBooleanValue (slotName + ".isPlaying", slot.isPlaying (), Boolean.FALSE);
                tf.testBooleanValue (slotName + ".isPlaybackQueued", slot.isPlaybackQueued (), Boolean.FALSE);
                tf.testBooleanValue (slotName + ".isRecording", slot.isRecording (), Boolean.FALSE);
                tf.testBooleanValue (slotName + ".isRecordingQueued", slot.isRecordingQueued (), Boolean.FALSE);
                tf.testBooleanValue (slotName + ".isSelected", slot.isSelected (), Boolean.valueOf (i == 0));
                tf.testBooleanValue (slotName + ".isStopQueued", slot.isStopQueued (), Boolean.FALSE);
            }
        }
    }
}
