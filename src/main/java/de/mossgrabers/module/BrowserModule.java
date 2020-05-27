// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2019-2020
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.module;

import de.mossgrabers.TestFramework;

import com.bitwig.extension.controller.api.BrowserFilterColumn;
import com.bitwig.extension.controller.api.ControllerHost;
import com.bitwig.extension.controller.api.CursorDevice;
import com.bitwig.extension.controller.api.CursorTrack;
import com.bitwig.extension.controller.api.PopupBrowser;


/**
 * Module tests for the Browser class.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class BrowserModule extends TestModule
{
    private static final int       NUM_SENDS       = 2;

    private static final String [] BROWSER_COLUMNS =
    {
        "Devices",
        "Presets",
        "Multisamples",
        "Samples",
        "Music"
    };


    /**
     * Constructor.
     */
    public BrowserModule ()
    {
        super ("Browser");
    }


    /** {@inheritDoc} */
    @Override
    public boolean registerTests (final TestFramework tf, final ControllerHost host)
    {
        if (!super.registerTests (tf, host))
            return false;

        final CursorTrack cursorTrack = host.createCursorTrack (NUM_SENDS, 0);
        final CursorDevice cursorDevice = cursorTrack.createCursorDevice ();
        tf.assertNotNull ("Cursor Device not created.", cursorDevice);

        final PopupBrowser browser = host.createPopupBrowser ();
        tf.assertNotNull ("Browser not created.", browser);
        browser.exists ().markInterested ();

        final BrowserFilterColumn filterColumn = browser.deviceTypeColumn ();
        tf.assertNotNull ("Filter Column not created.", filterColumn);

        host.scheduleTask ( () -> delayedBrowserOpen (tf, cursorDevice), ANSWER_DELAY);

        tf.testBooleanValue ("browser.exists", browser.exists (), Boolean.TRUE);
        tf.testStringValue ("browser.selectedContentTypeName", browser.selectedContentTypeName (), "Presets");
        tf.testStringArrayValue ("browser.contentTypeNames", browser.contentTypeNames (), BROWSER_COLUMNS);

        tf.testStringValue ("browser.title", browser.title (), "Select content to hotswap with device");
        tf.testBooleanValue ("browser.canAudition", browser.canAudition ());
        tf.testSettableBooleanValue ("browser.shouldAudition", browser.shouldAudition ());

        tf.testBooleanValue ("filterColumn.exists", filterColumn.exists (), Boolean.TRUE);
        tf.testStringValue ("filterColumn.name", filterColumn.name (), "Device Type");
        tf.testStringValue ("filterColumn.getWildcardItem.name", filterColumn.getWildcardItem ().name (), "Any Device Type");

        tf.testIntegerValue ("browser.selectedContentTypeIndex", browser.selectedContentTypeIndex (), Integer.valueOf (1), Integer.valueOf (0), Integer.valueOf (4), Integer.valueOf (2), Integer.valueOf (0));

        host.scheduleTask ( () -> delayedBrowserClose (tf, browser), ANSWER_DELAY);

        return true;
    }


    private static void delayedBrowserOpen (final TestFramework tf, final CursorDevice cursorDevice)
    {
        tf.scheduleFunction (new BrowserStarter (cursorDevice));
    }


    private static void delayedBrowserClose (final TestFramework tf, final PopupBrowser browser)
    {
        tf.scheduleFunction (browser::cancel);
    }


    /**
     * Start a browser.
     */
    public static class BrowserStarter implements Runnable
    {
        private CursorDevice cursorDevice;


        /**
         * Constructor.
         *
         * @param cursorDevice The cursor device
         */
        public BrowserStarter (final CursorDevice cursorDevice)
        {
            this.cursorDevice = cursorDevice;
        }


        /** {@inheritDoc} */
        @Override
        public void run ()
        {
            this.cursorDevice.replaceDeviceInsertionPoint ().browse ();
        }
    }
}
