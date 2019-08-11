// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.module;

import de.mossgrabers.TestFramework;

import com.bitwig.extension.controller.api.ControllerHost;
import com.bitwig.extension.controller.api.MidiIn;
import com.bitwig.extension.controller.api.NoteInput;
import com.bitwig.extension.controller.api.NoteRepeat;


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
    public void registerTests (final TestFramework tf, final ControllerHost host)
    {
        super.registerTests (tf, host);

        final MidiIn midiInPort = host.getMidiInPort (0);
        tf.assertNotNull ("MidiIn not created.", midiInPort);

        final NoteInput noteInput = midiInPort.createNoteInput ("UnitTests");
        tf.assertNotNull ("NoteInput not created.", noteInput);

        final NoteRepeat noteRepeat = noteInput.createNoteRepeat ();
        tf.assertNotNull ("NoteRepeat not created.", noteRepeat);

        tf.testSettableBooleanValue ("noteRepeat.isEnabled", noteRepeat.isEnabled ());
        tf.testSettableBooleanValue ("noteRepeat.shuffle", noteRepeat.shuffle ());
        tf.testSettableBooleanValue ("noteRepeat.usePressureToVelocity", noteRepeat.usePressureToVelocity ());

        tf.testDoubleValue ("noteRepeat.noteLengthRatio", noteRepeat.noteLengthRatio (), Double.valueOf (0.5), Double.valueOf (0.03125), Double.valueOf (1), Double.valueOf (0.5));
        tf.testDoubleValue ("noteRepeat.period", noteRepeat.period (), Double.valueOf (0.25), Double.valueOf (0.0078125), Double.valueOf (128), Double.valueOf (8));
        tf.testDoubleValue ("noteRepeat.velocityRamp", noteRepeat.velocityRamp (), Double.valueOf (0), Double.valueOf (-1), Double.valueOf (1), Double.valueOf (0.5));
    }
}
