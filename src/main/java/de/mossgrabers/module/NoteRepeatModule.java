// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2019-2020
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.module;

import de.mossgrabers.TestFramework;

import com.bitwig.extension.controller.api.Arpeggiator;
import com.bitwig.extension.controller.api.ControllerHost;
import com.bitwig.extension.controller.api.MidiIn;
import com.bitwig.extension.controller.api.NoteInput;


/**
 * Module tests for the NoteRepeat class.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class NoteRepeatModule extends TestModule
{
    /**
     * Constructor.
     */
    public NoteRepeatModule ()
    {
        super ("Note Repeat");
    }


    /** {@inheritDoc} */
    @Override
    public boolean registerTests (final TestFramework tf, final ControllerHost host)
    {
        if (!super.registerTests (tf, host))
            return false;

        final MidiIn midiInPort = host.getMidiInPort (0);
        tf.assertNotNull ("MidiIn not created.", midiInPort);

        final NoteInput noteInput = midiInPort.createNoteInput ("UnitTests");
        tf.assertNotNull ("NoteInput not created.", noteInput);

        // API 10

        final Arpeggiator arpeggiator = noteInput.arpeggiator ();
        tf.assertNotNull ("Arpeggiator not created.", arpeggiator);

        tf.testSettableBooleanValue ("arpeggiator.isEnabled", arpeggiator.isEnabled ());
        tf.testSettableBooleanValue ("arpeggiator.isFreeRunning", arpeggiator.isFreeRunning ());
        tf.testSettableBooleanValue ("arpeggiator.shuffle", arpeggiator.shuffle ());
        tf.testSettableBooleanValue ("arpeggiator.usePressureToVelocity", arpeggiator.usePressureToVelocity ());

        tf.testIntegerValue ("arpeggiator.octaves", arpeggiator.octaves (), Integer.valueOf (1), Integer.valueOf (0), Integer.valueOf (8), Integer.valueOf (4));

        tf.testDoubleValue ("arpeggiator.gateLength", arpeggiator.gateLength (), Double.valueOf (0.5), Double.valueOf (0.03125), Double.valueOf (1), Double.valueOf (0.5));
        tf.testDoubleValue ("arpeggiator.period", arpeggiator.rate (), Double.valueOf (0.25), Double.valueOf (0.0078125), Double.valueOf (128), Double.valueOf (8));

        tf.testEnumValue ("arpeggiator.mode", arpeggiator.mode (), new String []
        {
            "up"
        }, "up", "pinky-down", "random");

        // API 11

        tf.testSettableBooleanValue ("arpeggiator.enableOverlappingNotes", arpeggiator.enableOverlappingNotes ());
        tf.testDoubleValue ("arpeggiator.humanize", arpeggiator.humanize (), Double.valueOf (0), Double.valueOf (0.03125), Double.valueOf (1), Double.valueOf (0.5));
        tf.testSettableBooleanValue ("arpeggiator.terminateNotesImmediately", arpeggiator.terminateNotesImmediately ());

        return true;
    }
}
