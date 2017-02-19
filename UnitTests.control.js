loadAPI(2);

host.defineController ("Moss", "Unit Tests", "1.0", "423793DD-89BA-49DC-9E6E-5C61FDA19E85", "Jürgen Moßgraber");

const TEST_PROPERTY_GETTER   = true;
const TEST_PROPERTY_OBSERVER = true;

const TEST_APPLICATION       = true;
const TEST_ARRANGER          = true;
const TEST_MIXER             = true;
const TEST_CURSOR_DEVICE     = true;
const TEST_REMOTE_CONTROLS   = true;
const TEST_DEVICE_SIBLINGS   = true;

const NUM_SENDS           = 2;
const NUM_PARAMS          = 2;
const NUM_DEVICES_IN_BANK = 2;

const ANSWER_DELAY = 100;

const PAD = "  - ";
const PARAMETER_VALUES = [ 
    { exists: true, name: "Pitch", value: 0.5, modulatedValue: 0.5, displayedValue: "+0.000 st" },
    { exists: true, name: "Shape", value: 0.5, modulatedValue: 0.5, displayedValue: "0.000 %" } ];
const SIBLINGS_VALUES = [ { name: "Polysynth" }, { name: "" } ]

var properties = [];
var scheduler = [];

function init ()
{
    println ("----------------------------------------------------------------------");
    
    // Test Application properties
    
    if (TEST_APPLICATION)
    {
        var application = host.createApplication ();
        assertNotNull ("Application not created.", application);
        
        testProperty ("application.hasActiveEngine", application.hasActiveEngine (), true, false, true);
        testProperty ("application.projectName", application.projectName (), "UnitTestsProject");
        testProperty ("application.panelLayout", application.panelLayout (), "ARRANGE");
    }
    
    // Test Arranger properties
    
    if (TEST_ARRANGER)
    {
        var arranger = host.createArranger ();
        testProperty ("arranger.areCueMarkersVisible", arranger.areCueMarkersVisible (), false, false, true);
        testProperty ("arranger.isPlaybackFollowEnabled", arranger.isPlaybackFollowEnabled (), true, false, true);
        testProperty ("arranger.hasDoubleRowTrackHeight", arranger.hasDoubleRowTrackHeight (), true, false, true);
        testProperty ("arranger.isClipLauncherVisible", arranger.isClipLauncherVisible (), true, false, true);
        testProperty ("arranger.isTimelineVisible", arranger.isTimelineVisible (), true, false, true);
        testProperty ("arranger.isIoSectionVisible", arranger.isIoSectionVisible (), false, false, true);
        testProperty ("arranger.areEffectTracksVisible", arranger.areEffectTracksVisible (), true, false, true);
    }

    // Test Mixer properties
    
    if (TEST_MIXER)
    {
        var mixer = host.createMixer ();
        assertNotNull ("Mixer not created.", mixer);
        
        testProperty ("mixer.isClipLauncherSectionVisible", mixer.isClipLauncherSectionVisible (), true, false, true);
        testProperty ("mixer.isCrossFadeSectionVisible", mixer.isCrossFadeSectionVisible (), false, false, true);
        testProperty ("mixer.isDeviceSectionVisible", mixer.isDeviceSectionVisible (), true, false, true);
        testProperty ("mixer.isIoSectionVisible", mixer.isIoSectionVisible (), true, false, true);
        testProperty ("mixer.isMeterSectionVisible", mixer.isMeterSectionVisible (), false, false, true);
        testProperty ("mixer.isSendSectionVisible", mixer.isSendSectionVisible (), true, false, true);
    }

    // Test CursorDevice properties
    
    var cursorDevice = host.createEditorCursorDevice (NUM_SENDS);
    assertNotNull ("Cursor Device not created.", cursorDevice);

    if (TEST_CURSOR_DEVICE)
    {
        testProperty ("cursorDevice.isEnabled", cursorDevice.isEnabled (), true, false, true);
        testProperty ("cursorDevice.isPlugin", cursorDevice.isPlugin (), false, false, true);
        testProperty ("cursorDevice.position", cursorDevice.position (), 0);
        testProperty ("cursorDevice.name", cursorDevice.name (), "Polysynth");
        testProperty ("cursorDevice.hasPrevious", cursorDevice.hasPrevious (), false, false, true);
        testProperty ("cursorDevice.hasNext", cursorDevice.hasNext (), false, false, true);
        testProperty ("cursorDevice.isExpanded", cursorDevice.isExpanded (), true, false, true);
        testProperty ("cursorDevice.isRemoteControlsSectionVisible", cursorDevice.isRemoteControlsSectionVisible (), false, false, true);
        testProperty ("cursorDevice.isWindowOpen", cursorDevice.isWindowOpen (), false, false, false);
        testProperty ("cursorDevice.isNested", cursorDevice.isNested (), false);
        testProperty ("cursorDevice.hasDrumPads", cursorDevice.hasDrumPads (), false);
        testProperty ("cursorDevice.hasLayers", cursorDevice.hasLayers (), false);
        testProperty ("cursorDevice.hasSlots", cursorDevice.hasSlots (), true);
    }

    if (TEST_REMOTE_CONTROLS)
    {
        var remoteControls = cursorDevice.createCursorRemoteControlsPage (NUM_PARAMS);
        assertNotNull ("Remote controls not created.", remoteControls);
        
        testProperty ("remoteControls.hasPrevious", remoteControls.hasPrevious (), false);
        testProperty ("remoteControls.hasNext", remoteControls.hasNext (), true);
        testProperty ("remoteControls.selectedPageIndex", remoteControls.selectedPageIndex (), 0, 0, 8, 5);
        testProperty ("remoteControls.pageNames", remoteControls.pageNames ());
        for (var i = 0; i < NUM_PARAMS; i++)
        {
            var p = remoteControls.getParameter (i);
            var PV = PARAMETER_VALUES[i];
            testProperty ("p.exists (" + i + ")", p.exists (), PV.exists, true);
            testProperty ("p.name (" + i + ")", p.name (), PV.name, "Parameter Name 1", "Parameter Name 2", "Parameter Name 3");
            testProperty ("p.value (" + i + ")", p.value (), PV.value, 0, 1, 0.6);
            testProperty ("p.modulatedValue (" + i + ")", p.modulatedValue (), PV.modulatedValue);
            testProperty ("p.displayedValue (" + i + ")", p.displayedValue (), PV.displayedValue);
        }
    }

    if (TEST_DEVICE_SIBLINGS)
    {
        var siblings = cursorDevice.createSiblingsDeviceBank (NUM_DEVICES_IN_BANK);
        assertNotNull ("Siblings device bank not created.", siblings);
    
        for (var i = 0; i < NUM_DEVICES_IN_BANK; i++)
            testProperty ("siblings.getDevice (" + i + ").name", siblings.getDevice (i).name (), SIBLINGS_VALUES[i].name);
    }
    
    println ("----------------------------------------------------------------------");
    
    delay (testProperties);
}

function exit()
{
    println ("Exit.");
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
            // Test if toggling a boolean value works
            scheduleFunction (function (propertyObject) { propertyObject.property.toggle (); }, [ propertyObject ]);
            scheduleFunction (assertEqualsProperty, [ "Toggled", !value, propertyObject ]);
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
            // Test if toggling a boolean value works
            scheduleFunction (function (propertyObject) { propertyObject.property.toggle (); }, [ propertyObject ]);
            // false is retrieved from getter when off
            scheduleFunction (assertEqualsProperty, [ "(Off) Toggled", false, propertyObject ]);
        }
        else
        {
            // Test setting to a test value
            scheduleFunction (function (propertyObject, value) { propertyObject.property.set (value); }, [ propertyObject, propertyObject.testValue ]);
            // -1 or '' is retrieved from getter when off
            scheduleFunction (assertEqualsProperty, [ "(Off) Test", typeof (value) == "string" ? '' : -1, propertyObject ]);
        }

        // Turn on value updates
        scheduleFunction (function (property) { property.setIsSubscribed (true); }, [ propertyObject.property ]);

        // Now the value update must fire!
        if (propertyObject.property.toggle)
            scheduleFunction (assertEqualsProperty, [ "(On) Toggled", !value, propertyObject ]);
        else
            scheduleFunction (assertEqualsProperty, [ "(On) Test", propertyObject.testValue, propertyObject ]);
        
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