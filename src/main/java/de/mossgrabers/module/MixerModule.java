// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.module;

import de.mossgrabers.TestFramework;

import com.bitwig.extension.controller.api.ControllerHost;
import com.bitwig.extension.controller.api.Mixer;


/**
 * Module tests for the Mixer class.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class MixerModule extends TestModule
{
    /**
     * Constructor.
     */
    public MixerModule ()
    {
        super ("Mixer");
    }


    /** {@inheritDoc} */
    @Override
    public void registerTests (final TestFramework tf, final ControllerHost host)
    {
        super.registerTests (tf, host);

        final Mixer mixer = host.createMixer ();
        tf.assertNotNull ("Mixer not created.", mixer);

        tf.testSettableBooleanValue ("mixer.isClipLauncherSectionVisible", mixer.isClipLauncherSectionVisible ());
        tf.testSettableBooleanValue ("mixer.isCrossFadeSectionVisible", mixer.isCrossFadeSectionVisible ());
        tf.testSettableBooleanValue ("mixer.isDeviceSectionVisible", mixer.isDeviceSectionVisible ());
        tf.testSettableBooleanValue ("mixer.isIoSectionVisible", mixer.isIoSectionVisible ());
        tf.testSettableBooleanValue ("mixer.isMeterSectionVisible", mixer.isMeterSectionVisible ());
        tf.testSettableBooleanValue ("mixer.isSendSectionVisible", mixer.isSendSectionVisible ());
    }
}
