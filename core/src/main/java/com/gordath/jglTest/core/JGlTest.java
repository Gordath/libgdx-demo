package com.gordath.jglTest.core;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g3d.*;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Matrix4;

import static com.badlogic.gdx.Gdx.gl;
import static com.badlogic.gdx.Gdx.graphics;
import static com.badlogic.gdx.graphics.GL30.GL_COLOR_BUFFER_BIT;
import static com.badlogic.gdx.graphics.GL30.GL_DEPTH_BUFFER_BIT;
import static com.badlogic.gdx.graphics.VertexAttributes.Usage.Normal;
import static com.badlogic.gdx.graphics.VertexAttributes.Usage.Position;
import static com.badlogic.gdx.graphics.VertexAttributes.Usage.TextureCoordinates;

public class JGlTest implements ApplicationListener {

    /**
     * The "camera" (viewpoint) in our application.
     * It is nothing more than a 4x4 transformation matrix that is given a fancy name :)
     *
     * Though it implements some useful methods like project() and unproject() and getPickRay()
     * which we are going to need in order to select objects in the 3d world.
     */
    Camera perspectiveCamera;

    /**
     * This represents a 3 dimensional model. The model consists of nodes, meshes, animations, materials and more.
     * The nodes have their own meshes(geometry) and each one has it's own transformation matrix.
     * Animations affect the node transformation matrices over time. (And thus motion happens!).
     * The Model class owns all its components.
     */
    Model cube;

    /**
     * The model instance is responsible for handling transformations that position the model inside the world (nothing to do
     * with the Node transformations of the the model or the Animations).
     *
     * The ModelInstance does not own any resources of the Model so we can have multiple instances of the same model and we
     * can render them with different transformations and effects without affecting the original model.
     */
    ModelInstance cubeInstance;

    /**
     * The ModeBatch groups up our model instances and handles their rendering.
     */
    ModelBatch modelBatch;

    /**
     * This class is used in order to build a model using code instead of loading it from an external file.
     * We are using this to create a cube through code.
     */
    ModelBuilder modelBuilder;

    /**
     * The material that will get applied to the model.
     * A material normally consists of:
     * A diffuse color. 4 dimensional vector (rgba).
     * A specular color. 4 dimensional vector (rgba).
     * An ambient color. 4 dimensional vector (rgba).
     *
     * Any amount of textures.
     * Usually a diffuse texture, a specular texture and a normal map texture or bump map texture (these 2 are used
     * create the illusion of depth on a certain parts of a geometric surface).
     */
    Material material;

    /**
     * The Environment handles the lighting of the scene.
     */
    Environment environment;

    /**
     * The elapsed time since the application started running.
     */
	float elapsed;

    /**
     * This method is called once when the application starts. Think of it as the program's initialization function.
     */
	@Override
	public void create () {

        /**
         * We define an material attribute. In this case we define the diffuse color
         * that our model will have.
         */
        Attribute colorAttribute = new ColorAttribute(ColorAttribute.Diffuse, Color.RED);

        /**
         * Lets create the material!
         */
        material = new Material(colorAttribute);

        /**
         * Set the color attribute of the Ambient lighting of the scene.
         * We also add a directional light with the 3 1st values defining the light intensity and
         * the 3 last values defining the direction of the light.
         */
        environment = new Environment();
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.25f, 0.25f, 0.25f, 1f));
        environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));


        /**
         * Allocate the ModelBuilder!
         */
        modelBuilder = new ModelBuilder();

        /**
         * Create or cube model using the createBox() utility method of ModelBuilder.
         */
        cube = modelBuilder.createBox(1f, 1f, 1f, material, Position | Normal | TextureCoordinates);

        /**
         * Allocate an instance of our model so we can render in on screen and apply transformations on it.
         */
        cubeInstance = new ModelInstance(cube);

        /**
         * Allocate our ModelBatch that will handle the rendering of the ModelInstances and also handle the
         * camera transformations.
         */
        modelBatch = new ModelBatch();
	}

    /**
     * This method gets called only when the window changes size.
     * It is responsible to recalculate the projection and other transformations of the camera.
     * Sadly we have to allocate it again...every time the window changes size...
     * @param width The width of the viewport (X axis).
     * @param height The height of the viewport (Y axis).
     */
	@Override
	public void resize (int width, int height) {

        /**
         * Create a new perspective camera with the new width and height values after the window has changed size.
         */
        perspectiveCamera = new PerspectiveCamera(45.0f, width, height);

        /**
         * Apply a translation to to the camera. This function creates a 3 dimensional vector and multiplies it with
         * the cameras transformation matrix (cameraMatrix * translationVector).
         *
         * Then the lookAt transformation is applied to the camera's transformation matrix. What it does is, it points the
         * view point of the camera to the specified x,y,z coordinates in the 3 dimensional world.
         *
         * After that the near and far clipping planes are set. The perspective view frustum in now complete and our geometry
         * should be correctly projected to the 2 dimensional surface of the screen.
         */
        perspectiveCamera.translate(3, 3, 3);
        perspectiveCamera.lookAt(0, 0, 0);
        perspectiveCamera.near = 1f;
        perspectiveCamera.far = 300f;
	}

    /**
     * This method is call on every frame and is responsible for drawing stuff to the screen.
     */
    @Override
    public void render () {
        /**
         * Here we instruct the camera to recalculate it's transformation matrix each frame by using update()
         */
        perspectiveCamera.update();

        /**
         * The elapsed time in seconds.
         */
        elapsed += graphics.getDeltaTime();

        /**
         * We set the clear color of the framebuffer to a grey shade so we can detect rendered
         * geometry even if it has no material (only geometry data = black shape).
         * Then we clear the color and depth buffers of OpenGL so we can re-draw on them.
         *
         * Notice: GL_COLOR_BUFFER_BIT and GL_DEPTH_BUFFER_BIT are bitmask values so we can combine them using
         * the bitwise OR operator '|'
         */
        gl.glClearColor(0.3f, 0.3f, 0.3f, 1.0f);
		gl.glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        /**
         * We create a 4x4 matrix in order to store our transformations for the cube.
         * In this case we want to rotate the cube in the x and y axis at the same time.
         * We use the elapsed time in seconds multiplied by a custom value as the angle of rotation.
         */
        Matrix4 xform = new Matrix4();
        xform.rotate(1, 1, 0, elapsed * 30f);

        /**
         * We set the transformation matrix we just calculated for this frame to the ModelInstance of the cube.
         */
        cubeInstance.transform = xform;

        /**
         * We use the model batch to initiate the ModelInstance rendering.
         */
        modelBatch.begin(perspectiveCamera);

        /**
         * The actual render call.
         */
        modelBatch.render(cubeInstance, environment);

        /**
         * We call end() to finalize the rendering and let OpenGL do it's magic.
         */
        modelBatch.end();
	}

	@Override
	public void pause () {
	}

	@Override
	public void resume () {
	}

	@Override
	public void dispose () {
        /**
         * Every good programmer has to keep his house clean...even in Java.
         */
        cube.dispose();
        modelBatch.dispose();
	}
}
