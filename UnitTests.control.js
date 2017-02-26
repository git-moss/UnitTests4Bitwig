// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

loadAPI(2);
load ("MultiResult.js");
load ("Logger.js");
load ("TestFramework.js");

host.defineController ("Moss", "Unit Tests", "1.0", "423793DD-89BA-49DC-9E6E-5C61FDA19E85", "Jürgen Moßgraber");

const TEST_PROPERTY_GETTER   = true;
const TEST_PROPERTY_OBSERVER = true;

const TEST_APPLICATION       = true;
const TEST_ARRANGER          = true;
const TEST_MIXER             = true;
const TEST_TRANSPORT         = true;
const TEST_CURSOR_DEVICE     = true;
const TEST_REMOTE_CONTROLS   = true;
const TEST_DEVICE_SIBLINGS   = true;

const NUM_SENDS           = 2;
const NUM_PARAMS          = 2;
const NUM_DEVICES_IN_BANK = 2;

const ANSWER_DELAY = 100;

const PARAMETER_VALUES = [ 
    { exists: true, name: "Pitch", value: 0.5, modulatedValue: 0.5, displayedValue: "+0.000 st" },
    { exists: true, name: "Shape", value: 0.5, modulatedValue: 0.5, displayedValue: "0.000 %" } ];
const SIBLINGS_VALUES = [ { name: "Polysynth" }, { name: "" } ]

var scheduler = [];


function init ()
{
    LOG.infoLine ();
    
    // Test Application properties
    
    if (TEST_APPLICATION)
    {
        var application = host.createApplication ();
        assertNotNull ("Application not created.", application);
        
        testBooleanProperty ("application.hasActiveEngine", application.hasActiveEngine ());
        testProperty ("application.projectName", application.projectName (), "UnitTestsProject");
        testProperty ("application.panelLayout", application.panelLayout (), new MultiResult ([ "ARRANGE", "MIX", "EDIT", "PLAY" ]));
        testProperty ("application.displayProfile", application.displayProfile (), new MultiResult ([ "Single Display (Small)", "Single Display (Large)", "Dual Display (Studio)", "Dual Display (Arranger/Mixer)", "Dual Display (Master/Detail)", "Triple Display", "Tablet" ]));
    }
    
    // Test Arranger properties
    
    if (TEST_ARRANGER)
    {
        var arranger = host.createArranger ();
        testSettableBooleanProperty ("arranger.areCueMarkersVisible", arranger.areCueMarkersVisible ());
        testSettableBooleanProperty ("arranger.isPlaybackFollowEnabled", arranger.isPlaybackFollowEnabled ());
        testSettableBooleanProperty ("arranger.hasDoubleRowTrackHeight", arranger.hasDoubleRowTrackHeight ());
        testSettableBooleanProperty ("arranger.isClipLauncherVisible", arranger.isClipLauncherVisible ());
        testSettableBooleanProperty ("arranger.isTimelineVisible", arranger.isTimelineVisible ());
        testSettableBooleanProperty ("arranger.isIoSectionVisible", arranger.isIoSectionVisible ());
        testSettableBooleanProperty ("arranger.areEffectTracksVisible", arranger.areEffectTracksVisible ());
    }

    // Test Mixer properties
    
    if (TEST_MIXER)
    {
        var mixer = host.createMixer ();
        assertNotNull ("Mixer not created.", mixer);
        
        testSettableBooleanProperty ("mixer.isClipLauncherSectionVisible", mixer.isClipLauncherSectionVisible ());
        testSettableBooleanProperty ("mixer.isCrossFadeSectionVisible", mixer.isCrossFadeSectionVisible ());
        testSettableBooleanProperty ("mixer.isDeviceSectionVisible", mixer.isDeviceSectionVisible ());
        testSettableBooleanProperty ("mixer.isIoSectionVisible", mixer.isIoSectionVisible ());
        testSettableBooleanProperty ("mixer.isMeterSectionVisible", mixer.isMeterSectionVisible ());
        testSettableBooleanProperty ("mixer.isSendSectionVisible", mixer.isSendSectionVisible ());
    }
    
    if (TEST_TRANSPORT)
    {
        var transport = host.createTransport ();
        assertNotNull ("Transport not created.", transport);
        
        testSettableBooleanProperty ("transport.isPlaying", transport.isPlaying ());
        testSettableBooleanProperty ("transport.isArrangerRecordEnabled", transport.isArrangerRecordEnabled ());
        testSettableBooleanProperty ("transport.isArrangerOverdubEnabled", transport.isArrangerOverdubEnabled ());
        testSettableBooleanProperty ("transport.isClipLauncherOverdubEnabled", transport.isClipLauncherOverdubEnabled ());
        testEnumProperty ("transport.automationWriteMode", transport.automationWriteMode (), new MultiResult ([ "latch", "touch", "write" ]), "latch", "touch", "write");
        testSettableBooleanProperty ("transport.isArrangerAutomationWriteEnabled", transport.isArrangerAutomationWriteEnabled ());
        testSettableBooleanProperty ("transport.isClipLauncherAutomationWriteEnabled", transport.isClipLauncherAutomationWriteEnabled ());
        testBooleanProperty ("transport.isAutomationOverrideActive", transport.isAutomationOverrideActive ());
        testSettableBooleanProperty ("transport.isArrangerLoopEnabled", transport.isArrangerLoopEnabled ());
        testSettableBooleanProperty ("transport.isPunchInEnabled", transport.isPunchInEnabled ());
        testSettableBooleanProperty ("transport.isPunchOutEnabled", transport.isPunchOutEnabled ());
        testSettableBooleanProperty ("transport.isMetronomeEnabled", transport.isMetronomeEnabled ());
        testSettableBooleanProperty ("transport.isMetronomeTickPlaybackEnabled", transport.isMetronomeTickPlaybackEnabled ());
    }

    // Test CursorDevice properties
    
    if (TEST_CURSOR_DEVICE || TEST_REMOTE_CONTROLS || TEST_DEVICE_SIBLINGS)
    {
        var cursorDevice = host.createEditorCursorDevice (NUM_SENDS);
        assertNotNull ("Cursor Device not created.", cursorDevice);

        if (TEST_CURSOR_DEVICE)
        {
            testSettableBooleanProperty ("cursorDevice.isEnabled", cursorDevice.isEnabled ());
            testBooleanProperty ("cursorDevice.isPlugin", cursorDevice.isPlugin (), false, false, true);
            testIntegerProperty ("cursorDevice.position", cursorDevice.position (), 0);
            testStringProperty ("cursorDevice.name", cursorDevice.name (), "Polysynth");
            testBooleanProperty ("cursorDevice.hasPrevious", cursorDevice.hasPrevious ());
            testBooleanProperty ("cursorDevice.hasNext", cursorDevice.hasNext ());
            testSettableBooleanProperty ("cursorDevice.isExpanded", cursorDevice.isExpanded ());
            testSettableBooleanProperty ("cursorDevice.isRemoteControlsSectionVisible", cursorDevice.isRemoteControlsSectionVisible ());
            testSettableBooleanProperty ("cursorDevice.isWindowOpen", cursorDevice.isWindowOpen (), false, false, false);
            testBooleanProperty ("cursorDevice.isNested", cursorDevice.isNested (), false);
            testBooleanProperty ("cursorDevice.hasDrumPads", cursorDevice.hasDrumPads (), false);
            testBooleanProperty ("cursorDevice.hasLayers", cursorDevice.hasLayers (), false);
            testBooleanProperty ("cursorDevice.hasSlots", cursorDevice.hasSlots (), true);
        }

        if (TEST_REMOTE_CONTROLS)
        {
            var remoteControls = cursorDevice.createCursorRemoteControlsPage (NUM_PARAMS);
            assertNotNull ("Remote controls not created.", remoteControls);
            
            testBooleanProperty ("remoteControls.hasPrevious", remoteControls.hasPrevious ());
            testBooleanProperty ("remoteControls.hasNext", remoteControls.hasNext ());
            testIntegerProperty ("remoteControls.selectedPageIndex", remoteControls.selectedPageIndex (), 0, 0, 8, 5);
            testProperty ("remoteControls.pageNames", remoteControls.pageNames ());
            for (var i = 0; i < NUM_PARAMS; i++)
            {
                var p = remoteControls.getParameter (i);
                var PV = PARAMETER_VALUES[i];
                testBooleanProperty ("p.exists (" + i + ")", p.exists (), PV.exists, true);
                testStringProperty ("p.name (" + i + ")", p.name (), PV.name, "Parameter Name 1", "Parameter Name 2", "Parameter Name 3");
                testFloatProperty ("p.value (" + i + ")", p.value (), PV.value, 0, 1, 0.6);
                testFloatProperty ("p.modulatedValue (" + i + ")", p.modulatedValue (), PV.modulatedValue);
                testStringProperty ("p.displayedValue (" + i + ")", p.displayedValue (), PV.displayedValue);
            }
        }

        if (TEST_DEVICE_SIBLINGS)
        {
            var siblings = cursorDevice.createSiblingsDeviceBank (NUM_DEVICES_IN_BANK);
            assertNotNull ("Siblings device bank not created.", siblings);
        
            for (var i = 0; i < NUM_DEVICES_IN_BANK; i++)
                testStringProperty ("siblings.getDevice (" + i + ").name", siblings.getDevice (i).name (), SIBLINGS_VALUES[i].name);
        }
    }
    
    LOG.infoLine ();
    
    delay (executeScheduler);
}

function exit()
{
    println ("Exit.");
}
