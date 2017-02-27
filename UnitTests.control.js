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

// Deactivate run all ...
const TEST_RUN_ALL           = true;

// ... and activate individual tests for faster bug testing!
const TEST_APPLICATION       = false;
const TEST_ARRANGER          = false;
const TEST_MIXER             = false;
const TEST_TRANSPORT         = false;
const TEST_GROOVE            = false;
const TEST_SCENE             = false;
const TEST_CURSOR_CLIP       = false;
const TEST_CURSOR_DEVICE     = false;
const TEST_REMOTE_CONTROLS   = false;
const TEST_DEVICE_SIBLINGS   = false;

const NUM_SENDS           = 2;
const NUM_PARAMS          = 2;
const NUM_DEVICES_IN_BANK = 2;

const ANSWER_DELAY = 100;

var scheduler = [];


function init ()
{
    LOG.infoLine ();
    
    // Test Application properties
    if (TEST_RUN_ALL || TEST_APPLICATION)
    {
        var application = host.createApplication ();
        assertNotNull ("Application not created.", application);
        
        testBooleanValue ("application.hasActiveEngine", application.hasActiveEngine ());
        testValue ("application.projectName", application.projectName (), "UnitTestsProject");
        testValue ("application.panelLayout", application.panelLayout (), new MultiResult ([ "ARRANGE", "MIX", "EDIT", "PLAY" ]));
        testValue ("application.displayProfile", application.displayProfile (), new MultiResult ([ "Single Display (Small)", "Single Display (Large)", "Dual Display (Studio)", "Dual Display (Arranger/Mixer)", "Dual Display (Master/Detail)", "Triple Display", "Tablet" ]));
    }
    
    // Test Arranger properties
    if (TEST_RUN_ALL || TEST_ARRANGER)
    {
        var arranger = host.createArranger ();
        testSettableBooleanValue ("arranger.areCueMarkersVisible", arranger.areCueMarkersVisible ());
        testSettableBooleanValue ("arranger.isPlaybackFollowEnabled", arranger.isPlaybackFollowEnabled ());
        testSettableBooleanValue ("arranger.hasDoubleRowTrackHeight", arranger.hasDoubleRowTrackHeight ());
        testSettableBooleanValue ("arranger.isClipLauncherVisible", arranger.isClipLauncherVisible ());
        testSettableBooleanValue ("arranger.isTimelineVisible", arranger.isTimelineVisible ());
        testSettableBooleanValue ("arranger.isIoSectionVisible", arranger.isIoSectionVisible ());
        testSettableBooleanValue ("arranger.areEffectTracksVisible", arranger.areEffectTracksVisible ());
    }

    // Test Mixer properties
    if (TEST_RUN_ALL || TEST_MIXER)
    {
        var mixer = host.createMixer ();
        assertNotNull ("Mixer not created.", mixer);
        
        testSettableBooleanValue ("mixer.isClipLauncherSectionVisible", mixer.isClipLauncherSectionVisible ());
        testSettableBooleanValue ("mixer.isCrossFadeSectionVisible", mixer.isCrossFadeSectionVisible ());
        testSettableBooleanValue ("mixer.isDeviceSectionVisible", mixer.isDeviceSectionVisible ());
        testSettableBooleanValue ("mixer.isIoSectionVisible", mixer.isIoSectionVisible ());
        testSettableBooleanValue ("mixer.isMeterSectionVisible", mixer.isMeterSectionVisible ());
        testSettableBooleanValue ("mixer.isSendSectionVisible", mixer.isSendSectionVisible ());
    }
    
    // Test Transport properties
    if (TEST_RUN_ALL || TEST_TRANSPORT)
    {
        var transport = host.createTransport ();
        assertNotNull ("Transport not created.", transport);
        
        testSettableBooleanValue ("transport.isPlaying", transport.isPlaying ());
        testSettableBooleanValue ("transport.isArrangerRecordEnabled", transport.isArrangerRecordEnabled ());
        testSettableBooleanValue ("transport.isArrangerOverdubEnabled", transport.isArrangerOverdubEnabled ());
        testSettableBooleanValue ("transport.isClipLauncherOverdubEnabled", transport.isClipLauncherOverdubEnabled ());
        testEnumValue ("transport.automationWriteMode", transport.automationWriteMode (), new MultiResult ([ "latch", "touch", "write" ]), "latch", "touch", "write");
        testSettableBooleanValue ("transport.isArrangerAutomationWriteEnabled", transport.isArrangerAutomationWriteEnabled ());
        testSettableBooleanValue ("transport.isClipLauncherAutomationWriteEnabled", transport.isClipLauncherAutomationWriteEnabled ());
        testBooleanValue ("transport.isAutomationOverrideActive", transport.isAutomationOverrideActive ());
        testSettableBooleanValue ("transport.isArrangerLoopEnabled", transport.isArrangerLoopEnabled ());
        testSettableBooleanValue ("transport.isPunchInEnabled", transport.isPunchInEnabled ());
        testSettableBooleanValue ("transport.isPunchOutEnabled", transport.isPunchOutEnabled ());
        testSettableBooleanValue ("transport.isMetronomeEnabled", transport.isMetronomeEnabled ());
        testSettableBooleanValue ("transport.isMetronomeTickPlaybackEnabled", transport.isMetronomeTickPlaybackEnabled ());
        testSettableRangedValue ("transport.metronomeVolume", transport.metronomeVolume (), 0.75, 0, 1, 0.6, "-12.0 dB");
        testSettableBooleanValue ("transport.isMetronomeAudibleDuringPreRoll", transport.isMetronomeAudibleDuringPreRoll ());
        testEnumValue ("transport.preRoll", transport.preRoll (), new MultiResult ([ "none", "one_bar", "two_bars", "four_bars" ]), "none", "one_bar", "four_bars");
        testParameter ("transport.tempo", transport.tempo (), 0.1393188854489164, 0, 1, 0.1393188854489164, "110.000 BPM", "");
        testSettableBeatTimeValue ("transport.getPosition", transport.getPosition (), 0, 0, 1, 10, "001:01:01:00");
        testSettableBeatTimeValue ("transport.getInPosition", transport.getInPosition (), 0, 0, 1, 10, "001:01:01:00");
        testSettableBeatTimeValue ("transport.getOutPosition", transport.getOutPosition (), 4, 1, 2, 10, "002:01:01:00");
        testParameter ("transport.getCrossfade", transport.getCrossfade (), 0.5, 0, 1, 0.75, "0.000 %", "");

        var timeSignature = transport.getTimeSignature ();
        testTimeSignature ("transport.getTimeSignature", timeSignature, "4/4", "3/4", "5/8", "15/16");
        testIntegerValue ("transport.timeSignature.getNumerator", timeSignature.getNumerator (), 4, 2, 3, 4);
        testIntegerValue ("transport.timeSignature.getDenominator", timeSignature.getDenominator (), 4, 8, 16, 4);
        testIntegerValue ("transport.timeSignature.getTicks", timeSignature.getTicks (), 16, 0, 32, 10);
        
        testEnumValue ("transport.clipLauncherPostRecordingAction", transport.clipLauncherPostRecordingAction (), new MultiResult ([ "off", "play_recorded", "record_next_free_slot", "stop", "return_to_arrangement", "return_to_previous_clip", "play_random" ]), "off", "play_recorded", "play_random"); 
        testSettableBeatTimeValue ("transport.getClipLauncherPostRecordingTimeOffset", transport.getClipLauncherPostRecordingTimeOffset (), 4, 0, 1, 10, "001:00:00:00");
    }
    
    // Test Groove properties
    if (TEST_RUN_ALL || TEST_GROOVE)
    {
        var groove = host.createGroove ();
        assertNotNull ("Groove not created.", groove);
        
        testParameter ("groove.getEnabled", groove.getEnabled (), 0, 0, 1, 1, "off", "Enabled");
        testParameter ("groove.getShuffleAmount", groove.getShuffleAmount (), 0.5, 0, 1, 0.6, "50.000 %", "Shuffle");
        testParameter ("groove.getShuffleRate", groove.getShuffleRate (), 1, 0, 1, 1, "1/16", "Shuffle Rate");
        testParameter ("groove.getAccentAmount", groove.getAccentAmount (), 1, 0, 1, 1, "100.000 %", "Accent");
        testParameter ("groove.getAccentRate", groove.getAccentRate (), 0, 0, 1, 1, "1/4", "Accent Rate");
        testParameter ("groove.getAccentPhase", groove.getAccentPhase (), 0.5, 0, 1, 1, "0.000 %", "Accent Phase");
    }    

    // Test Scene properties
    if (TEST_RUN_ALL || TEST_SCENE)
    {
        var numScenes = 3;
        var sceneBank = host.createSceneBank (numScenes);
        assertNotNull ("Scene Bank not created.", sceneBank);
        
        var sceneValues = [ 
            { exists: true, sceneIndex: 0, name: "1st Scene", clipCount: 1 },
            { exists: true, sceneIndex: 1, name: "2nd Scene", clipCount: 2 },
            { exists: false, sceneIndex: 2, name: "", clipCount: 0 }
        ];
        
        for (var i = 0; i < numScenes; i++)
        {
            var scene = sceneBank.getScene (i);
            assertNotNull ("Scene " + i + " not created.", scene);
            
            testStringValue ("scene.exists", scene.exists (), sceneValues[i].exists);
            testStringValue ("scene.sceneIndex", scene.sceneIndex (), sceneValues[i].sceneIndex);
            testStringValue ("scene.name", scene.name (), sceneValues[i].name);
            testStringValue ("scene.clipCount", scene.clipCount (), sceneValues[i].clipCount);
        }
    }
    
    // Test Scene properties
    if (TEST_RUN_ALL || TEST_CURSOR_CLIP)
    {
        var clip = host.createCursorClip (8, 8);
        assertNotNull ("Clip not created.", clip);
        
        // Make sure that the first slot is selected for testing
        var trackBank = host.createTrackBank (2, 0, 3);
        assertNotNull ("TrackBank not created.", trackBank);
        scheduleFunction (function (trackBank) { 
            var slotBank = trackBank.getChannel (0).clipLauncherSlotBank ();
            slotBank.select (0);
            slotBank.showInEditor (0);
        }, [ trackBank ]);
        
        testBooleanValue ("clip.canScrollKeysUp", clip.canScrollKeysUp (), false);
        testBooleanValue ("clip.canScrollKeysDown", clip.canScrollKeysDown (), false);
        testBooleanValue ("clip.canScrollStepsBackwards", clip.canScrollStepsBackwards (), false);
        testBooleanValue ("clip.canScrollStepsForwards", clip.canScrollStepsForwards (), false);
        testIntegerValue ("clip.playingStep ()", clip.playingStep (), -1);
        testSettableBooleanValue ("clip.getShuffle", clip.getShuffle (), false);
        testSettableRangedValue ("clip.getAccent", clip.getAccent (), 0.5, 0, 1, 0.6, "0.00 %");
        testSettableBeatTimeValue ("clip.getPlayStart", clip.getPlayStart (), 0, 0, 1, 1, "001:01:01:00");
        testSettableBeatTimeValue ("clip.getPlayStop", clip.getPlayStop (), 10, 4, 4, 4, "003:03:01:00");
        testSettableBooleanValue ("clip.isLoopEnabled", clip.isLoopEnabled (), true);
        testSettableBeatTimeValue ("clip.getLoopStart", clip.getLoopStart (), 0, 0, 1, 10, "001:01:01:00");
        testSettableBeatTimeValue ("clip.getLoopLength", clip.getLoopLength (), 4, 1, 4, 10, "001:00:00:00");
        
//        testValue ("clip.color", clip.color ());
        
        testStringValue ("clip.getTrack.name", clip.getTrack ().name (), "Polysynth");
    }
    
    // Test CursorDevice properties
    if (TEST_RUN_ALL || TEST_CURSOR_DEVICE || TEST_REMOTE_CONTROLS || TEST_DEVICE_SIBLINGS)
    {
        var cursorDevice = host.createEditorCursorDevice (NUM_SENDS);
        assertNotNull ("Cursor Device not created.", cursorDevice);

        if (TEST_CURSOR_DEVICE)
        {
            testSettableBooleanValue ("cursorDevice.isEnabled", cursorDevice.isEnabled ());
            testBooleanValue ("cursorDevice.isPlugin", cursorDevice.isPlugin (), false, false, true);
            testIntegerValue ("cursorDevice.position", cursorDevice.position (), 0);
            testStringValue ("cursorDevice.name", cursorDevice.name (), "Polysynth");
            testBooleanValue ("cursorDevice.hasPrevious", cursorDevice.hasPrevious ());
            testBooleanValue ("cursorDevice.hasNext", cursorDevice.hasNext ());
            testSettableBooleanValue ("cursorDevice.isExpanded", cursorDevice.isExpanded ());
            testSettableBooleanValue ("cursorDevice.isRemoteControlsSectionVisible", cursorDevice.isRemoteControlsSectionVisible ());
            testSettableBooleanValue ("cursorDevice.isWindowOpen", cursorDevice.isWindowOpen (), false, false, false);
            testBooleanValue ("cursorDevice.isNested", cursorDevice.isNested (), false);
            testBooleanValue ("cursorDevice.hasDrumPads", cursorDevice.hasDrumPads (), false);
            testBooleanValue ("cursorDevice.hasLayers", cursorDevice.hasLayers (), false);
            testBooleanValue ("cursorDevice.hasSlots", cursorDevice.hasSlots (), true);
        }

        if (TEST_RUN_ALL || TEST_REMOTE_CONTROLS)
        {
            var remoteControls = cursorDevice.createCursorRemoteControlsPage (NUM_PARAMS);
            assertNotNull ("Remote controls not created.", remoteControls);
            
            testBooleanValue ("remoteControls.hasPrevious", remoteControls.hasPrevious ());
            testBooleanValue ("remoteControls.hasNext", remoteControls.hasNext ());
            testIntegerValue ("remoteControls.selectedPageIndex", remoteControls.selectedPageIndex (), 0, 0, 8, 5);
            testValue ("remoteControls.pageNames", remoteControls.pageNames ());
            testParameter ("remoteControls.getParameter (0)", remoteControls.getParameter (0), 0.5, 0, 1, 0.6, "+0.000 st", "Pitch", "Parameter Name 1", "Parameter Name 2", "Parameter Name 3");
            testParameter ("remoteControls.getParameter (1)", remoteControls.getParameter (1), 0.5, 0, 1, 0.6, "0.000 %", "Shape", "Parameter Name 1", "Parameter Name 2", "Parameter Name 3");
        }

        if (TEST_RUN_ALL || TEST_DEVICE_SIBLINGS)
        {
            var siblings = cursorDevice.createSiblingsDeviceBank (NUM_DEVICES_IN_BANK);
            assertNotNull ("Siblings device bank not created.", siblings);
            
            testStringValue ("siblings.getDevice (0).name", siblings.getDevice (0).name (), "Polysynth");
            testStringValue ("siblings.getDevice (1).name", siblings.getDevice (1).name (), "");
        }
    }
    
    LOG.infoLine ();
    
    delay (executeScheduler);
}

function exit()
{
    println ("Exit.");
}
