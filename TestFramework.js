// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

const BOOLEAN_OPTS = new MultiResult ([ false, true ]);

function testBooleanProperty (propertyName, property)
{
    testProperty (propertyName, property, BOOLEAN_OPTS);
}

function testSettableBooleanProperty (propertyName, property)
{
    testProperty (propertyName, property, BOOLEAN_OPTS, false, true);
}

/**
 * Test Value and SettableValue interfaces.
 */
function testProperty (propertyName, property, defaultValue, minValue, maxValue, testValue)
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
    properties.push (propertyObject);
}

function testProperties ()
{
    for (var i = 0; i < properties.length; i++)
        testPropertyDelayed (properties[i]);
    executeScheduler ();
}

function testPropertyDelayed (propertyObject)
{
    scheduleFunction (println, [ "Test property " + propertyObject.name ]);
    
    // Test if the expected default value ist set
    var value = propertyObject.property.get ();
    scheduleFunction (assertEquals, [ "Default", propertyObject.defaultValue, value ]);
    
    if (propertyObject.property.set)
    {
        if (typeof (propertyObject.minValue) == "undefined")
        {
            scheduleFunction (errorln, [ PAD + "There are no test values given. Add them or implement the Property readonly." ]);
            return;
        }
        
        if (propertyObject.property.toggle)
        {
            // Don't toggle if not possible
            if (propertyObject.minValue !== propertyObject.maxValue)
            {
                // Test if toggling a boolean value works
                scheduleFunction (function (propertyObject) { propertyObject.property.toggle (); }, [ propertyObject ]);
                scheduleFunction (assertEqualsProperty, [ "Toggled", !value, propertyObject ]);
            }
        }
        else
        {
            // Test setting to a test value
            scheduleFunction (function (propertyObject, value) { propertyObject.property.set (value); }, [ propertyObject, propertyObject.testValue ]);
            scheduleFunction (assertEqualsProperty, [ "Test", propertyObject.testValue, propertyObject ]);
        }
        
        // Test setting to a 'minimum' value
        scheduleFunction (function (propertyObject, value) { propertyObject.property.set (value); }, [ propertyObject, propertyObject.minValue ]);
        scheduleFunction (assertEqualsProperty, [ "Min", propertyObject.minValue, propertyObject ]);

        // Test setting to a 'maximum' value
        scheduleFunction (function (propertyObject, value) { propertyObject.property.set (value); }, [ propertyObject, propertyObject.maxValue ]);
        scheduleFunction (assertEqualsProperty, [ "Max", propertyObject.maxValue, propertyObject ]);

        // Reset to original value
        scheduleFunction (function (propertyObject, value) { propertyObject.property.set (value); }, [ propertyObject, value ]);
        scheduleFunction (assertEqualsProperty, [ "Reset to default", value, propertyObject ]);

        // Turn off value updates
        scheduleFunction (function (property) { property.setIsSubscribed (false); }, [ propertyObject.property ]);
        
        // Retest, but value must not update
        if (propertyObject.property.toggle)
        {
            // Don't toggle if not possible
            if (propertyObject.minValue !== propertyObject.maxValue)
            {
                // Test if toggling a boolean value works
                scheduleFunction (function (propertyObject) { propertyObject.property.toggle (); }, [ propertyObject ]);
                // false is retrieved from getter when off
                scheduleFunction (assertEqualsProperty, [ "(Off) Toggled", false, propertyObject ]);
            }
        }
        else
        {
            // Test setting to a test value
            scheduleFunction (function (propertyObject, value) { propertyObject.property.set (value); }, [ propertyObject, propertyObject.testValue ]);
            // -1 (integer), 0 (float) or '' is retrieved from getter when off
            scheduleFunction (assertEqualsProperty, [ "(Off) Test", typeof (value) == "string" ? '' : new MultiResult ([-1, 0]), propertyObject ]);
        }

        // Turn on value updates
        scheduleFunction (function (property) { property.setIsSubscribed (true); }, [ propertyObject.property ]);

        // Don't toggle if not possible
        if (propertyObject.minValue !== propertyObject.maxValue)
        {
            // Now the value update must fire!
            if (propertyObject.property.toggle)
                scheduleFunction (assertEqualsProperty, [ "(On) Toggled", !value, propertyObject ]);
            else
                scheduleFunction (assertEqualsProperty, [ "(On) Test", propertyObject.testValue, propertyObject ]);
        }
        
        // Reset to original value
        scheduleFunction (function (propertyObject, value) { propertyObject.property.set (value); }, [ propertyObject, value ]);
    }
    else
        scheduleFunction (println, [ PAD + "Property is readonly. Done." ]);
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
        if (expectedValue.checkResult (actualValue))
            println (PAD + name + " value should be in '" + expectedValue.printOptions () + "', OK.");
        else
            errorln (PAD + name + " value should be '" + expectedValue.printOptions () + "' but was '" + actualValue + "'.");
        return;
    }
    
    if (typeof (actualValue) == "object")
    {
        // Check for array
        if (typeof (expectedValue) == "undefined" && actualValue.length)
        {
            // Only check that it is not empty
            if (actualValue.length > 0)
            {
                println (PAD + name + " value should be a non-empty array, OK.");
                return;
            }
            // Fall through for not supported object cases
        }
    }
    
    if (expectedValue === actualValue)
        println (PAD + name + " value should be '" + expectedValue + "', OK.");
    else
        errorln (PAD + name + " value should be '" + expectedValue + "' but was '" + actualValue + "'.");
}

function assertNotNull (message, object)
{
    if (object == null)
        errorln (message);
}

function scheduleFunction (f, args)
{
    scheduler.push ({ f: f, args: args });
}

function executeScheduler ()
{
    if (scheduler.length == 0)
    {
        println ("----------------------------------------------------------------------");
        println ("Finished.");
        return;
    }
    delay (function ()
    {
        var exec = scheduler.shift ();
        exec.f.apply (this, exec.args);
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

function errorln (message)
{
    host.errorln (message);
}
