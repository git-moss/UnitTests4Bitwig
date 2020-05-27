// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2019-2020
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers;

import com.bitwig.extension.controller.api.ControllerHost;
import com.bitwig.extension.controller.api.SettableEnumValue;


/**
 * Logs messages to the Bitwigs script console.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class ConsoleLogger
{
    private static final String SEPARATOR_LINE = "----------------------------------------------------------------------";
    private static final String PAD1           = "  - ";
    private static final String PAD2           = "      * ";

    private ControllerHost      host;
    private SettableEnumValue   loggingSetting;


    /**
     * Constructor.
     *
     * @param host The controller host
     */
    public ConsoleLogger (final ControllerHost host)
    {
        this.host = host;
    }


    /**
     * Prints the finished message.
     */
    public void finish ()
    {
        this.header ("Done");
    }


    /**
     * Prints a separator line.
     * 
     * @param header The header text
     */
    public void header (final String header)
    {
        this.host.println (header + " " + SEPARATOR_LINE.substring (0, SEPARATOR_LINE.length () - header.length () - 2));
    }


    /**
     * Prints an error message.
     *
     * @param message The message
     * @param padDepth The padding, 1 or 2
     */
    public void error (final String message, final int padDepth)
    {
        this.host.errorln (buildMsg (message, padDepth));
    }


    /**
     * Prints an info message.
     *
     * @param message The message
     * @param padDepth The padding
     */
    public void info (final String message, final int padDepth)
    {
        this.host.println (buildMsg (message, padDepth));
    }


    /**
     * Prints an fine info message.
     *
     * @param message The message
     * @param padDepth The padding
     */
    public void fine (final String message, final int padDepth)
    {
        if (BooleanSetting.isTrue (this.loggingSetting))
            this.host.println (buildMsg (message, padDepth));
    }


    /**
     * Sets the enum value, which toggles between fine and broad grain logging.
     *
     * @param loggingSetting The setting
     */
    public void setLogLevel (final SettableEnumValue loggingSetting)
    {
        this.loggingSetting = loggingSetting;
    }


    private static String buildMsg (final String message, final int padDepth)
    {
        final StringBuilder sb = new StringBuilder ();
        if (padDepth == 1)
            sb.append (PAD1);
        else
            sb.append (PAD2);
        return sb.append (message).toString ();
    }
}