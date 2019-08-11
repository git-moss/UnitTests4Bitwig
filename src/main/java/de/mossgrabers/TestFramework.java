// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers;

import de.mossgrabers.module.BrowserModule;

import com.bitwig.extension.controller.api.BooleanValue;
import com.bitwig.extension.controller.api.ColorValue;
import com.bitwig.extension.controller.api.ControllerHost;
import com.bitwig.extension.controller.api.DoubleValue;
import com.bitwig.extension.controller.api.EnumValue;
import com.bitwig.extension.controller.api.IntegerValue;
import com.bitwig.extension.controller.api.Parameter;
import com.bitwig.extension.controller.api.RangedValue;
import com.bitwig.extension.controller.api.SettableBeatTimeValue;
import com.bitwig.extension.controller.api.SettableBooleanValue;
import com.bitwig.extension.controller.api.SettableDoubleValue;
import com.bitwig.extension.controller.api.SettableEnumValue;
import com.bitwig.extension.controller.api.SettableIntegerValue;
import com.bitwig.extension.controller.api.SettableRangedValue;
import com.bitwig.extension.controller.api.SettableStringValue;
import com.bitwig.extension.controller.api.StringArrayValue;
import com.bitwig.extension.controller.api.StringValue;
import com.bitwig.extension.controller.api.TimeSignatureValue;
import com.bitwig.extension.controller.api.Value;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;


/**
 * Testframework for Bitwig Studio Value properties.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class TestFramework
{
    private static final String                         OBSERVING_PROPERTY = "Observing property ";
    private static final int                            ANSWER_DELAY       = 100;
    private static final Set<Class<? extends Value<?>>> SETTABLE_CLASSES   = new HashSet<> ();
    private static final Set<Boolean>                   BOOLEAN_OPTS       = new HashSet<> (2);

    static
    {
        Collections.addAll (BOOLEAN_OPTS, Boolean.FALSE, Boolean.TRUE);
        Collections.addAll (SETTABLE_CLASSES, SettableBooleanValue.class, SettableStringValue.class, SettableIntegerValue.class, SettableDoubleValue.class, SettableRangedValue.class);
    }

    ConsoleLogger                      logger;

    private ControllerHost             host;
    private final LinkedList<Runnable> scheduler = new LinkedList<> ();
    private SettableEnumValue          getterSetting;
    private SettableEnumValue          observerSetting;


    /**
     * Constructor.
     *
     * @param host The controller host
     * @param logger Status logging
     */
    public TestFramework (final ControllerHost host, final ConsoleLogger logger)
    {
        this.host = host;
        this.logger = logger;
    }


    /**
     * Set the settings for dis-/enabling testing getters and/or observers.
     *
     * @param getterSetting Test property getters when true
     * @param observerSetting Test property observers when true
     */
    public void setSettings (final SettableEnumValue getterSetting, final SettableEnumValue observerSetting)
    {
        this.getterSetting = getterSetting;
        this.observerSetting = observerSetting;
    }


    /**
     * Mark a new section for a module test.
     *
     * @param moduleName The name of the module
     * @param setting The setting for dis-/enabling the test module
     */
    public void beginModuleTest (final String moduleName, final SettableEnumValue setting)
    {
        this.host.scheduleTask ( () -> this.scheduleFunction (new ModuleSection (moduleName, setting)), ANSWER_DELAY);
    }


    /**
     * Test a time signature property value.
     *
     * @param propertyName The name of the propery
     * @param property The property
     * @param defaultValue The default value to test
     * @param minValue The minimum value to test
     * @param maxValue The maximum value to test
     * @param testValue Another value to test
     */
    public void testTimeSignature (final String propertyName, final TimeSignatureValue property, final String defaultValue, final String minValue, final String maxValue, final String testValue)
    {
        final PropertyTestValues<TimeSignatureValue, String> propertyObject = this.enableTimeSignatureValue (propertyName, property, defaultValue, minValue, maxValue, testValue);
        this.host.scheduleTask ( () -> this.delayedTestValue (propertyObject, "4/4"), ANSWER_DELAY);
    }


    /**
     * Test a parameter property value.
     *
     * @param propertyName The name of the propery
     * @param property The property
     * @param defaultValue The default value to test
     * @param minValue The minimum value to test
     * @param maxValue The maximum value to test
     * @param testValue Another value to test
     * @param defaultDisplayValue The displayed string value to test
     * @param defaultNameValue The displayed name to test
     */
    public void testParameter (final String propertyName, final Parameter property, final Double defaultValue, final Double minValue, final Double maxValue, final Double testValue, final String defaultDisplayValue, final String defaultNameValue)
    {
        this.testParameter (propertyName, property, defaultValue, minValue, maxValue, testValue, defaultDisplayValue, defaultNameValue, null, null, null);
    }


    /**
     * Test a beat time value.
     *
     * @param propertyName The name of the propery
     * @param property The property
     * @param defaultValue The default value to test
     * @param minValue The minimum value to test
     * @param maxValue The maximum value to test
     * @param testValue Another value to test
     * @param expectedFormattedBeatTime The formatted beat time to test
     */
    public void testSettableBeatTimeValue (final String propertyName, final SettableBeatTimeValue property, final Double defaultValue, final Double minValue, final Double maxValue, final Double testValue, final String expectedFormattedBeatTime)
    {
        final PropertyTestValues<SettableBeatTimeValue, Double> propertyObject = this.enableSettableBeatTimeValue (propertyName, property, defaultValue, minValue, maxValue, testValue);
        this.host.scheduleTask ( () -> this.delayedTestValue (propertyObject, Double.valueOf (0.0)), ANSWER_DELAY);
        this.host.scheduleTask ( () -> this.delayedTestFormattedBeatTime (propertyObject, expectedFormattedBeatTime), ANSWER_DELAY);
    }


    /**
     * Test a parameter property value.
     *
     * @param propertyName The name of the propery
     * @param property The property
     * @param defaultValue The default value to test
     * @param minValue The minimum value to test
     * @param maxValue The maximum value to test
     * @param testValue Another value to test
     * @param defaultDisplayValue The displayed string value to test
     * @param defaultNameValue The displayed name to test
     * @param minNameValue A value for the name to test
     * @param maxNameValue A second value for the name to test
     * @param testNameValue A third value for the name to test
     */
    public void testParameter (final String propertyName, final Parameter property, final Double defaultValue, final Double minValue, final Double maxValue, final Double testValue, final String defaultDisplayValue, final String defaultNameValue, final String minNameValue, final String maxNameValue, final String testNameValue)
    {
        this.testBooleanValue (propertyName + ".exists", property.exists (), Boolean.TRUE);
        this.testStringValue (propertyName + ".name", property.name (), defaultNameValue, minNameValue, maxNameValue, testNameValue);
        if (minValue != null)
            this.testSettableRangedValue (propertyName + ".value", property.value (), defaultValue, minValue, maxValue, testValue);
        this.testRangedValue (propertyName + ".modulatedValue", property.modulatedValue (), defaultValue);
        this.testStringValue (propertyName + ".displayedValue", property.displayedValue (), defaultDisplayValue);
    }


    /**
     * Test a boolean property value. Note that Value is actually also the SettableValue interface
     * and only different in the documentation!
     *
     * @param propertyName The name of the propery
     * @param property The property
     */
    public void testBooleanValue (final String propertyName, final BooleanValue property)
    {
        this.testBooleanValue (propertyName, property, null);
    }


    /**
     * Test a boolean property value. Note that Value is actually also the SettableValue interface
     * and only different in the documentation!
     *
     * @param propertyName The name of the propery
     * @param property The property
     * @param defaultValue The default value to test
     */
    public void testBooleanValue (final String propertyName, final BooleanValue property, final Boolean defaultValue)
    {
        final PropertyTestValues<BooleanValue, Boolean> propertyObject = this.enableBooleanValue (propertyName, property, defaultValue, null, null, null);
        this.host.scheduleTask ( () -> this.delayedTestBooleanValue (propertyObject), ANSWER_DELAY);
    }


    /**
     * Test a boolean property value.
     *
     * @param propertyName The name of the propery
     * @param property The property
     */
    public void testSettableBooleanValue (final String propertyName, final SettableBooleanValue property)
    {
        this.testSettableBooleanValue (propertyName, property, null, null, null);
    }


    /**
     * Test a boolean property value.
     *
     * @param propertyName The name of the propery
     * @param property The property
     * @param defaultValue The default value to test
     * @param minValue The minimum value to test
     * @param maxValue The maximum value to test
     */
    public void testSettableBooleanValue (final String propertyName, final SettableBooleanValue property, final Boolean defaultValue, final Boolean minValue, final Boolean maxValue)
    {
        final PropertyTestValues<BooleanValue, Boolean> propertyObject = this.enableBooleanValue (propertyName, property, defaultValue, minValue == null ? Boolean.FALSE : minValue, maxValue == null ? Boolean.TRUE : maxValue, null);
        this.host.scheduleTask ( () -> this.delayedTestBooleanValue (propertyObject), ANSWER_DELAY);
    }


    /**
     * Test a string property value.
     *
     * @param propertyName The name of the propery
     * @param property The property
     * @param defaultValue The default value to test
     */
    public void testStringValue (final String propertyName, final StringValue property, final String defaultValue)
    {
        this.testStringValue (propertyName, property, Collections.singleton (defaultValue));
    }


    /**
     * Test a string property value.
     *
     * @param propertyName The name of the propery
     * @param property The property
     * @param defaultValues The default values to test
     */
    public void testStringValue (final String propertyName, final StringValue property, final Set<String> defaultValues)
    {
        final PropertyTestValues<StringValue, String> propertyObject = this.enableStringValue (propertyName, property, defaultValues);
        this.host.scheduleTask ( () -> this.delayedTestValue (propertyObject, ""), ANSWER_DELAY);
    }


    /**
     * Test a string property value.
     *
     * @param propertyName The name of the propery
     * @param property The property
     * @param defaultValue The default value to test
     * @param minValue The minimum value to test
     * @param maxValue The maximum value to test
     * @param testValue Another value to test
     */
    public void testStringValue (final String propertyName, final StringValue property, final String defaultValue, final String minValue, final String maxValue, final String testValue)
    {
        final PropertyTestValues<StringValue, String> propertyObject = this.enableStringValue (propertyName, property, Collections.singleton (defaultValue), minValue, maxValue, testValue);
        this.host.scheduleTask ( () -> this.delayedTestValue (propertyObject, ""), ANSWER_DELAY);
    }


    /**
     * Test a string array property value.
     *
     * @param propertyName The name of the propery
     * @param property The property
     * @param defaultValue The default value to test
     */
    public void testStringArrayValue (final String propertyName, final StringArrayValue property, final String [] defaultValue)
    {
        final PropertyTestValues<StringArrayValue, String []> propertyObject = this.enableStringArrayValue (propertyName, property, defaultValue, null, null, null);
        this.host.scheduleTask ( () -> this.delayedTestValue (propertyObject, null), ANSWER_DELAY);
    }


    /**
     * Test a settable enum property value.
     *
     * @param propertyName The name of the propery
     * @param property The property
     * @param enumValues The possible values for the enum
     * @param defaultValue The default value to test
     */
    public void testSettableEnumValue (final String propertyName, final SettableEnumValue property, final String [] enumValues, final String defaultValue)
    {
        for (final String enumValue: enumValues)
        {
            final PropertyTestValues<SettableEnumValue, String> propertyObject = this.enableSettableEnumValue (propertyName, property, defaultValue, enumValue);
            this.host.scheduleTask ( () -> this.delayedTestValue (propertyObject, defaultValue), ANSWER_DELAY);
        }
    }


    /**
     * Test an integer property value.
     *
     * @param propertyName The name of the propery
     * @param property The property
     * @param defaultValue The default value to test
     */
    public void testIntegerValue (final String propertyName, final IntegerValue property, final Integer defaultValue)
    {
        final PropertyTestValues<IntegerValue, Integer> propertyObject = this.enableIntegerValue (propertyName, property, defaultValue, null, null, null);
        this.host.scheduleTask ( () -> this.delayedTestValue (propertyObject, Integer.valueOf (-1)), ANSWER_DELAY);
    }


    /**
     * Test an integer property value.
     *
     * @param propertyName The name of the propery
     * @param property The property
     * @param defaultValue The default value to test
     * @param minValue The minimum value to test
     * @param maxValue The maximum value to test
     * @param testValue Another value to test
     */
    public void testIntegerValue (final String propertyName, final IntegerValue property, final Integer defaultValue, final Integer minValue, final Integer maxValue, final Integer testValue)
    {
        this.testIntegerValue (propertyName, property, defaultValue, minValue, maxValue, testValue, Integer.valueOf (-1));
    }


    /**
     * Test an integer property value.
     *
     * @param propertyName The name of the propery
     * @param property The property
     * @param defaultValue The default value to test
     * @param minValue The minimum value to test
     * @param maxValue The maximum value to test
     * @param testValue Another value to test
     * @param offValue The value when not subscribed
     */
    public void testIntegerValue (final String propertyName, final IntegerValue property, final Integer defaultValue, final Integer minValue, final Integer maxValue, final Integer testValue, final Integer offValue)
    {
        final PropertyTestValues<IntegerValue, Integer> propertyObject = this.enableIntegerValue (propertyName, property, defaultValue, minValue, maxValue, testValue);
        this.host.scheduleTask ( () -> this.delayedTestValue (propertyObject, offValue), ANSWER_DELAY);
    }


    /**
     * Test a double property value.
     *
     * @param propertyName The name of the propery
     * @param property The property
     * @param defaultValue The default value to test
     * @param minValue The minimum value to test
     * @param maxValue The maximum value to test
     * @param testValue Another value to test
     */
    public void testDoubleValue (final String propertyName, final DoubleValue property, final Double defaultValue, final Double minValue, final Double maxValue, final Double testValue)
    {
        final PropertyTestValues<DoubleValue, Double> propertyObject = this.enableDoubleValue (propertyName, property, defaultValue, minValue, maxValue, testValue);
        this.host.scheduleTask ( () -> this.delayedTestValue (propertyObject, Double.valueOf (0.0)), ANSWER_DELAY);
    }


    /**
     * Test a ranged property value.
     *
     * @param propertyName The name of the propery
     * @param property The property
     * @param defaultValue The default value to test
     * @param minValue The minimum value to test
     * @param maxValue The maximum value to test
     * @param testValue Another value to test
     */
    public void testSettableRangedValue (final String propertyName, final SettableRangedValue property, final Double defaultValue, final Double minValue, final Double maxValue, final Double testValue)
    {
        this.testSettableRangedValue (propertyName, property, defaultValue, minValue, maxValue, testValue, null);
    }


    /**
     * Test a ranged property value.
     *
     * @param propertyName The name of the propery
     * @param property The property
     * @param defaultValue The default value to test
     * @param minValue The minimum value to test
     * @param maxValue The maximum value to test
     * @param testValue Another value to test
     * @param defaultDisplayValue The displayed string value
     */
    public void testSettableRangedValue (final String propertyName, final SettableRangedValue property, final Double defaultValue, final Double minValue, final Double maxValue, final Double testValue, final String defaultDisplayValue)
    {
        final PropertyTestValues<RangedValue, Double> propertyObject = this.enableRangedValue (propertyName, property, defaultValue, minValue, maxValue, testValue);
        this.host.scheduleTask ( () -> this.delayedTestValue (propertyObject, Double.valueOf (0.0)), ANSWER_DELAY);
        if (defaultDisplayValue != null)
            this.testStringValue (propertyName + ".displayedValue", property.displayedValue (), defaultDisplayValue);
    }


    /**
     * Test a ranged property value.
     *
     * @param propertyName The name of the propery
     * @param property The property
     * @param defaultValue The default value to test
     */
    public void testRangedValue (final String propertyName, final RangedValue property, final Double defaultValue)
    {
        this.testRangedValue (propertyName, property, defaultValue, null);
    }


    /**
     * Test a ranged property value.
     *
     * @param propertyName The name of the propery
     * @param property The property
     * @param defaultValue The default value to test
     * @param defaultDisplayValue The displayed string value
     */
    public void testRangedValue (final String propertyName, final RangedValue property, final Double defaultValue, final String defaultDisplayValue)
    {
        final PropertyTestValues<RangedValue, Double> propertyObject = this.enableRangedValue (propertyName, property, defaultValue, null, null, null);
        this.host.scheduleTask ( () -> this.delayedTestValue (propertyObject, Double.valueOf (0.0)), ANSWER_DELAY);
        if (defaultDisplayValue != null)
            this.testStringValue (propertyName + ".displayedValue", property.displayedValue (), defaultDisplayValue);
    }


    /**
     * Test an enum property value.
     *
     * @param propertyName The name of the propery
     * @param property The property
     * @param defaultValues The default values
     * @param minValue The minimum value to test
     * @param maxValue The maximum value to test
     * @param testValue Another value to test
     */
    public void testEnumValue (final String propertyName, final EnumValue property, final Set<String> defaultValues, final String minValue, final String maxValue, final String testValue)
    {
        final PropertyTestValues<EnumValue, String> propertyObject = this.enableEnumValue (propertyName, property, defaultValues, minValue, maxValue, testValue);
        this.host.scheduleTask ( () -> this.delayedTestValue (propertyObject, minValue), ANSWER_DELAY);
    }


    /**
     * Test a color property value.
     *
     * @param propertyName The name of the propery
     * @param property The property
     * @param redValue The value for red
     * @param greenValue The value for green
     * @param blueValue The value for blue
     */
    public void testColorValue (final String propertyName, final ColorValue property, final Double redValue, final Double greenValue, final Double blueValue)
    {
        final PropertyTestValues<ColorValue, Double []> propertyObject = this.enableColorValue (propertyName, property, new Double []
        {
            redValue,
            greenValue,
            blueValue
        });
        this.host.scheduleTask ( () -> this.delayedTestColorValue (propertyObject), ANSWER_DELAY);
    }


    private void delayedTestColorValue (final PropertyTestValues<ColorValue, Double []> propertyObject)
    {
        this.logPropertyName (propertyObject);

        final ColorValue property = propertyObject.getProperty ();
        final float valueRed = property.red ();
        final float valueGreen = property.green ();
        final float valueBlue = property.blue ();

        final Set<Double []> defaultValues = propertyObject.getDefaultValues ();
        if (defaultValues.isEmpty ())
            return;

        final Double [] defaultValue = defaultValues.iterator ().next ();

        this.scheduleFunction ( () -> this.assertEquals ("Red", defaultValue[0], Double.valueOf (valueRed)));
        this.scheduleFunction ( () -> this.assertEquals ("Green", defaultValue[1], Double.valueOf (valueGreen)));
        this.scheduleFunction ( () -> this.assertEquals ("Blue", defaultValue[2], Double.valueOf (valueBlue)));
    }


    private void delayedTestBooleanValue (final PropertyTestValues<BooleanValue, Boolean> propertyObject)
    {
        // Note that Value is actually also the SettableValue interface and only different in the
        // documentation! Therefore, you need to give no value for minValue to cancel here
        final Boolean minValue = propertyObject.getMinValue ();
        if (minValue == null)
        {
            this.delayedTestValueDefaultValue (propertyObject);
            this.scheduleFunction ( () -> this.logger.info ("Value is readonly. Done.", 2));
            return;
        }

        final SettableBooleanValue property = (SettableBooleanValue) propertyObject.getProperty ();

        final Boolean value = this.delayedTestSettableValueDefaultValue (propertyObject);

        // Don't toggle if not possible
        if (minValue.equals (propertyObject.getMaxValue ()))
        {
            this.scheduleFunction ( () -> this.logger.info ("Value can't be toggled. Done.", 2));
            return;
        }

        // Test if toggling a boolean value works
        this.scheduleFunction (property::toggle);
        this.scheduleFunction ( () -> this.assertEqualsValue ("Toggled", Boolean.valueOf (!value.booleanValue ()), propertyObject));

        this.delayedTestValueMinMaxValues (propertyObject);
        this.delayedResetValue (property, value);
        this.delayedDisableValueUpdates (property);

        // Retest, but value must not update
        // Test if toggling a boolean value works
        this.scheduleFunction (property::toggle);
        // false is retrieved from getter when off
        this.scheduleFunction ( () -> this.assertOffEqualsValue ("(Off) Toggled", Boolean.FALSE, propertyObject));

        this.delayedEnableValueUpdates (property);

        // Now the value update must fire again!
        this.scheduleFunction ( () -> this.assertEqualsValue ("(On) Toggled", Boolean.valueOf (!value.booleanValue ()), propertyObject));

        this.delayedResetValue (property, value);
    }


    private <V extends Value<?>, T> void delayedTestValue (final PropertyTestValues<V, T> propertyObject, final T expectedDisabledValue)
    {
        final V property = propertyObject.getProperty ();
        if (isSettable (property))
        {
            this.delayedTestValueDefaultValue (propertyObject);
            this.scheduleFunction ( () -> this.logger.info ("Value is readonly. Done.", 2));
            return;
        }

        final T value = this.delayedTestSettableValueDefaultValue (propertyObject);

        if (propertyObject.getMinValue () == null)
            return;

        // Test setting to a test value
        final T testValue = propertyObject.getTestValue ();
        this.scheduleFunction ( () -> this.setPropertyValue (property, testValue));
        this.scheduleFunction ( () -> this.assertEqualsValue ("Test", testValue, propertyObject));

        this.delayedTestValueMinMaxValues (propertyObject);
        this.delayedResetValue (property, value);
        this.delayedDisableValueUpdates (property);

        // Retest, but now value must not update
        this.scheduleFunction ( () -> this.setPropertyValue (property, testValue));
        this.scheduleFunction ( () -> this.assertOffEqualsValue ("(Off) Test", expectedDisabledValue, propertyObject));

        this.delayedEnableValueUpdates (property);

        // Don't change if not possible
        if (!propertyObject.getMinValue ().equals (propertyObject.getMaxValue ()))
        {
            // Now the value update must fire!
            this.scheduleFunction ( () -> this.setPropertyValue (property, testValue));
            this.scheduleFunction ( () -> this.assertEqualsValue ("(On) Test", testValue, propertyObject));
        }

        this.delayedResetValue (property, value);
    }


    private <V extends Value<?>, T> void delayedTestValueDefaultValue (final PropertyTestValues<V, T> propertyObject)
    {
        this.logPropertyName (propertyObject);
        this.scheduleFunction ( () -> this.assertEquals ("Default", propertyObject.getDefaultValues (), propertyObject.getObserved ()));
    }


    @SuppressWarnings("unchecked")
    private <V extends Value<?>, T> T delayedTestSettableValueDefaultValue (final PropertyTestValues<V, T> propertyObject)
    {
        this.logPropertyName (propertyObject);

        // Test if the expected default value ist set
        final Object value = this.getPropertyValue (propertyObject.getProperty ());
        this.scheduleFunction ( () -> this.assertEquals ("Default", propertyObject.getDefaultValues (), value));
        return (T) value;
    }


    private <V extends Value<?>, T> void logPropertyName (final PropertyTestValues<V, T> propertyObject)
    {
        this.scheduleFunction ( () -> this.logger.info ("Test property " + propertyObject.getName (), 1));
    }


    private <V extends Value<?>, T> void delayedTestValueMinMaxValues (final PropertyTestValues<V, T> propertyObject)
    {
        final V property = propertyObject.getProperty ();

        // Test setting to a 'minimum' value
        this.scheduleFunction ( () -> this.setPropertyValue (property, propertyObject.getMinValue ()));

        this.scheduleFunction ( () -> this.assertEqualsValue ("Min", propertyObject.getMinValue (), propertyObject));

        // Test setting to a 'maximum' value
        this.scheduleFunction ( () -> this.setPropertyValue (property, propertyObject.getMaxValue ()));
        this.scheduleFunction ( () -> this.assertEqualsValue ("Max", propertyObject.getMaxValue (), propertyObject));
    }


    private void delayedTestFormattedBeatTime (final PropertyTestValues<SettableBeatTimeValue, Double> propertyObject, final String expectedFormattedBeatTime)
    {
        this.scheduleFunction ( () -> this.assertEquals (propertyObject.getName () + ".getFormatted", expectedFormattedBeatTime, propertyObject.getProperty ().getFormatted ()));
    }


    private void delayedDisableValueUpdates (final Value<?> property)
    {
        this.scheduleFunction ( () -> property.setIsSubscribed (false));
    }


    private void delayedEnableValueUpdates (final Value<?> property)
    {
        this.scheduleFunction ( () -> property.setIsSubscribed (true));
    }


    private void delayedResetValue (final Value<?> property, final Object value)
    {
        this.scheduleFunction ( () -> this.logger.info ("Resetting value to " + value.toString () + ".", 2));
        this.scheduleFunction ( () -> this.setPropertyValue (property, value));
    }


    private PropertyTestValues<BooleanValue, Boolean> enableBooleanValue (final String propertyName, final BooleanValue property, final Boolean defaultValue, final Boolean minValue, final Boolean maxValue, final Boolean testValue)
    {
        this.enableGetter (propertyName, property);
        final PropertyTestValues<BooleanValue, Boolean> propertyObject = new PropertyTestValues<> (propertyName, property, defaultValue == null ? BOOLEAN_OPTS : Collections.singleton (defaultValue), minValue, maxValue, testValue);
        if (this.testObserver ())
        {
            this.logger.info (OBSERVING_PROPERTY + propertyName, 1);
            property.addValueObserver (propertyObject::setObserved);
        }
        return propertyObject;
    }


    private PropertyTestValues<TimeSignatureValue, String> enableTimeSignatureValue (final String propertyName, final TimeSignatureValue property, final String defaultValue, final String minValue, final String maxValue, final String testValue)
    {
        this.enableGetter (propertyName, property);
        final PropertyTestValues<TimeSignatureValue, String> propertyObject = new PropertyTestValues<> (propertyName, property, defaultValue, minValue, maxValue, testValue);
        if (this.testObserver ())
        {
            this.logger.info (OBSERVING_PROPERTY + propertyName, 1);
            property.addValueObserver (propertyObject::setObserved);
        }
        return propertyObject;
    }


    private PropertyTestValues<ColorValue, Double []> enableColorValue (final String propertyName, final ColorValue property, final Double [] defaultValue)
    {
        this.enableGetter (propertyName, property);
        final PropertyTestValues<ColorValue, Double []> propertyObject = new PropertyTestValues<> (propertyName, property, defaultValue, null, null, null);
        if (this.testObserver ())
        {
            this.logger.info (OBSERVING_PROPERTY + propertyName, 1);
            property.addValueObserver ( (red, green, blue) -> propertyObject.setObserved (new Double []
            {
                Double.valueOf (red),
                Double.valueOf (green),
                Double.valueOf (blue)
            }));
        }
        return propertyObject;
    }


    private PropertyTestValues<StringValue, String> enableStringValue (final String propertyName, final StringValue property, final Set<String> defaultValue)
    {
        return this.enableStringValue (propertyName, property, defaultValue, null, null, null);
    }


    private PropertyTestValues<StringValue, String> enableStringValue (final String propertyName, final StringValue property, final Set<String> defaultValue, final String minValue, final String maxValue, final String testValue)
    {
        this.enableGetter (propertyName, property);
        final PropertyTestValues<StringValue, String> propertyObject = new PropertyTestValues<> (propertyName, property, defaultValue, minValue, maxValue, testValue);
        if (this.testObserver ())
        {
            this.logger.info (OBSERVING_PROPERTY + propertyName, 1);
            property.addValueObserver (propertyObject::setObserved);
        }
        return propertyObject;
    }


    private PropertyTestValues<StringArrayValue, String []> enableStringArrayValue (final String propertyName, final StringArrayValue property, final String [] defaultValue, final String [] minValue, final String [] maxValue, final String [] testValue)
    {
        this.enableGetter (propertyName, property);
        final PropertyTestValues<StringArrayValue, String []> propertyObject = new PropertyTestValues<> (propertyName, property, defaultValue, minValue, maxValue, testValue);
        if (this.testObserver ())
        {
            this.logger.info (OBSERVING_PROPERTY + propertyName, 1);
            property.addValueObserver (propertyObject::setObserved);
        }
        return propertyObject;
    }


    private PropertyTestValues<IntegerValue, Integer> enableIntegerValue (final String propertyName, final IntegerValue property, final Integer defaultValue, final Integer minValue, final Integer maxValue, final Integer testValue)
    {
        this.enableGetter (propertyName, property);
        final PropertyTestValues<IntegerValue, Integer> propertyObject = new PropertyTestValues<> (propertyName, property, defaultValue, minValue, maxValue, testValue);
        if (this.testObserver ())
        {
            this.logger.info (OBSERVING_PROPERTY + propertyName, 1);
            property.addValueObserver (value -> propertyObject.setObserved (Integer.valueOf (value)));
        }
        return propertyObject;
    }


    private PropertyTestValues<SettableBeatTimeValue, Double> enableSettableBeatTimeValue (final String propertyName, final SettableBeatTimeValue property, final Double defaultValue, final Double minValue, final Double maxValue, final Double testValue)
    {
        this.enableGetter (propertyName, property);
        final PropertyTestValues<SettableBeatTimeValue, Double> propertyObject = new PropertyTestValues<> (propertyName, property, defaultValue, minValue, maxValue, testValue);
        if (this.testObserver ())
        {
            this.logger.info (OBSERVING_PROPERTY + propertyName, 1);
            property.addValueObserver (value -> propertyObject.setObserved (Double.valueOf (value)));
        }
        return propertyObject;
    }


    private PropertyTestValues<SettableEnumValue, String> enableSettableEnumValue (final String propertyName, final SettableEnumValue property, final String defaultValue, final String testValue)
    {
        this.enableGetter (propertyName, property);
        final PropertyTestValues<SettableEnumValue, String> propertyObject = new PropertyTestValues<> (propertyName, property, defaultValue, testValue, testValue, testValue);
        if (this.testObserver ())
        {
            this.logger.info (OBSERVING_PROPERTY + propertyName, 1);
            property.addValueObserver (propertyObject::setObserved);
        }
        return propertyObject;
    }


    private PropertyTestValues<DoubleValue, Double> enableDoubleValue (final String propertyName, final DoubleValue property, final Double defaultValue, final Double minValue, final Double maxValue, final Double testValue)
    {
        this.enableGetter (propertyName, property);
        final PropertyTestValues<DoubleValue, Double> propertyObject = new PropertyTestValues<> (propertyName, property, defaultValue, minValue, maxValue, testValue);
        if (this.testObserver ())
        {
            this.logger.info (OBSERVING_PROPERTY + propertyName, 1);
            property.addValueObserver (value -> propertyObject.setObserved (Double.valueOf (value)));
        }
        return propertyObject;
    }


    private PropertyTestValues<RangedValue, Double> enableRangedValue (final String propertyName, final RangedValue property, final Double defaultValue, final Double minValue, final Double maxValue, final Double testValue)
    {
        this.enableGetter (propertyName, property);
        final PropertyTestValues<RangedValue, Double> propertyObject = new PropertyTestValues<> (propertyName, property, defaultValue, minValue, maxValue, testValue);
        if (this.testObserver ())
        {
            this.logger.info (OBSERVING_PROPERTY + propertyName, 1);
            property.addValueObserver (value -> propertyObject.setObserved (Double.valueOf (value)));
        }
        return propertyObject;
    }


    private PropertyTestValues<EnumValue, String> enableEnumValue (final String propertyName, final EnumValue property, final Set<String> defaultValue, final String minValue, final String maxValue, final String testValue)
    {
        this.enableGetter (propertyName, property);
        final PropertyTestValues<EnumValue, String> propertyObject = new PropertyTestValues<> (propertyName, property, defaultValue, minValue, maxValue, testValue);
        if (this.testObserver ())
        {
            this.logger.info (OBSERVING_PROPERTY + propertyName, 1);
            property.addValueObserver (propertyObject::setObserved);
        }
        return propertyObject;
    }


    private void enableGetter (final String propertyName, final Value<?> property)
    {
        if (!this.testGetter ())
            return;
        this.logger.info ("Marking property " + propertyName, 1);
        property.markInterested ();
    }


    private <V extends Value<?>, T> void assertOffEqualsValue (final String name, final T expectedValue, final PropertyTestValues<V, T> propertyObject)
    {
        if (this.testGetter ())
        {
            try
            {
                this.getPropertyValue (propertyObject.getProperty ());
                this.logger.error (name + "  (get): No exception was thrown but should be!", 2);
            }
            catch (final RuntimeException ex)
            {
                this.logger.info (name + "  (get): '" + ex.getLocalizedMessage () + "', OK.", 2);
            }
        }

        if (this.testObserver ())
            this.assertEquals (name + " (observed)", expectedValue, propertyObject.getObserved ());
    }


    private <V extends Value<?>, T> void assertEqualsValue (final String name, final T expectedValue, final PropertyTestValues<V, T> propertyObject)
    {
        if (this.testGetter ())
            this.assertEquals (name + " (get)", expectedValue, this.getPropertyValue (propertyObject.getProperty ()));
        if (this.testObserver ())
            this.assertEquals (name + " (observed)", expectedValue, propertyObject.getObserved ());
    }


    private void assertEquals (final String name, final Object expectedValue, final Object actualValue)
    {
        // Check for several possible result values
        if (expectedValue instanceof Set)
        {
            this.printEqualsMessage (name, ((Set<?>) expectedValue).contains (actualValue), expectedValue, actualValue, 2);
            return;
        }

        // Compare string array
        if (expectedValue instanceof String [])
        {
            final String [] ev = (String []) expectedValue;
            boolean isEqual = false;
            if (actualValue != null)
            {
                final String [] av = (String []) actualValue;
                isEqual = ev.length == av.length;
                if (isEqual)
                {
                    for (int i = 0; i < ev.length; i++)
                    {
                        isEqual = ev[i].equals (av[i]);
                        if (!isEqual)
                            break;
                    }
                }
            }
            this.printEqualsMessage (name, isEqual, expectedValue, actualValue, 2);
            return;
        }

        if (expectedValue instanceof Double)
        {
            final boolean isEqual = ((Double) expectedValue).doubleValue () - ((Number) actualValue).doubleValue () < 0.00001;
            this.printEqualsMessage (name, isEqual, expectedValue, actualValue, 2);
            return;
        }

        final boolean isEquals = (expectedValue == null && actualValue == null) || (expectedValue != null && expectedValue.equals (actualValue));
        this.printEqualsMessage (name, isEquals, expectedValue, actualValue, 2);
    }


    private void printEqualsMessage (final String prefix, final boolean condition, final Object expectedValue, final Object actualValue, final int padDepth)
    {
        if (condition)
            this.logger.info (prefix + " value was '" + printValue (actualValue) + "', OK.", padDepth);
        else
            this.logger.error (prefix + " value was '" + printValue (actualValue) + "' but must be '" + printValue (expectedValue) + "'.", padDepth);
    }


    private static String printValue (final Object value)
    {
        if (value == null)
            return "null";

        if (value instanceof String [])
            return printValue ((String []) value);

        if (value instanceof Collection)
            return printValue ((Collection<?>) value);

        return value.toString ();
    }


    private static String printValue (final String [] value)
    {
        return printValue (Arrays.asList (value));
    }


    private static String printValue (final Collection<?> value)
    {
        if (value.size () == 1)
            return printValue (value.iterator ().next ());

        final StringBuilder sb = new StringBuilder ();
        for (final Object s: value)
        {
            if (sb.length () > 0)
                sb.append (", ");
            sb.append (printValue (s));
        }
        return "{ " + sb.append (" }").toString ();
    }


    /**
     * Test the given object for null.
     *
     * @param message The message to display
     * @param object The object to test
     */
    public void assertNotNull (final String message, final Object object)
    {
        if (object == null)
            this.logger.error (message, 1);
    }


    /**
     * Execute all scheduled test functions.
     */
    public void executeScheduler ()
    {
        if (this.scheduler.isEmpty ())
        {
            this.logger.finish ();
            return;
        }

        int delay = ANSWER_DELAY;

        final Runnable exec = this.scheduler.remove ();
        try
        {
            exec.run ();

            if (exec instanceof ModuleSection)
            {
                final ModuleSection ms = (ModuleSection) exec;
                if (!ms.isEnabled ())
                {
                    this.logger.info ("Skipping disabled module " + ms.moduleName, 1);
                    while (!this.scheduler.isEmpty () && !(this.scheduler.getFirst () instanceof ModuleSection))
                        this.scheduler.remove ();
                }
            }
            else if (exec instanceof BrowserModule.BrowserStarter)
            {
                delay = 2000;
                this.logger.info ("Waiting 2 seconds for browser...", 1);
            }
        }
        catch (final RuntimeException ex)
        {
            this.logger.error (ex.getLocalizedMessage (), 2);
        }

        this.host.scheduleTask (this::executeScheduler, delay);
    }


    /**
     * Schedule a function for later test processing.
     *
     * @param f The function to schedule
     */
    public void scheduleFunction (final Runnable f)
    {
        this.scheduler.add (f);
    }


    private static <V extends Value<?>> boolean isSettable (final V property)
    {
        return SETTABLE_CLASSES.contains (property.getClass ());
    }


    private <V extends Value<?>> Object getPropertyValue (final V property)
    {
        if (property instanceof BooleanValue)
            return Boolean.valueOf (((BooleanValue) property).get ());
        if (property instanceof StringValue)
            return ((StringValue) property).get ();
        if (property instanceof IntegerValue)
            return Integer.valueOf (((IntegerValue) property).get ());
        if (property instanceof DoubleValue)
            return Double.valueOf (((DoubleValue) property).get ());
        if (property instanceof RangedValue)
            return Double.valueOf (((RangedValue) property).get ());
        if (property instanceof EnumValue)
            return ((EnumValue) property).get ();
        if (property instanceof TimeSignatureValue)
            return ((TimeSignatureValue) property).get ();
        if (property instanceof StringArrayValue)
            return ((StringArrayValue) property).get ();

        this.logger.error ("Not implemented property type: " + property.getClass (), 2);
        return null;
    }


    private <V extends Value<?>, T> void setPropertyValue (final V property, final T testValue)
    {
        if (property instanceof SettableBooleanValue)
        {
            ((SettableBooleanValue) property).set (((Boolean) testValue).booleanValue ());
            return;
        }

        if (property instanceof SettableStringValue)
        {
            ((SettableStringValue) property).set ((String) testValue);
            return;
        }

        if (property instanceof SettableIntegerValue)
        {
            ((SettableIntegerValue) property).set (((Integer) testValue).intValue ());
            return;
        }

        if (property instanceof SettableDoubleValue)
        {
            ((SettableDoubleValue) property).set (((Double) testValue).doubleValue ());
            return;
        }

        if (property instanceof SettableEnumValue)
        {
            ((SettableEnumValue) property).set ((String) testValue);
            return;
        }

        if (property instanceof SettableRangedValue)
        {
            ((SettableRangedValue) property).setImmediately (((Double) testValue).doubleValue ());
            return;
        }

        if (property instanceof TimeSignatureValue)
        {
            ((TimeSignatureValue) property).set ((String) testValue);
            return;
        }

        this.logger.error ("Not implemented settable property type: " + property.getClass (), 2);
    }


    private boolean testObserver ()
    {
        return BooleanSetting.isTrue (this.observerSetting);
    }


    private boolean testGetter ()
    {
        return BooleanSetting.isTrue (this.getterSetting);
    }

    class ModuleSection implements Runnable
    {
        String                    moduleName;
        private SettableEnumValue setting;


        public ModuleSection (final String moduleName, final SettableEnumValue setting)
        {
            this.moduleName = moduleName;
            this.setting = setting;
        }


        @Override
        public void run ()
        {
            TestFramework.this.logger.header (this.moduleName + " module tests are starting ...");
        }


        public boolean isEnabled ()
        {
            return BooleanSetting.isTrue (this.setting);
        }
    }
}
