import java.applet.Applet;
import java.awt.event.*;
import com.sun.j3d.utils.applet.MainFrame;
import com.sun.j3d.utils.geometry.Sphere;
import com.sun.j3d.utils.geometry.*;
import com.sun.j3d.utils.universe.*;
import javax.media.j3d.*;
import javax.vecmath.*;
import java.io.*;
import java.awt.*;
import com.sun.j3d.utils.behaviors.mouse.*;
import com.sun.j3d.utils.behaviors.keyboard.*;
import javax.swing.JApplet;
import com.sun.j3d.utils.image.TextureLoader;
import java.net.*;

public class SolarSystem extends JApplet {

	// create the bounds of the universe
	BoundingSphere bounds = new BoundingSphere(new Point3d(0.0, 0.0, 0.0), 1000.0);
	BoundingLeaf boundingLeaf = new BoundingLeaf(bounds);
	PlatformGeometry platformGeom = new PlatformGeometry();

	// creating the (single) branch group
	BranchGroup main = new BranchGroup();

	int primflags = Primitive.GENERATE_NORMALS + Primitive.GENERATE_TEXTURE_COORDS;

	public SolarSystem() {
		// create a content pane
		Container container = getContentPane();
		container.setLayout(new BorderLayout());
		Canvas3D c = new Canvas3D(SimpleUniverse.getPreferredConfiguration());
		container.add("Center", c);

		BranchGroup universe = createSceneGraph();
		SimpleUniverse uni2 = new SimpleUniverse(c);

		uni2.getViewingPlatform().setNominalViewingTransform();
		uni2.addBranchGraph(universe);
		//able to view whole universe
		uni2.getViewer().getView().setBackClipDistance(500);

		// *** create a viewing platform
		TransformGroup cameraTG = uni2.getViewingPlatform().getViewPlatformTransform();
		// starting postion of the viewing platform
		Vector3f translate = new Vector3f();
		Transform3D T3D = new Transform3D();
		// move along z axis by 10.0f ("move away from the screen")
		translate.set(0.0f, 0.0f, 100.0f);
		T3D.setTranslation(translate);
		cameraTG.setTransform(T3D);

	}

	public BranchGroup createSceneGraph() {
	
		
		// creating the transform group for the (one) branchgroup
		//also the main transform group
		TransformGroup ss = new TransformGroup();
		
		
		ss.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);

		PointLight light = new PointLight();
		light.setColor(new Color3f(Color.WHITE));
		light.setPosition(0.0f, 0.0f, 0.0f);
		
		Color3f amb = new Color3f(0.5f,0.5f,0.5f);
		AmbientLight a = new AmbientLight(amb);
		a.setInfluencingBounds(bounds);
		
		
		//create texture for bg
		TextureLoader tl = new TextureLoader("space2.jpg", this);
		tl.getTexture();
		Texture t = tl.getTexture();
		 
		Appearance bg1 = new Appearance();
		bg1.setTexture(t);
		
	
		//create inverted sphere for bg
		Sphere sphere = new Sphere(100f, Primitive.GENERATE_TEXTURE_COORDS | Primitive.GENERATE_NORMALS_INWARD,1000,bg1);
		sphere.setCollidable(false);
		
		
		// control intensity
		// light.setAttenuation(0.0f,0.0f,0.0f);
		light.setInfluencingBounds(bounds);
		//adding all the elements to the main transform group
		ss.addChild(light);
		ss.addChild(sphere);
		ss.addChild(a);
		//float radius, float distance, long orbit, int rotate, String texture
		//adding all the planets to the main transform group
		ss.addChild(Planet(4f, 0f, 90000, 2500000, "sunt1.png"));
		ss.addChild(Planet(0.38f,11f,8790,587000,"mercury.jpg"));
		ss.addChild(Planet(0.95f,15.75f,2430,243000,"venus.jpg"));
		ss.addChild(Planet(1.0f,20.5f,3650,1000,"earth.jpg"));
		ss.addChild(Planet(0.53f,25.6f,68698,2010,"mars.jpg"));
		ss.addChild(Planet(2.12f,30.0f,401500,26000,"jupiter.jpg"));
		ss.addChild(Planet(1.5f,35.5f,105850,3500,"saturn.jpg"));
		ss.addChild(Planet(0.8f,50.5f,30660,7000,"uranus.png"));
		ss.addChild(Planet(0.8f,78.75f,598600,8500,"neptune.jpg"));
	

		
		
		//rocket body
		Cylinder cylinder = new Cylinder(2f, 3f);
		Transform3D rocketbody = new Transform3D();
		rocketbody.setTranslation(new Vector3f(-11.0f,-1.2f,0.0f));
		TransformGroup rocketBody = new TransformGroup(rocketbody);
		rocketBody.addChild(cylinder);
		
		//rocket nozzle
		Cone nozzle = new Cone(04f,8f);
		Transform3D cR = new Transform3D();
		cR.setTranslation(new Vector3f(-11.0f,0.0f,0.0f));
		TransformGroup rocketHead = new TransformGroup(cR);
		rocketHead.addChild(nozzle);
		
		//positioning of rocket
		TransformGroup circle = new TransformGroup();
		circle.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		Transform3D yAxis1 = new Transform3D();
		yAxis1.rotX(Math.PI/2);
		
		
		//rocket movement
		Alpha alpha = new Alpha(1,10000);
		RotationInterpolator rot = new RotationInterpolator(alpha, circle, yAxis1, 0.0f,(float) Math.PI*(-2.0f));
		rot.setSchedulingBounds(bounds);
		
		/*COLLISION*/
		
		nozzle.setCollisionBounds(new BoundingBox(new Point3d(0.0, 0.0, 0.0), new Point3d(0.0, 0.0, 0.0)));
		nozzle.setCollidable(true);
		
		Alpha[] cAlpha = new Alpha[1];
		cAlpha[0] = alpha;
		CollisionBehaviour2 cb = new CollisionBehaviour2(nozzle, cAlpha, bounds);
		ss.addChild(cb);
		
		/*END COLLISON*/
		
		ss.addChild(rot);
		ss.addChild(circle);
		circle.addChild(rocketHead);
		circle.addChild(rocketBody);

		
		MouseRotate mousebehavior = new MouseRotate();
		mousebehavior.setTransformGroup(ss);
		main.addChild(mousebehavior);
		mousebehavior.setSchedulingBounds(bounds);

		// Create the zoom behavior node
		MouseZoom secondbehavior = new MouseZoom();
		secondbehavior.setTransformGroup(ss);
		main.addChild(secondbehavior);
		secondbehavior.setSchedulingBounds(bounds);

		// Create the translate behavior node
		MouseTranslate behavior3 = new MouseTranslate();
		behavior3.setTransformGroup(ss);
		main.addChild(behavior3);
		behavior3.setSchedulingBounds(bounds);

		platformGeom.addChild(boundingLeaf);

		main.addChild(ss);

		main.compile();
		return main;
	}
	
	
	
	public TransformGroup Planet(float radius, float distance, long orbit, int rotate, String texture) {
	
		TransformGroup transgroup0 = new TransformGroup();
		transgroup0.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);

		// set orbit speed around the sun
		// -1 allows unlimited orbits
		Alpha rotationaAl = new Alpha(-1, orbit);


		Transform3D yAxis = new Transform3D();
		// rotates the relative y axis by 90 degrees
		
		yAxis.rotX((Math.PI/2));
		//sets up to orbit a full circle around 0,0
		RotationInterpolator rotator0 = new RotationInterpolator(rotationaAl, transgroup0, yAxis, 0.0f,
				(float) Math.PI * (2));
		rotator0.setSchedulingBounds(bounds);

		// create new transform group
		Transform3D t = new Transform3D();
		t.setScale(new Vector3d(2.0, 2.0, 2.0));
		t.setTranslation(new Vector3d(0.0,  (-distance), 0.0));
		Transform3D helperT3D = new Transform3D();
		helperT3D.rotZ(Math.PI);
		t.mul(helperT3D);
		helperT3D.rotX(Math.PI / 2);
		t.mul(helperT3D);
		//set translations to transform group
		TransformGroup transgroup1 = new TransformGroup(t);

		 
		TransformGroup transgroup2 = new TransformGroup();
		transgroup2.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
	
	
		// -1 means indefinite loop count
		Alpha rotationAlpha2 = new Alpha(-1, rotate);

		Transform3D yAxis2 = new Transform3D();
		//sets up to rotate about itself
		RotationInterpolator rotator2 = new RotationInterpolator(rotationAlpha2, transgroup2, yAxis2, 0.0f,
				(float) Math.PI * (2.0f));
		rotator2.setSchedulingBounds(bounds);

		// create textures for a sphere
		TextureLoader tl = new TextureLoader(texture, new Container());
		Texture atmosphere = tl.getTexture();
		atmosphere.setBoundaryModeS(Texture.WRAP);
		atmosphere.setBoundaryModeT(Texture.WRAP);

		TextureAttributes ta = new TextureAttributes();
		ta.setTextureMode(TextureAttributes.MODULATE);
		//set appearance for planets
		Appearance app = new Appearance();
		app.setTexture(atmosphere);
		app.setTextureAttributes(ta);



		Material material = new Material();
		
		//sets the material of objects over 2f in radius
		if (radius >= 2f) {
			material.setEmissiveColor(new Color3f(Color.WHITE));
		}
		//sets how much light the object reflects
		material.setShininess(5000);
		
		app.setMaterial(material);
		
		//sets each sphere radius, how many shapes to use(how smooth the object is), and appearance
		Sphere body = new Sphere(radius, primflags, 100, app);

		//add all to the main TG for the method
		transgroup0.addChild(transgroup1);
		transgroup1.addChild(transgroup2);
		transgroup0.addChild(rotator0);
		transgroup2.addChild(rotator2);
		transgroup2.addChild(body);

		//return main method TG
		return transgroup0;
	}

	public TransformGroup CustomShape(){
		TransformGroup newShape = new TransformGroup();
	
		newShape.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		
		Point3f[] verts = {
				//first half of base
				new Point3f(1,1,0), new Point3f(1,-1,0), new Point3f(-1,1,0),
				//second half of base
				new Point3f(-1,-1,0), new Point3f(-1,1,0), new Point3f(1
				,-1,0),
				// each of the sides of the pyramid
				new Point3f(1,1,0)  , new Point3f(-1,1,0),  new Point3f(0,0
				,2),
				new Point3f(-1,1,0 ), new Point3f(-1,-1,0), new Point3f(0
				,0,2),
				new Point3f(-1,-1,0), new Point3f(1,-1,0), new Point3f(0
				,0,2),
				new Point3f(1,-1,0), new Point3f(1,1,0), new Point3f(0,0
				,2)};
				TriangleArray tri = new TriangleArray(18, TriangleArray.COORDINATES |
				TriangleArray.NORMALS | TriangleArray.TEXTURE_COORDINATE_2);
				tri.setCoordinates(0, verts);
				
				
				Alpha rotationAl = new Alpha(-1, 58000);

				// sets up rotation of the planet around the sun
				Transform3D yAxis = new Transform3D();
				// rotates the relative y axis by 90 degrees
				
				yAxis.rotX((Math.PI/2));
				//sets up to orbit a full circle around 0,0
				RotationInterpolator rotatorShapeOrbit = new RotationInterpolator(rotationAl, newShape, yAxis, 0.0f,
						(float) Math.PI * (2));
				rotatorShapeOrbit.setSchedulingBounds(bounds);
				
				Transform3D trans = new Transform3D();
				trans.setScale(new Vector3d(2.0, 2.0, 2.0));
				trans.setTranslation(new Vector3d(0.0,  (-8.0f), 0.0));
				Transform3D helperT3D = new Transform3D();
				helperT3D.rotZ(Math.PI);
				trans.mul(helperT3D);
				helperT3D.rotX(Math.PI / 2);
				trans.mul(helperT3D);
				//set translations to transform group
				TransformGroup translationTG = new TransformGroup(trans);
				
				TransformGroup tGroup2 = new TransformGroup();
				tGroup2.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
			
			
				// -1 means indefinite loop count
				Alpha rotationAlpha2 = new Alpha(-1, 250000);

				Transform3D yAxis2 = new Transform3D();
				//sets up to rotate about itself
				RotationInterpolator rotator2 = new RotationInterpolator(rotationAlpha2, tGroup2, yAxis2, 0.0f,
						(float) Math.PI * (2.0f));
				rotator2.setSchedulingBounds(bounds);
			
				
				
				Material material = new Material();
				material.setShininess(5000);
				
				
				Shape3D shape= new Shape3D(tri);
				
				newShape.addChild(translationTG);
				translationTG.addChild(tGroup2);
				newShape.addChild(rotatorShapeOrbit);
				tGroup2.addChild(rotator2);
				tGroup2.addChild(shape);
				
				
			
		return newShape;
	}

	public static void main(String[] args) {

		new MainFrame(new SolarSystem(), 1000, 1000);
	}

}