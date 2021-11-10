// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2019-2020
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers;

import com.bitwig.extension.api.PlatformType;
import com.bitwig.extension.controller.AutoDetectionMidiPortNamesList;
import com.bitwig.extension.controller.ControllerExtension;
import com.bitwig.extension.controller.ControllerExtensionDefinition;
import com.bitwig.extension.controller.api.ControllerHost;

import java.util.UUID;


/**
 * Definition class for the Unit Tests extension.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class UnitTestsExtensionDefinition extends ControllerExtensionDefinition
{
    private static final UUID EXTENSION_ID = UUID.fromString ("423793DD-89BA-49DC-9E6E-5C61FDA19E85");


    /** {@inheritDoc} */
    @Override
    public String getName ()
    {
        return "Unit Tests";
    }


    /** {@inheritDoc} */
    @Override
    public String getVersion ()
    {
        return "15";
    }


    /** {@inheritDoc} */
    @Override
    public String getAuthor ()
    {
        return "Jürgen Moßgraber";
    }


    /** {@inheritDoc} */
    @Override
    public String getHardwareVendor ()
    {
        return "Moss";
    }


    /** {@inheritDoc} */
    @Override
    public String getHardwareModel ()
    {
        return "Unit Tests";
    }


    /** {@inheritDoc} */
    @Override
    public UUID getId ()
    {
        return EXTENSION_ID;
    }


    /** {@inheritDoc} */
    @Override
    public int getRequiredAPIVersion ()
    {
        return 15;
    }


    /** {@inheritDoc} */
    @Override
    public int getNumMidiInPorts ()
    {
        // An input is needed to test note repeat
        return 1;
    }


    /** {@inheritDoc} */
    @Override
    public int getNumMidiOutPorts ()
    {
        return 0;
    }


    /** {@inheritDoc} */
    @Override
    public void listAutoDetectionMidiPortNames (final AutoDetectionMidiPortNamesList list, final PlatformType platformType)
    {
        // Intentionally empty
    }


    /** {@inheritDoc} */
    @Override
    public ControllerExtension createInstance (final ControllerHost host)
    {
        return new UnitTestsExtension (this, host);
    }
}
