// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers;

import com.bitwig.extension.controller.api.Value;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;


/**
 * Stores the necessary values for testing a value property.
 *
 * @param <V> The specific value type
 * @param <T> The specific type stored in the value property
 * 
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class PropertyTestValues<V extends Value<?>, T>
{
    private final String name;
    private final V      property;
    private final Set<T> defaultValues;
    private final T      minValue;
    private final T      maxValue;
    private final T      testValue;
    private T            observed;


    /**
     * Constructor.
     *
     * @param name The name of the propery
     * @param property The property
     * @param defaultValue The default value to test
     * @param minValue The minimum value to test
     * @param maxValue The maximum value to test
     * @param testValue Another value to test
     */
    public PropertyTestValues (final String name, final V property, final T defaultValue, final T minValue, final T maxValue, final T testValue)
    {
        this (name, property, Collections.singleton (defaultValue), minValue, maxValue, testValue);
    }


    /**
     * Constructor.
     *
     * @param name The name of the propery
     * @param property The property
     * @param defaultValues The default values to test
     * @param minValue The minimum value to test
     * @param maxValue The maximum value to test
     * @param testValue Another value to test
     */
    public PropertyTestValues (final String name, final V property, final Collection<T> defaultValues, final T minValue, final T maxValue, final T testValue)
    {
        this (name, property, new HashSet<T> (defaultValues), minValue, maxValue, testValue);
    }


    /**
     * Constructor.
     *
     * @param name The name of the propery
     * @param property The property
     * @param defaultValues The default values to test
     * @param minValue The minimum value to test
     * @param maxValue The maximum value to test
     * @param testValue Another value to test
     */
    public PropertyTestValues (final String name, final V property, final Set<T> defaultValues, final T minValue, final T maxValue, final T testValue)
    {
        this.name = name;
        this.property = property;
        this.defaultValues = defaultValues;
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.testValue = testValue;
    }


    /**
     * Get the observed value.
     *
     * @return The observed value
     */
    public T getObserved ()
    {
        return this.observed;
    }


    /**
     * Set the observed value.
     *
     * @param observed The observed value
     */
    public void setObserved (final T observed)
    {
        this.observed = observed;
    }


    /**
     * Get the property name.
     *
     * @return The property name
     */
    public String getName ()
    {
        return this.name;
    }


    /**
     * Get the property.
     *
     * @return The property
     */
    public V getProperty ()
    {
        return this.property;
    }


    /**
     * Get the default values.
     *
     * @return The default values
     */
    public Set<T> getDefaultValues ()
    {
        return this.defaultValues;
    }


    /**
     * Get the minimum value.
     *
     * @return The minimum value
     */
    public T getMinValue ()
    {
        return this.minValue;
    }


    /**
     * Get the maximum value.
     *
     * @return The maximum value
     */
    public T getMaxValue ()
    {
        return this.maxValue;
    }


    /**
     * Get the test value.
     *
     * @return The test value
     */
    public T getTestValue ()
    {
        return this.testValue;
    }
}
