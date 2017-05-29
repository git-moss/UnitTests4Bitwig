// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.module;

import de.mossgrabers.TestFramework;

import com.bitwig.extension.controller.api.ControllerHost;
import com.bitwig.extension.controller.api.TimeSignatureValue;
import com.bitwig.extension.controller.api.Transport;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;


/**
 * Module tests for the Transport class.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class TransportModule extends TestModule
{
    private static final Set<String> AUTOMATION_WRITE_MODE     = new HashSet<> ();
    private static final Set<String> LAUNCHER_POST_REC_ACTIONS = new HashSet<> ();
    private static final Set<String> PRE_ROLL                  = new HashSet<> ();

    static
    {
        Collections.addAll (AUTOMATION_WRITE_MODE, "latch", "touch", "write");
        Collections.addAll (LAUNCHER_POST_REC_ACTIONS, "off", "play_recorded", "record_next_free_slot", "stop", "return_to_arrangement", "return_to_previous_clip", "play_random");
        Collections.addAll (PRE_ROLL, "none", "one_bar", "two_bars", "four_bars");
    }


    /**
     * Constructor.
     */
    public TransportModule ()
    {
        super ("Transport");
    }


    /** {@inheritDoc} */
    @Override
    public void registerTests (final TestFramework tf, final ControllerHost host)
    {
        super.registerTests (tf, host);

        final Transport transport = host.createTransport ();
        tf.assertNotNull ("Transport not created.", transport);

        tf.testSettableBooleanValue ("transport.isPlaying", transport.isPlaying ());
        tf.testSettableBooleanValue ("transport.isArrangerRecordEnabled", transport.isArrangerRecordEnabled ());
        tf.testSettableBooleanValue ("transport.isArrangerOverdubEnabled", transport.isArrangerOverdubEnabled ());
        tf.testSettableBooleanValue ("transport.isClipLauncherOverdubEnabled", transport.isClipLauncherOverdubEnabled ());
        tf.testEnumValue ("transport.automationWriteMode", transport.automationWriteMode (), AUTOMATION_WRITE_MODE, "latch", "touch", "write");
        tf.testSettableBooleanValue ("transport.isArrangerAutomationWriteEnabled", transport.isArrangerAutomationWriteEnabled ());
        tf.testSettableBooleanValue ("transport.isClipLauncherAutomationWriteEnabled", transport.isClipLauncherAutomationWriteEnabled ());
        tf.testBooleanValue ("transport.isAutomationOverrideActive", transport.isAutomationOverrideActive ());
        tf.testSettableBooleanValue ("transport.isArrangerLoopEnabled", transport.isArrangerLoopEnabled ());
        tf.testSettableBooleanValue ("transport.isPunchInEnabled", transport.isPunchInEnabled ());
        tf.testSettableBooleanValue ("transport.isPunchOutEnabled", transport.isPunchOutEnabled ());
        tf.testSettableBooleanValue ("transport.isMetronomeEnabled", transport.isMetronomeEnabled ());
        tf.testSettableBooleanValue ("transport.isMetronomeTickPlaybackEnabled", transport.isMetronomeTickPlaybackEnabled ());

        tf.testSettableRangedValue ("transport.metronomeVolume", transport.metronomeVolume (), Double.valueOf (0.75), Double.valueOf (0.0), Double.valueOf (1.0), Double.valueOf (0.6), "-12.0 dB");
        tf.testSettableBooleanValue ("transport.isMetronomeAudibleDuringPreRoll", transport.isMetronomeAudibleDuringPreRoll ());
        tf.testEnumValue ("transport.preRoll", transport.preRoll (), PRE_ROLL, "none", "one_bar", "four_bars");

        tf.testParameter ("transport.tempo", transport.tempo (), Double.valueOf (0.1393188854489164), Double.valueOf (0.0), Double.valueOf (1.0), Double.valueOf (0.1393188854489164), "110.000 BPM", "Tempo");
        tf.testSettableBeatTimeValue ("transport.getPosition", transport.getPosition (), Double.valueOf (0.0), Double.valueOf (0.0), Double.valueOf (1.0), Double.valueOf (10.0), "001:01:01:00");
        tf.testSettableBeatTimeValue ("transport.getInPosition", transport.getInPosition (), Double.valueOf (0.0), Double.valueOf (0.0), Double.valueOf (1.0), Double.valueOf (10.0), "001:01:01:00");
        tf.testSettableBeatTimeValue ("transport.getOutPosition", transport.getOutPosition (), Double.valueOf (4.0), Double.valueOf (1.0), Double.valueOf (2.0), Double.valueOf (10.0), "002:01:01:00");
        tf.testParameter ("transport.getCrossfade", transport.getCrossfade (), Double.valueOf (0.5), Double.valueOf (0.0), Double.valueOf (1.0), Double.valueOf (0.75), "0.000 %", "Crossfade");

        final TimeSignatureValue timeSignature = transport.getTimeSignature ();
        tf.testTimeSignature ("transport.getTimeSignature", timeSignature, "4/4", "3/4", "5/8", "15/16");
        tf.testIntegerValue ("transport.timeSignature.getNumerator", timeSignature.getNumerator (), Integer.valueOf (4), Integer.valueOf (2), Integer.valueOf (3), Integer.valueOf (4));
        tf.testIntegerValue ("transport.timeSignature.getDenominator", timeSignature.getDenominator (), Integer.valueOf (4), Integer.valueOf (8), Integer.valueOf (16), Integer.valueOf (4));
        tf.testIntegerValue ("transport.timeSignature.getTicks", timeSignature.getTicks (), Integer.valueOf (16), Integer.valueOf (0), Integer.valueOf (32), Integer.valueOf (10));

        tf.testEnumValue ("transport.clipLauncherPostRecordingAction", transport.clipLauncherPostRecordingAction (), LAUNCHER_POST_REC_ACTIONS, "off", "play_recorded", "play_random");
        tf.testSettableBeatTimeValue ("transport.getClipLauncherPostRecordingTimeOffset", transport.getClipLauncherPostRecordingTimeOffset (), Double.valueOf (4.0), Double.valueOf (0.0), Double.valueOf (1.0), Double.valueOf (10.0), "001:00:00:00");
    }
}