// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2019-2020
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.module;

import de.mossgrabers.TestFramework;

import com.bitwig.extension.controller.api.ControllerHost;
import com.bitwig.extension.controller.api.Scene;
import com.bitwig.extension.controller.api.SceneBank;


/**
 * Module tests for the Scene class.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class SceneModule extends TestModule
{
    private static final SceneVals [] SCENE_VALUES =
    {
        new SceneVals (true, 0, "1st Scene", 1),
        new SceneVals (true, 1, "2nd Scene", 1),
        new SceneVals (false, 2, "", 0)
    };


    /**
     * Constructor.
     */
    public SceneModule ()
    {
        super ("Scene");
    }


    /** {@inheritDoc} */
    @Override
    public boolean registerTests (final TestFramework tf, final ControllerHost host)
    {
        if (!super.registerTests (tf, host))
            return false;

        final int numScenes = 3;
        final SceneBank sceneBank = host.createSceneBank (numScenes);
        tf.assertNotNull ("Scene Bank not created.", sceneBank);

        for (int i = 0; i < numScenes; i++)
        {
            final Scene scene = sceneBank.getScene (i);
            tf.assertNotNull ("Scene " + i + " not created.", scene);

            tf.testBooleanValue ("scene.exists", scene.exists (), Boolean.valueOf (SCENE_VALUES[i].exists));
            tf.testIntegerValue ("scene.sceneIndex", scene.sceneIndex (), Integer.valueOf (SCENE_VALUES[i].sceneIndex));
            tf.testStringValue ("scene.name", scene.name (), SCENE_VALUES[i].name);
            tf.testIntegerValue ("scene.clipCount", scene.clipCount (), Integer.valueOf (SCENE_VALUES[i].clipCount));
            tf.testColorValue ("scene.color", scene.color (), Double.valueOf (0.0), Double.valueOf (0.6000000238418579), Double.valueOf (0.8509804010391235));
        }

        return true;
    }


    private static class SceneVals
    {
        boolean exists;
        int     sceneIndex;
        String  name;
        int     clipCount;


        SceneVals (final boolean exists, final int sceneIndex, final String name, final int clipCount)
        {
            this.exists = exists;
            this.sceneIndex = sceneIndex;
            this.name = name;
            this.clipCount = clipCount;
        }
    }
}
