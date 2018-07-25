// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.module;

import de.mossgrabers.TestFramework;

import com.bitwig.extension.controller.api.ControllerHost;
import com.bitwig.extension.controller.api.CursorRemoteControlsPage;
import com.bitwig.extension.controller.api.DeviceBank;
import com.bitwig.extension.controller.api.PinnableCursorDevice;


/**
 * Module tests for the Cursor Device class.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class CursorDeviceModule extends TestModule
{
    private static final int NUM_SENDS           = 2;
    private static final int NUM_PARAMS          = 2;
    private static final int NUM_DEVICES_IN_BANK = 2;


    /**
     * Constructor.
     */
    public CursorDeviceModule ()
    {
        super ("Cursor Device");
    }


    /** {@inheritDoc} */
    @Override
    public void registerTests (final TestFramework tf, final ControllerHost host)
    {
        super.registerTests (tf, host);

        final PinnableCursorDevice cursorDevice = host.createCursorTrack (NUM_SENDS, 0).createCursorDevice ();
        tf.assertNotNull ("Cursor Device not created.", cursorDevice);

        tf.testSettableBooleanValue ("cursorDevice.isEnabled", cursorDevice.isEnabled ());
        tf.testBooleanValue ("cursorDevice.isPlugin", cursorDevice.isPlugin (), Boolean.FALSE);
        tf.testIntegerValue ("cursorDevice.position", cursorDevice.position (), Integer.valueOf (0));
        tf.testStringValue ("cursorDevice.name", cursorDevice.name (), "Polysynth");
        tf.testBooleanValue ("cursorDevice.hasPrevious", cursorDevice.hasPrevious ());
        tf.testBooleanValue ("cursorDevice.hasNext", cursorDevice.hasNext ());
        tf.testSettableBooleanValue ("cursorDevice.isExpanded", cursorDevice.isExpanded ());
        tf.testSettableBooleanValue ("cursorDevice.isRemoteControlsSectionVisible", cursorDevice.isRemoteControlsSectionVisible ());
        tf.testSettableBooleanValue ("cursorDevice.isWindowOpen", cursorDevice.isWindowOpen (), Boolean.FALSE, Boolean.FALSE, Boolean.FALSE);
        tf.testBooleanValue ("cursorDevice.isNested", cursorDevice.isNested (), Boolean.FALSE);
        tf.testBooleanValue ("cursorDevice.hasDrumPads", cursorDevice.hasDrumPads (), Boolean.FALSE);
        tf.testBooleanValue ("cursorDevice.hasLayers", cursorDevice.hasLayers (), Boolean.FALSE);
        tf.testBooleanValue ("cursorDevice.hasSlots", cursorDevice.hasSlots (), Boolean.TRUE);
        tf.testBooleanValue ("cursorDevice.isPinned", cursorDevice.isPinned (), Boolean.FALSE);

        final CursorRemoteControlsPage remoteControls = cursorDevice.createCursorRemoteControlsPage (NUM_PARAMS);
        tf.assertNotNull ("Remote controls not created.", remoteControls);

        tf.testBooleanValue ("remoteControls.hasPrevious", remoteControls.hasPrevious ());
        tf.testBooleanValue ("remoteControls.hasNext", remoteControls.hasNext ());
        tf.testIntegerValue ("remoteControls.selectedPageIndex", remoteControls.selectedPageIndex (), Integer.valueOf (0), Integer.valueOf (0), Integer.valueOf (8), Integer.valueOf (5));
        tf.testStringArrayValue ("remoteControls.pageNames", remoteControls.pageNames (), new String []
        {
            "OSC"
        });
        tf.testParameter ("remoteControls.getParameter (0)", remoteControls.getParameter (0), Double.valueOf (0.5), Double.valueOf (0.0), Double.valueOf (1.0), Double.valueOf (0.6), "+0.000 st", "Pitch", "Parameter Name 1", "Parameter Name 2", "Parameter Name 3");
        tf.testParameter ("remoteControls.getParameter (1)", remoteControls.getParameter (1), Double.valueOf (0.5), Double.valueOf (0.0), Double.valueOf (1.0), Double.valueOf (0.6), "0.000 %", "Shape", "Parameter Name 1", "Parameter Name 2", "Parameter Name 3");

        final DeviceBank siblings = cursorDevice.createSiblingsDeviceBank (NUM_DEVICES_IN_BANK);
        tf.assertNotNull ("Siblings device bank not created.", siblings);

        tf.testStringValue ("siblings.getDevice (0).name", siblings.getDevice (0).name (), "Polysynth");
        tf.testStringValue ("siblings.getDevice (1).name", siblings.getDevice (1).name (), "");
    }
}
