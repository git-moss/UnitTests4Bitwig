// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017
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
        this.host.println (SEPARATOR_LINE);
        this.host.println ("Finished.");
    }


    /**
     * Prints a separater line.
     */
    public void infoLine ()
    {
        this.host.println (SEPARATOR_LINE);
    }


    /**
     * Prints a header message.
     *
     * @param message The message
     */
    public void header (final String message)
    {
        this.host.println ("");
        this.infoLine ();
        this.host.println (message);
    }


    /**
     * Prints an info message.
     *
     * @param message The message
     * @param padDepth The padding, 1 or 2
     */
    public void info (final String message, final int padDepth)
    {
        if (padDepth < 2 || BooleanSetting.isTrue (this.loggingSetting))
            this.host.println (buildMsg (message, padDepth));
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