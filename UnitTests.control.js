// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

loadAPI(2);
load ("MultiResult.js");
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
        testProperty ("application.panelLayout", application.panelLayout (), new MultiResult ([ "ARRANGE", "MIX", "EDIT", "PLAY" ]));
        testProperty ("application.displayProfile", application.displayProfile (), new MultiResult ([ "Single Display (Small)", "Single Display (Large)", "Dual Display (Studio)", "Dual Display (Arranger/Mixer)", "Dual Display (Master/Detail)", "Triple Display", "Tablet" ]));
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
    
    if (TEST_TRANSPORT)
    {
        var transport = host.createTransport ();
        assertNotNull ("Transport not created.", transport);
        
        var booleanOpts = new MultiResult ([ false, true ]);
        
        testProperty ("transport.isPlaying", transport.isPlaying (), booleanOpts, false, true);
        testProperty ("transport.isArrangerRecordEnabled", transport.isArrangerRecordEnabled (), booleanOpts, false, true);
        testProperty ("transport.isArrangerOverdubEnabled", transport.isArrangerOverdubEnabled (), booleanOpts, false, true);
        testProperty ("transport.isClipLauncherOverdubEnabled", transport.isClipLauncherOverdubEnabled (), booleanOpts, false, true);
        testProperty ("transport.automationWriteMode", transport.automationWriteMode (), new MultiResult ([ "latch", "touch", "write" ]), "latch", "touch", "write");
        testProperty ("transport.isArrangerAutomationWriteEnabled", transport.isArrangerAutomationWriteEnabled (), booleanOpts, false, true);
        testProperty ("transport.isClipLauncherAutomationWriteEnabled", transport.isClipLauncherAutomationWriteEnabled (), booleanOpts, false, true);
        testProperty ("transport.isAutomationOverrideActive", transport.isAutomationOverrideActive (), booleanOpts);
        testProperty ("transport.isArrangerLoopEnabled", transport.isArrangerLoopEnabled (), booleanOpts, false, true);
        testProperty ("transport.isPunchInEnabled", transport.isPunchInEnabled (), booleanOpts, false, true);
        testProperty ("transport.isPunchOutEnabled", transport.isPunchOutEnabled (), booleanOpts, false, true);
        testProperty ("transport.isMetronomeEnabled", transport.isMetronomeEnabled (), booleanOpts, false, true);
        testProperty ("transport.isMetronomeTickPlaybackEnabled", transport.isMetronomeTickPlaybackEnabled (), booleanOpts, false, true);
        
        // TODO
    }

    // Test CursorDevice properties
    
    if (TEST_CURSOR_DEVICE || TEST_REMOTE_CONTROLS || TEST_DEVICE_SIBLINGS)
    {
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
        
        // TODO Test layers and drum pads
    }
    
    println ("----------------------------------------------------------------------");
    
    delay (testProperties);
}

function exit()
{
    println ("Exit.");
}
