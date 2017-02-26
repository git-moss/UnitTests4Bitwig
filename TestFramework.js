// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

const BOOLEAN_OPTS = new MultiResult ([ false, true ]);
const LOG = new Logger ();

/**
 * Note that Value is actually also the SettableValue interface and only different in the documentation!
 */
function testBooleanProperty (propertyName, property, defaultValue)
{
    var propertyObject = enableProperty (propertyName, property, defaultValue ? defaultValue : BOOLEAN_OPTS);
    delay (delayedTestBooleanProperty, [ propertyObject ]); 
}

function testSettableBooleanProperty (propertyName, property, defaultValue, minValue, maxValue)
{
    var propertyObject = enableProperty (propertyName, property, typeof (defaultValue) == 'undefined' ? BOOLEAN_OPTS : defaultValue, typeof (minValue) == 'undefined' ? false : minValue, typeof (maxValue) == 'undefined' ? true : maxValue);
    delay (delayedTestBooleanProperty, [ propertyObject ]); 
}

function testStringProperty (propertyName, property, defaultValue, minValue, maxValue, testValue)
{
    var propertyObject = enableProperty (propertyName, property, defaultValue, minValue, maxValue, testValue);
    delay (delayedTestProperty, [ propertyObject, '']);
}

function testIntegerProperty (propertyName, property, defaultValue, minValue, maxValue, testValue)
{
    var propertyObject = enableProperty (propertyName, property, defaultValue, minValue, maxValue, testValue);
    delay (delayedTestProperty, [ propertyObject, -1]);
}

function testFloatProperty (propertyName, property, defaultValue, minValue, maxValue, testValue)
{
    var propertyObject = enableProperty (propertyName, property, defaultValue, minValue, maxValue, testValue);
    delay (delayedTestProperty, [ propertyObject, 0]);
}

function testEnumProperty (propertyName, property, defaultValueList, minValue, maxValue, testValue)
{
    var propertyObject = enableProperty (propertyName, property, defaultValueList, minValue, maxValue, testValue);
    delay (delayedTestProperty, [ propertyObject, defaultValueList.getDefaultValue ()]);
}

/**
 * Test Value and SettableValue interfaces.
 */
function testProperty (propertyName, property, defaultValue, minValue, maxValue, testValue)
{
    var propertyObject = enableProperty (propertyName, property, defaultValue, minValue, maxValue, testValue);
    delay (delayedTestProperty, [ propertyObject ]);
}


///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// Private
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

function enableProperty (propertyName, property, defaultValue, minValue, maxValue, testValue)
{
    if (TEST_PROPERTY_GETTER)
    {
        println ("Marking property " + propertyName);
        property.markInterested ();
    }
    var propertyObject = { name: propertyName, property: property, defaultValue: defaultValue, minValue: minValue, maxValue: maxValue, testValue: testValue, observed: null };
    if (TEST_PROPERTY_OBSERVER)
    {
        println ("Observing property " + propertyName);
        property.addValueObserver (doObject (propertyObject, function (value) { this.observed = value; }));
    }
    return propertyObject;
}

function delayedTestBooleanProperty (propertyObject)
{
    var value = delayedTestPropertyDefaultValue (propertyObject);

    // Note that Value is actually also the SettableValue interface and only different in the documentation!
    // Therefore, you need to give no value for minValue to cancel here
    if (typeof (propertyObject.minValue) == "undefined")
    {
        scheduleFunction (logInfo, [ "Property is readonly. Done." ]);
        return;
    }
        
    // Don't toggle if not possible
    if (propertyObject.minValue === propertyObject.maxValue)
    {
        scheduleFunction (logInfo, [ "Property can't be toggled. Done." ]);
        return;
    }
    
    // Test if toggling a boolean value works
    scheduleFunction (function (propertyObject) { propertyObject.property.toggle (); }, [ propertyObject ]);
    scheduleFunction (assertEqualsProperty, [ "Toggled", !value, propertyObject ]);
    
    delayedTestPropertyMinMaxValues (propertyObject, value);
    delayedResetValue (propertyObject.property, value);
    delayedDisableValueUpdates (propertyObject.property);
    
    // Retest, but value must not update
    // Test if toggling a boolean value works
    scheduleFunction (function (propertyObject) { propertyObject.property.toggle (); }, [ propertyObject ]);
    // false is retrieved from getter when off
    scheduleFunction (assertEqualsProperty, [ "(Off) Toggled", false, propertyObject ]);

    delayedEnableValueUpdates (propertyObject.property);

    // Now the value update must fire again!
    scheduleFunction (assertEqualsProperty, [ "(On) Toggled", !value, propertyObject ]);
    
    delayedResetValue (propertyObject.property, value);
}

function delayedTestProperty (propertyObject, expectedDisabledValue)
{
    var value = delayedTestPropertyDefaultValue (propertyObject);

    if (!propertyObject.property.set)
    {
        scheduleFunction (logInfo, [ "Property is readonly. Done." ]);
        return;
    }
        
    if (typeof (propertyObject.minValue) == "undefined")
        return;
    
    // Test setting to a test value
    scheduleFunction (function (propertyObject, value) { propertyObject.property.set (value); }, [ propertyObject, propertyObject.testValue ]);
    scheduleFunction (assertEqualsProperty, [ "Test", propertyObject.testValue, propertyObject ]);
    
    delayedTestPropertyMinMaxValues (propertyObject, value);
    delayedResetValue (propertyObject.property, value);
    delayedDisableValueUpdates (propertyObject.property);
    
    // Retest, but now value must not update
    scheduleFunction (function (propertyObject, value) { propertyObject.property.set (value); }, [ propertyObject, propertyObject.testValue ]);
    scheduleFunction (assertEqualsProperty, [ "(Off) Test", expectedDisabledValue, propertyObject ]);

    delayedEnableValueUpdates (propertyObject.property);

    // Don't toggle if not possible
    if (propertyObject.minValue !== propertyObject.maxValue)
    {
        // Now the value update must fire!
        scheduleFunction (assertEqualsProperty, [ "(On) Test", propertyObject.testValue, propertyObject ]);
    }
    
    delayedResetValue (propertyObject.property, value);
}

function delayedTestPropertyDefaultValue (propertyObject)
{
    scheduleFunction (println, [ "Test property " + propertyObject.name ]);
    
    // Test if the expected default value ist set
    var value = propertyObject.property.get ();
    scheduleFunction (assertEquals, [ "Default", propertyObject.defaultValue, value ]);
    return value;
}

function delayedTestPropertyMinMaxValues (propertyObject, defaultValue)
{
    // Test setting to a 'minimum' value
    scheduleFunction (function (propertyObject, value) { propertyObject.property.set (value); }, [ propertyObject, propertyObject.minValue ]);
    scheduleFunction (assertEqualsProperty, [ "Min", propertyObject.minValue, propertyObject ]);

    // Test setting to a 'maximum' value
    scheduleFunction (function (propertyObject, value) { propertyObject.property.set (value); }, [ propertyObject, propertyObject.maxValue ]);
    scheduleFunction (assertEqualsProperty, [ "Max", propertyObject.maxValue, propertyObject ]);
}

function delayedDisableValueUpdates (property)
{
    scheduleFunction (function (property) { property.setIsSubscribed (false); }, [ property ]);
}

function delayedEnableValueUpdates (property)
{
    scheduleFunction (function (property) { property.setIsSubscribed (true); }, [ property ]);
}

function delayedResetValue (property, value)
{
    scheduleFunction (function (property, value) { property.set (value); }, [ property, value ]);
}

function assertEqualsProperty (name, expectedValue, propertyObject)
{
    if (TEST_PROPERTY_GETTER)
        assertEquals (name + " (get)", expectedValue, propertyObject.property.get ());
    if (TEST_PROPERTY_OBSERVER)
        assertEquals (name + " (observed)", expectedValue, propertyObject.observed);
}

function assertEquals (name, expectedValue, actualValue)
{
    // Check for several possible result values
    if (expectedValue instanceof MultiResult)
    {
        LOG.equals (name, expectedValue.checkResult (actualValue), expectedValue.printOptions (), actualValue);
        return;
    }
    
    // Check for array
    if (typeof (actualValue) == "object" && typeof (expectedValue) == "undefined" && actualValue.length)
    {
        // Only check that it is not empty
        if (actualValue.length > 0)
        {
            LOG.info ("Value should be a non-empty array, OK.");
            return;
        }
        // Fall through for not supported object cases
    }

    LOG.equals (name, expectedValue === actualValue, expectedValue, actualValue);
}

function assertNotNull (message, object)
{
    if (object == null)
        LOG.error (message);
}

function logError (message)
{
    LOG.error (message);
}

function logInfo (message)
{
    LOG.info (message);
}

function scheduleFunction (f, args)
{
    scheduler.push ({ f: f, args: args });
}

function executeScheduler ()
{
    if (scheduler.length == 0)
    {
        LOG.finish ();
        return;
    }
    delay (function ()
    {
        var exec = scheduler.shift ();
        try
        {
            exec.f.apply (this, exec.args);
        }
        catch (ex)
        {
            LOG.error (ex);
        }
        executeScheduler ();
    });
}

function delay (method, args)
{
    if (args)
        host.scheduleTask (method, args, ANSWER_DELAY);
    else
        host.scheduleTask (method, ANSWER_DELAY);
}

function doObject (object, f)
{
    return function ()
    {
        f.apply (object, arguments);
    };
}
