// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.module;

import de.mossgrabers.TestFramework;

import com.bitwig.extension.controller.api.Application;
import com.bitwig.extension.controller.api.ControllerHost;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;


/**
 * Module tests for the Application class.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class ApplicationModule extends TestModule
{
    private static final Set<String> PANEL_LAYOUTS            = new HashSet<> ();
    private static final Set<String> DISPLAY_LAYOUTS          = new HashSet<> ();
    private static final String []   RECORD_QUANTIZATION_GRID =
    {
        "OFF",
        "1/32",
        "1/16",
        "1/8",
        "1/4"
    };

    static
    {
        Collections.addAll (PANEL_LAYOUTS, "ARRANGE", "MIX", "EDIT", "PLAY");
        Collections.addAll (DISPLAY_LAYOUTS, "Single Display (Small)", "Single Display (Large)", "Dual Display (Studio)", "Dual Display (Arranger/Mixer)", "Dual Display (Master/Detail)", "Triple Display", "Tablet");
    }


    /**
     * Constructor.
     */
    public ApplicationModule ()
    {
        super ("Application");
    }


    /** {@inheritDoc} */
    @Override
    public void registerTests (final TestFramework tf, final ControllerHost host)
    {
        super.registerTests (tf, host);

        final Application application = host.createApplication ();
        tf.assertNotNull ("Application not created.", application);

        tf.testBooleanValue ("application.hasActiveEngine", application.hasActiveEngine ());
        tf.testStringValue ("application.projectName", application.projectName (), "UnitTestsProject");
        tf.testStringValue ("application.panelLayout", application.panelLayout (), PANEL_LAYOUTS);
        tf.testStringValue ("application.displayProfile", application.displayProfile (), DISPLAY_LAYOUTS);

        // API 9
        tf.testEnumValue ("application.recordQuantizationGrid", application.recordQuantizationGrid (), RECORD_QUANTIZATION_GRID, RECORD_QUANTIZATION_GRID[0], RECORD_QUANTIZATION_GRID[4], RECORD_QUANTIZATION_GRID[2]);
        tf.testBooleanValue ("application.recordQuantizeNoteLength", application.recordQuantizeNoteLength ());
    }
}
