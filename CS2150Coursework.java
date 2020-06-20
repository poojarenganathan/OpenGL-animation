/* CS2150Coursework.java

 * 180201200
 * Pooja Renganathan
 * BSc Computer Science
 * Second year
 * 
 * All work is my own.
 *
 * Scene Graph:
 *  Scene origin
 *  |
 *  +-- [S(25,1,20) T(0,-1,-10)] Ground plane
 *  |
 *  +-- [S(25,1,10) Rx(90) T(0,4,-20)] Sky plane
 *  |
 *  +-- [T(-5.7,currentSun,-19)] Sun
 *  |
 *  +-- [T(-4.1,-0.2,-8.5)] First boulder
 *  |
 *  +-- [T(-4.1,-0.2,-10)] Second boulder
 *  |
 *  +-- [Rx(30) T(-0.5,-1.0,-15)] Mountain
 *  |
 *  +-- [T(1.6,-1,-4.3)] Tree
 *  |   |
 *  |   +-- [Rx(-90)] Trunk
 *  |   |
 *  |   +-- [T(1.6,-0.1,-4.3)] Leafy head
 *  |
 *  +-- [S(0.23,0.4,0.6) T(currentVan,-0.1,-2.5)] Van
 *  |   |
 *  |   +-- [S(0.23,0.4,0.6) T(currentVan,-0.1,-2.5)] Van front
 *  |   |
 *  |   +-- [S(0.2,0.2,0.2) T(currentVan,-0.3,-2.2)] First wheel
 *  |   |
 *  |   +-- [S(0.2,0.2,0.2) T(currentVan + 0.2,-0.3,-2.2)] Second wheel
 *  |
 *  +-- [S(0.25,0.2,0.25) T(currentCar,-0.3,-2.0)] Car
 *      |
 *      +-- [S(0.85,0.85,0.85) T(0.0,0.0,0.0)] Car roof
 *      |
 *      +-- [T(0,-0.65,0.8)] First wheel
 *      |
 *      +-- [T(0.85,-0.6,0.5)] Second wheel
 *      |
 *      +-- [T(0.8,-0.6,-0.2)] Third wheel
 *      |
 *      +-- [T(-0.2,-0.65,-0.5)] Fourth wheel
 */
package coursework.prenganathan;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.Cylinder;
import org.lwjgl.util.glu.Sphere;
import org.lwjgl.util.glu.Disk;
import org.lwjgl.input.Keyboard;
import org.newdawn.slick.opengl.Texture;
import GraphicsLab.*;

/*
 * This coursework depicts a mountain scene and demonstrates user input through different animations. 	
 * The sun will rise and set, and vehicles will move relative to each other along the mountain road. When 
 * the car moves to the right and stops in front of the tree, the van will simultaneously move to the 
 * left and vice versa. 
 * 
 * 
 * <p>Controls:
 * <ul>
 * <li>Press the escape key to exit the application
 * <li>Press the space key to reset the application
 * <li>Hold the x, y and z keys to view the scene along the x, y and z axis, respectively
 * <li>While viewing the scene along the x, y or z axis, use the up and down cursor keys
 *      to increase or decrease the viewpoint's distance from the scene origin
 * <li>Press U to make the sun rise
 * <li>Press D to make the sun set   
 * <li>Press R to move the car to the right and the van to the left 
 * <li>Press L to move the car to the left and the van to the right
 * </ul>
 * 
 */

public class CS2150Coursework extends GraphicsLab
{
 
    // display list id for the plane
    private final int planeList = 1;
    
    // display list id for mountain
    private final int mountainList = 2;
    
    // display list id for the car 
    private final int carList  = 3; 
    
    // display list id for the car roof 
    private final int roofList  = 4;
    
    // display list id for van
    private final int vanList = 5;
    
    // display id for front of van
    private final int frontList = 6;
    
    // ids for nearest, linear and mipmapped textures for the ground plane and sky plane
    private Texture groundTextures;
    private Texture mountainTextures;
   
    // the sun's current Y offset from the scene origin 
    private float currentSun = 2.7f;  
    // the sun's lowest possible Y offset
    private final float lowestSun = currentSun;
    // the sun's highest possible Y offset
    private final float highestSun  = 5.5f; 
    // initialise risingSun to false 
    private boolean risingSun = false;
    
    // the current X offset of the car from the scene origin
    private float currentCar = -0.8f;   
    // the car's nearest possible X offset  
    private final float nearestCar = currentCar;
    // the car's furthest possible X offset 
    private final float furthestCar = 0.30f;
    // initialise movingCar to false
    private boolean movingCar = false;

    // the current X offset of the van from the scene origin
    private float currentVan = 0.3f;  
    // the van's furthest possible X offset 
    private final float furthestVan = currentVan;
 
 
    public static void main(String args[]) 
    {
       new CS2150Coursework().run(WINDOWED,"CS2150 Coursework Submission",0.01f);
    }

    
    protected void initScene() throws Exception 
    {
  	
    	// load the textures
    	groundTextures = loadTexture("coursework/prenganathan/textures/road.bmp");
        mountainTextures = loadTexture("coursework/prenganathan/textures/mountain.bmp");
        		
    	// global ambient light level
        float globalAmbient[]   = {0.1f,  0.1f,  0.1f, 1f};
        
        // set the global ambient lighting
        GL11.glLightModel(GL11.GL_LIGHT_MODEL_AMBIENT, FloatBuffer.wrap(globalAmbient));

        // the first light for the scene is white...
        float diffuse0[]  = { 0.6f,  0.6f, 0.6f, 1.0f};
        // ...with a dim ambient contribution...
        float ambient0[]  = { 0.1f,  0.1f, 0.1f, 1.0f};
        // ...and is positioned above and behind the viewpoint
        float position0[] = { 0.0f, 10.0f, 5.0f, 1.0f}; 

        // supply OpenGL with the properties for the first light
        GL11.glLight(GL11.GL_LIGHT0, GL11.GL_AMBIENT, FloatBuffer.wrap(ambient0));
        GL11.glLight(GL11.GL_LIGHT0, GL11.GL_DIFFUSE, FloatBuffer.wrap(diffuse0));
  		GL11.glLight(GL11.GL_LIGHT0, GL11.GL_SPECULAR, FloatBuffer.wrap(diffuse0));
        GL11.glLight(GL11.GL_LIGHT0, GL11.GL_POSITION, FloatBuffer.wrap(position0));
        
        // enable the first light
        GL11.glEnable(GL11.GL_LIGHT0);
      
        // enable lighting calculations
        GL11.glEnable(GL11.GL_LIGHTING);
        // ensure that all normals are re-normalised after transformations automatically
        GL11.glEnable(GL11.GL_NORMALIZE);
        
        // prepare the display lists for later use
        GL11.glNewList(planeList,GL11.GL_COMPILE);
        {   drawUnitPlane();
        }
        GL11.glEndList();
        
        GL11.glNewList(mountainList, GL11.GL_COMPILE);
        {   drawUnitPyramid();
        }
        GL11.glEndList();
          
        GL11.glNewList(carList,GL11.GL_COMPILE);
        {   drawUnitCuboid();
        }
        GL11.glEndList();
        
        GL11.glNewList(roofList,GL11.GL_COMPILE);
        {   drawUnitPrism();
        }
        GL11.glEndList();
          
        GL11.glNewList(vanList,GL11.GL_COMPILE);
        {   drawUnitCuboid2();
        }
        GL11.glEndList();
        
        GL11.glNewList(frontList,GL11.GL_COMPILE);
        {   drawUnitPrism2();
        }
        GL11.glEndList();   
    }
    	
    
    protected void checkSceneInput()
    {
    	// sun will rise 
    	if(Keyboard.isKeyDown(Keyboard.KEY_U))
        {   risingSun = true;
        }
    	// sun will set
        else if(Keyboard.isKeyDown(Keyboard.KEY_D))
        {   risingSun = false;
        }
        else if(Keyboard.isKeyDown(Keyboard.KEY_SPACE))
        {   resetAnimations();
        }
        
    	// moves the car to the right and the van to the left 
        if(Keyboard.isKeyDown(Keyboard.KEY_R))	
        {   movingCar = true;	
        }  
        // moves the car to the left and the van to the right 
        else if(Keyboard.isKeyDown(Keyboard.KEY_L))
        {   movingCar = false;  	
        }
        else if(Keyboard.isKeyDown(Keyboard.KEY_SPACE))
        {   resetAnimations();
        }
    }
    
    
    protected void updateScene() 
    {	
    	// if the sun is rising, and it isn't at its highest, then increment the sun's Y offset
        if(risingSun && currentSun < highestSun) 
        {
            	currentSun += 0.2f * getAnimationScale();
        }
        // else if the sun is falling, and it isn't at its lowest, then decrement the sun's Y offset
        else if(!risingSun && currentSun > lowestSun){
        	currentSun -= 0.2f * getAnimationScale();
        }
    
        /* if the car is moving, and it isn't at its furthest, then increment the car's X offset
           decrement the van's x offset */
        if(movingCar && currentCar < furthestCar)    
        {
        	currentCar += 0.2f * getAnimationScale(); 
        	currentVan -= 0.2f * getAnimationScale();	      	
        }
        /* else if the car is moving, and it isn't at its nearest, then decrement the car's X offset
           increment the van's x offset */
        else if(!movingCar && currentCar > nearestCar) 
        {
        	currentCar -= 0.2f * getAnimationScale();	
        	currentVan += 0.2f * getAnimationScale();
        }
    }
        
 
    protected void renderScene() 
    {
    	// draw the ground plane
        GL11.glPushMatrix();
        {
            // disable lighting calculations so that they don't affect the appearance of the texture 
            GL11.glPushAttrib(GL11.GL_LIGHTING_BIT);
            GL11.glDisable(GL11.GL_LIGHTING);
            
            // change the geometry colour to white so that the texture is bright and details can be seen clearly
            Colour.WHITE.submit();
            
            // enable texturing and bind an appropriate texture
            GL11.glEnable(GL11.GL_TEXTURE_2D);
            GL11.glBindTexture(GL11.GL_TEXTURE_2D,groundTextures.getTextureID());
            
            // position, scale and draw the ground plane using its display list
            GL11.glTranslatef(0.0f,-1.0f,-10.0f);
            GL11.glScaled(25.0f, 1.0f, 20.0f);
            GL11.glCallList(planeList);

            // disable textures and reset any local lighting changes
            GL11.glDisable(GL11.GL_TEXTURE_2D);
            GL11.glPopAttrib();
        }
        GL11.glPopMatrix();
        
        
        // draw the back plane
        GL11.glPushMatrix();
        {
            // disable lighting calculations so that they don't affect the appearance of the texture 
            GL11.glPushAttrib(GL11.GL_LIGHTING_BIT);
            GL11.glDisable(GL11.GL_LIGHTING);
            
            // change the geometry colour to white so that the texture is bright and details can be seen clearly
            Colour.WHITE.submit();
            
            // enable texturing and bind an appropriate texture
            GL11.glEnable(GL11.GL_TEXTURE_2D);
            GL11.glBindTexture(GL11.GL_TEXTURE_2D,mountainTextures.getTextureID());
            
            // position, scale and draw the back plane using its display list
            GL11.glTranslatef(0.0f,4.0f,-20.0f);
            GL11.glRotatef(90.0f, 1.0f, 0.0f, 0.0f);
            GL11.glScalef(25.0f, 1.0f, 10.0f);
            GL11.glCallList(planeList);
            
            // disable textures and reset any local lighting changes
            GL11.glDisable(GL11.GL_TEXTURE_2D);
            GL11.glPopAttrib();
        }
        GL11.glPopMatrix();
        
     
        // draw the sun
        GL11.glPushMatrix();
        {
            // how shiny are the front faces of the sun (specular exponent)
            float sunFrontShininess  = 1.0f;
            // specular reflection of the front faces of the sun
            float sunFrontSpecular[] = {1.0f, 0.7f, 0.2f, 1.0f};
            // diffuse reflection of the front faces of the sun
            float sunFrontDiffuse[]  = {1.0f, 0.7f, 0.2f, 1.0f};

            // set the material properties for the sun using OpenGL
            GL11.glMaterialf(GL11.GL_FRONT, GL11.GL_SHININESS, sunFrontShininess);
            GL11.glMaterial(GL11.GL_FRONT, GL11.GL_SPECULAR, FloatBuffer.wrap(sunFrontSpecular));
            GL11.glMaterial(GL11.GL_FRONT, GL11.GL_DIFFUSE, FloatBuffer.wrap(sunFrontDiffuse));

            // position and draw the sun using a sphere quadric object
            GL11.glTranslatef(-5.7f, currentSun, -19.0f);
            new Sphere().draw(0.4f,12,12);
        }
        GL11.glPopMatrix();
        
        
        // draw the first boulder
        GL11.glPushMatrix();
        {
            // how shiny are the front faces of the boulder (specular exponent)
            float boulder1FrontShininess  = 1.0f;
            // specular reflection of the front faces of the boulder
            float boulder1FrontSpecular[] = {0.2f, 0.2f, 0.2f, 1.0f};
            // diffuse reflection of the front faces of the boulder
            float boulder1FrontDiffuse[]  = {0.2f, 0.2f, 0.2f, 1.0f};

            // set the material properties for the boulder using OpenGL
            GL11.glMaterialf(GL11.GL_FRONT, GL11.GL_SHININESS, boulder1FrontShininess);
            GL11.glMaterial(GL11.GL_FRONT, GL11.GL_SPECULAR, FloatBuffer.wrap(boulder1FrontSpecular));
            GL11.glMaterial(GL11.GL_FRONT, GL11.GL_DIFFUSE, FloatBuffer.wrap(boulder1FrontDiffuse));

            // position and draw the boulder using a sphere quadric object
            GL11.glTranslatef(-4.1f, -0.2f, -8.5f);
            new Sphere().draw(0.4f, 9, 9);
        }
        GL11.glPopMatrix();
        
        
        // draw the second boulder
        GL11.glPushMatrix();
        {
            // how shiny are the front faces of the boulder (specular exponent)
            float boulder2FrontShininess  = 1.0f;
            // specular reflection of the front faces of the boulder
            float boulder2FrontSpecular[] = {0.2f, 0.2f, 0.2f, 1.0f};
            // diffuse reflection of the front faces of the boulder
            float boulder2FrontDiffuse[]  = {0.2f, 0.2f, 0.2f, 1.0f};

            // set the material properties for the boulder using OpenGL
            GL11.glMaterialf(GL11.GL_FRONT, GL11.GL_SHININESS, boulder2FrontShininess);
            GL11.glMaterial(GL11.GL_FRONT, GL11.GL_SPECULAR, FloatBuffer.wrap(boulder2FrontSpecular));
            GL11.glMaterial(GL11.GL_FRONT, GL11.GL_DIFFUSE, FloatBuffer.wrap(boulder2FrontDiffuse));

            // position and draw the boulder using a sphere quadric object
            GL11.glTranslatef(-4.1f, -0.2f, -10.0f);
            new Sphere().draw(0.4f, 9, 9);
        }
        GL11.glPopMatrix();
        
        
        // draw the mountain
	    GL11.glPushMatrix();
	    {
	    	// how shiny are the front faces of the mountain (specular exponent)
	        float mountainFrontShininess  = 5.0f;
	        // specular reflection of the front faces of the mountain
	        float mountainFrontSpecular[] = {0.1f, 0.0f, 0.0f, 1.0f};
	        // diffuse reflection of the front faces of the mountain
	        float mountainFrontDiffuse[]  = {0.15f, 0.05f, 0.05f, 1.0f};
	            
	        // set the material properties for the mountain using OpenGL
	        GL11.glMaterialf(GL11.GL_FRONT, GL11.GL_SHININESS, mountainFrontShininess);
	        GL11.glMaterial(GL11.GL_FRONT, GL11.GL_SPECULAR, FloatBuffer.wrap(mountainFrontSpecular));
	        GL11.glMaterial(GL11.GL_FRONT, GL11.GL_DIFFUSE, FloatBuffer.wrap(mountainFrontDiffuse));

	        // position and draw the mountain
	        GL11.glTranslatef(-0.5f,-1.0f,-15.0f);
	        GL11.glRotatef(30, 1.0f, 0.0f, 0.0f);
	         
	        // draw the mountain using its display list
	        GL11.glCallList(mountainList);    
	     }
	     GL11.glPopMatrix();
     
	     
	     // draw the tree
	     GL11.glPushMatrix();
	     {
	    	 // how shiny are the front faces of the trunk (specular exponent)
	         float trunkFrontShininess  = 1.0f;
	         // specular reflection of the front faces of the trunk
	         float trunkFrontSpecular[] = {0.15f, 0.05f, 0.0f, 1.0f};
	         // diffuse reflection of the front faces of the trunk
	         float trunkFrontDiffuse[]  = {0.2f, 0.1f, 0.0f, 1.0f};
	            
	         // set the material properties for the trunk using OpenGL
	         GL11.glMaterialf(GL11.GL_FRONT, GL11.GL_SHININESS, trunkFrontShininess);
	         GL11.glMaterial(GL11.GL_FRONT, GL11.GL_SPECULAR, FloatBuffer.wrap(trunkFrontSpecular));
	         GL11.glMaterial(GL11.GL_FRONT, GL11.GL_DIFFUSE, FloatBuffer.wrap(trunkFrontDiffuse));

	         // position the tree
	         GL11.glTranslatef(1.6f,-1.0f,-4.3f);
	            
	         /* draw the trunk using a cylinder quadric object. Surround the draw call with a
                push/pop matrix pair, as the cylinder will originally be aligned with the Z axis
	            and will have to be rotated to align it along the Y axis */  
	         GL11.glPushMatrix();
	         {
	        	 GL11.glRotatef(-90.0f, 1.0f, 0.0f, 0.0f);
	             new Cylinder().draw(0.075f, 0.075f, 0.9f, 10, 10);
	         }
	         GL11.glPopMatrix();

	         // how shiny are the front faces of the leafy head of the tree (specular exponent)
	         float headFrontShininess  = 10.0f;
	         // specular reflection of the front faces of the head
	         float headFrontSpecular[] = {0.1f, 0.3f, 0.1f, 1.0f};
	         // diffuse reflection of the front faces of the head
	         float headFrontDiffuse[]  = {0.0f, 0.5f, 0.0f, 1.0f};
	            
	         // set the material properties for the head using OpenGL
	         GL11.glMaterialf(GL11.GL_FRONT, GL11.GL_SHININESS, headFrontShininess);
             GL11.glMaterial(GL11.GL_FRONT, GL11.GL_SPECULAR, FloatBuffer.wrap(headFrontSpecular));
	         GL11.glMaterial(GL11.GL_FRONT, GL11.GL_DIFFUSE, FloatBuffer.wrap(headFrontDiffuse));

	         // position and draw the leafy head using a sphere quadric object
	         GL11.glTranslatef(1.6f, -0.1f,-4.3f);
	         new Sphere().draw(0.32f, 10, 10);
	     }
	     GL11.glPopMatrix();
	        
	        
	     // draw the van 
		 GL11.glPushMatrix();
		 {
			 // position and scale the van
			 GL11.glTranslatef(currentVan, -0.1f, -2.5f);
			 GL11.glScalef(0.23f, 0.4f, 0.6f);
			         
			 // how shiny are the front faces of the van (specular exponent)
			 float vanFrontShininess  = 2.0f;
			 // specular reflection of the front faces of the van
			 float vanFrontSpecular[] = {0.0f, 0.0f, 0.7f, 1.0f};
		 	// diffuse reflection of the front faces of the van
			 float vanFrontDiffuse[]  = {0.0f, 0.0f, 0.7f, 1.0f};
			        
			 // set the material properties for the van using OpenGL
			 GL11.glMaterialf(GL11.GL_FRONT, GL11.GL_SHININESS, vanFrontShininess);
			 GL11.glMaterial(GL11.GL_FRONT, GL11.GL_SPECULAR, FloatBuffer.wrap(vanFrontSpecular));
			 GL11.glMaterial(GL11.GL_FRONT, GL11.GL_DIFFUSE, FloatBuffer.wrap(vanFrontDiffuse));
	        
			 // draw the van using its display list
			 GL11.glCallList(vanList);
		 }  
		 GL11.glPopMatrix();
		     
		     
		 // draw the van front 
		 GL11.glPushMatrix();
		 {
			 // position and scale the van front 
			 GL11.glTranslatef(currentVan, -0.1f, -2.5f);
			 GL11.glScalef(0.23f, 0.4f, 0.6f);
			                
			 // how shiny are the front faces of the van (specular exponent)
			 float vanFrontShininess  = 2.0f;
			 // specular reflection of the front faces of the van
			 float vanFrontSpecular[] = {0.0f, 0.0f, 0.7f, 1.0f};
			 // diffuse reflection of the front faces of the van
			 float vanFrontDiffuse[]  = {0.f, 0.0f, 0.7f, 1.0f};
			        
			 // set the material properties for the van using OpenGL
			 GL11.glMaterialf(GL11.GL_FRONT, GL11.GL_SHININESS, vanFrontShininess);
			 GL11.glMaterial(GL11.GL_FRONT, GL11.GL_SPECULAR, FloatBuffer.wrap(vanFrontSpecular));
			 GL11.glMaterial(GL11.GL_FRONT, GL11.GL_DIFFUSE, FloatBuffer.wrap(vanFrontDiffuse));

			 // draw the front of the van using its display list
			 GL11.glCallList(frontList);
		 }  
		 GL11.glPopMatrix();     
	        
		  
		 // draw the first wheel of the van
		 GL11.glPushMatrix();
		 {
			 // how shiny are the front faces of the wheel (specular exponent)
		     float wheel1FrontShininess  = 10.0f;
		     // specular reflection of the front faces of the wheel
		     float wheel1FrontSpecular[] = {0.05f, 0.05f, 0.05f, 1.0f};
		     // diffuse reflection of the front faces of the wheel
		     float wheel1FrontDiffuse[]  = {0.05f, 0.05f, 0.05f, 1.0f};

		     // set the material properties for the wheel using OpenGL
		     GL11.glMaterialf(GL11.GL_FRONT, GL11.GL_SHININESS, wheel1FrontShininess);
		     GL11.glMaterial(GL11.GL_FRONT, GL11.GL_SPECULAR, FloatBuffer.wrap(wheel1FrontSpecular));
		     GL11.glMaterial(GL11.GL_FRONT, GL11.GL_DIFFUSE, FloatBuffer.wrap(wheel1FrontDiffuse));

		     // position, scale and draw the wheel using a disk quadric object
		     GL11.glTranslatef(currentVan, -0.3f, -2.2f);
		     GL11.glScalef(0.2f, 0.2f, 0.2f); 
		     new Disk().draw(0.18f, 0.4f, 15, 15);
		 }
		 GL11.glPopMatrix();  
    
		 
		 // draw the second wheel of the van
		 GL11.glPushMatrix();
		 {
			 // how shiny are the front faces of the wheel (specular exponent)
		     float wheel2FrontShininess  = 10.0f;
		     // specular reflection of the front faces of the wheel
		     float wheel2FrontSpecular[] = {0.05f, 0.05f, 0.05f, 1.0f};
		     // diffuse reflection of the front faces of the wheel
		     float wheel2FrontDiffuse[]  = {0.05f, 0.05f, 0.05f, 1.0f};

		     // set the material properties for the wheel using OpenGL
		     GL11.glMaterialf(GL11.GL_FRONT, GL11.GL_SHININESS, wheel2FrontShininess);
		     GL11.glMaterial(GL11.GL_FRONT, GL11.GL_SPECULAR, FloatBuffer.wrap(wheel2FrontSpecular));
		     GL11.glMaterial(GL11.GL_FRONT, GL11.GL_DIFFUSE, FloatBuffer.wrap(wheel2FrontDiffuse));

		     // position, scale and draw the wheel using a disk quadric object
		     GL11.glTranslatef(currentVan + 0.2f, -0.3f, -2.2f);
		     GL11.glScalef(0.2f, 0.2f, 0.2f); 
		     new Disk().draw(0.18f, 0.4f, 15, 15);
		 }
		 GL11.glPopMatrix();  
    
		           
        // draw the car 
        GL11.glPushMatrix();
        {
        	// position and scale the car
	        GL11.glTranslatef(currentCar, -0.3f, -2.0f);
	        GL11.glScalef(0.25f, 0.20f, 0.25f);
	          
	        // how shiny are the front faces of the car (specular exponent)
	        float carFrontShininess  = 2.0f;
	        // specular reflection of the front faces of the car
	        float carFrontSpecular[] = {0.4f, 0.0f, 0.0f, 1.0f};
	        // diffuse reflection of the front faces of the car
	        float carFrontDiffuse[]  = {0.4f, 0.0f, 0.0f, 1.0f};
	        
	        // set the material properties for the car using OpenGL
	        GL11.glMaterialf(GL11.GL_FRONT, GL11.GL_SHININESS, carFrontShininess);
	        GL11.glMaterial(GL11.GL_FRONT, GL11.GL_SPECULAR, FloatBuffer.wrap(carFrontSpecular));
	        GL11.glMaterial(GL11.GL_FRONT, GL11.GL_DIFFUSE, FloatBuffer.wrap(carFrontDiffuse));
   
	        // draw the base of the car using its display list
	        GL11.glCallList(carList);
        }  
        
        
	    // draw the roof of the car
	    GL11.glPushMatrix();
	    {     
	    	// how shiny are the front faces of the roof (specular exponent)
		    float roofFrontShininess  = 5.0f;
		    // specular reflection of the front faces of the roof
		    float roofFrontSpecular[] = {0.4f, 0.0f, 0.0f, 1.0f};
		    // diffuse reflection of the front faces of the roof
		    float roofFrontDiffuse[]  = {0.4f, 0.0f, 0.0f, 1.0f};
		        
		    // set the material properties for the roof using OpenGL
		    GL11.glMaterialf(GL11.GL_FRONT, GL11.GL_SHININESS, roofFrontShininess);
		    GL11.glMaterial(GL11.GL_FRONT, GL11.GL_SPECULAR, FloatBuffer.wrap(roofFrontSpecular));
		    GL11.glMaterial(GL11.GL_FRONT, GL11.GL_DIFFUSE, FloatBuffer.wrap(roofFrontDiffuse));
		        
		    // position and scale the roof relative to the base of the car
		    GL11.glTranslatef(0.0f, 0.0f, 0.0f);
		    GL11.glScalef(0.85f, 0.85f, 0.85f);
  
		    // draw the roof of the car using its display list
		    GL11.glCallList(roofList); 
	    }
	     
	     
	    // draw the first wheel
	    GL11.glPushMatrix();
	    {
	    	 // how shiny are the front faces of the wheel (specular exponent)
	         float wheel1FrontShininess  = 10.0f;
	         // specular reflection of the front faces of the wheel
	         float wheel1FrontSpecular[] = {0.1f, 0.1f, 0.1f, 1.0f};
	         // diffuse reflection of the front faces of the wheel
	         float wheel1FrontDiffuse[]  = {0.1f, 0.1f, 0.1f, 1.0f};

	         // set the material properties for the wheel using OpenGL
	         GL11.glMaterialf(GL11.GL_FRONT, GL11.GL_SHININESS, wheel1FrontShininess);
	         GL11.glMaterial(GL11.GL_FRONT, GL11.GL_SPECULAR, FloatBuffer.wrap(wheel1FrontSpecular));
	         GL11.glMaterial(GL11.GL_FRONT, GL11.GL_DIFFUSE, FloatBuffer.wrap(wheel1FrontDiffuse));

	         // position and draw the wheel using a disk quadric object
	         GL11.glTranslatef(0.0f, -0.65f, 0.8f);
	         new Disk().draw(0.18f, 0.38f, 15, 15);
	     }
	     GL11.glPopMatrix();
	     
	     
	     // draw the second wheel
	     GL11.glPushMatrix();
	     {
	    	 // how shiny are the front faces of the wheel (specular exponent)
	         float wheel2FrontShininess  = 10.0f;
	         // specular reflection of the front faces of the wheel
	         float wheel2FrontSpecular[] = {0.1f, 0.1f, 0.1f, 1.0f};
	         // diffuse reflection of the front faces of the wheel
	         float wheel2FrontDiffuse[]  = {0.1f, 0.1f, 0.1f, 1.0f};

	         // set the material properties for the wheel using OpenGL
	         GL11.glMaterialf(GL11.GL_FRONT, GL11.GL_SHININESS, wheel2FrontShininess);
	         GL11.glMaterial(GL11.GL_FRONT, GL11.GL_SPECULAR, FloatBuffer.wrap(wheel2FrontSpecular));
	         GL11.glMaterial(GL11.GL_FRONT, GL11.GL_DIFFUSE, FloatBuffer.wrap(wheel2FrontDiffuse));

	         // position and draw the wheel using a disk quadric object
	         GL11.glTranslatef(0.85f, -0.6f, 0.5f);
	         new Disk().draw(0.16f, 0.34f, 15, 15);
	     }
	     GL11.glPopMatrix();
	     
	     
	     // draw the third wheel
	     GL11.glPushMatrix();
	     {
	    	 // how shiny are the front faces of the wheel (specular exponent)
	         float wheel3FrontShininess  = 10.0f;
	         // specular reflection of the front faces of the wheel
	         float wheel3FrontSpecular[] = {0.1f, 0.1f, 0.1f, 1.0f};
	         // diffuse reflection of the front faces of the wheel
	         float wheel3FrontDiffuse[]  = {0.1f, 0.1f, 0.1f, 1.0f};

	         // set the material properties for the wheel using OpenGL
	         GL11.glMaterialf(GL11.GL_FRONT, GL11.GL_SHININESS, wheel3FrontShininess);
	         GL11.glMaterial(GL11.GL_FRONT, GL11.GL_SPECULAR, FloatBuffer.wrap(wheel3FrontSpecular));
	         GL11.glMaterial(GL11.GL_FRONT, GL11.GL_DIFFUSE, FloatBuffer.wrap(wheel3FrontDiffuse));

	         // position and draw the wheel using a disk quadric object
	         GL11.glTranslatef(0.8f, -0.6f, -0.2f);
	         new Disk().draw(0.16f, 0.34f, 15, 15);
	     }
	     GL11.glPopMatrix();
	     
	     
	     // draw the fourth wheel
	     GL11.glPushMatrix();
	     {
	    	 // how shiny are the front faces of the wheel (specular exponent)
	         float wheel4FrontShininess  = 10.0f;
	         // specular reflection of the front faces of the wheel
	         float wheel4FrontSpecular[] = {0.1f, 0.1f, 0.1f, 1.0f};
	         // diffuse reflection of the front faces of the wheel3
	         float wheel4FrontDiffuse[]  = {0.1f, 0.1f, 0.1f, 1.0f};

	         // set the material properties for the wheel using OpenGL
	         GL11.glMaterialf(GL11.GL_FRONT, GL11.GL_SHININESS, wheel4FrontShininess);
	         GL11.glMaterial(GL11.GL_FRONT, GL11.GL_SPECULAR, FloatBuffer.wrap(wheel4FrontSpecular));
	         GL11.glMaterial(GL11.GL_FRONT, GL11.GL_DIFFUSE, FloatBuffer.wrap(wheel4FrontDiffuse));

	         // position and draw the wheel using a disk quadric object
	         GL11.glTranslatef(-0.2f, -0.65f, -0.5f);
	         new Disk().draw(0.18f, 0.38f, 15, 15);
	     }
	     GL11.glPopMatrix();
    }
       

	protected void setSceneCamera()
    {
        /* Calls the default behaviour defined in GraphicsLab
		 	this will set a default perspective projection
         	and default camera settings ready for some custom camera positioning below...  
        */
        super.setSceneCamera();   
    }

	
    protected void cleanupScene()
    { // empty
    }

    
    // reset all attributes that are modified by user controls or animations
    private void resetAnimations() 
    { 
    	currentSun = lowestSun;
        risingSun = true;
        
        currentCar = nearestCar;
        movingCar = true;
        
        currentVan = furthestVan;
    }
    
    
    /* 	Draws a plane aligned with the X and Z axis, with its front face toward positive Y.
        The plane is of unit width and height, and uses the current OpenGL material settings
        for its appearance */
    private void drawUnitPlane()
    {
    	Vertex v1 = new Vertex(-0.5f, 0.0f,-0.5f); // left,  back
        Vertex v2 = new Vertex( 0.5f, 0.0f,-0.5f); // right, back
        Vertex v3 = new Vertex( 0.5f, 0.0f, 0.5f); // right, front
        Vertex v4 = new Vertex(-0.5f, 0.0f, 0.5f); // left,  front
        
        // draw the plane geometry and order the vertices so that the plane faces up
        GL11.glBegin(GL11.GL_POLYGON);
        {
        	new Normal(v4.toVector(),v3.toVector(),v2.toVector(),v1.toVector()).submit();
            
            GL11.glTexCoord2f(0.0f,0.0f);
            v4.submit();
            
            GL11.glTexCoord2f(1.0f,0.0f);
            v3.submit();
            
            GL11.glTexCoord2f(1.0f,1.0f);
            v2.submit();
            
            GL11.glTexCoord2f(0.0f,1.0f);
            v1.submit();
        }
        GL11.glEnd();
        
        /* if the user is viewing an axis, then also draw this plane using lines so that axis 
           aligned planes can still be seen */
        if(isViewingAxis())
        {
            // also disable textures when drawing as lines so that the lines can be seen more clearly
            GL11.glPushAttrib(GL11.GL_TEXTURE_2D);
            GL11.glDisable(GL11.GL_TEXTURE_2D);
            GL11.glBegin(GL11.GL_LINE_LOOP);
            {
                v4.submit();
                v3.submit();
                v2.submit();
                v1.submit();
            }
            GL11.glEnd();
            GL11.glPopAttrib();
        }
    }
    

    // draws a cuboid as the base of the car 
    private void drawUnitCuboid()
    {
        // the vertices for the cuboid
        Vertex v1 = new Vertex(-0.5f, -0.5f,  0.5f);
        Vertex v2 = new Vertex(-0.5f,  0.1f,  0.5f);
        Vertex v3 = new Vertex( 1.4f,  0.1f,  0.5f);
        Vertex v4 = new Vertex( 1.4f, -0.5f,  0.5f);
        Vertex v5 = new Vertex(-0.5f, -0.5f, -0.5f);
        Vertex v6 = new Vertex(-0.5f,  0.1f, -0.5f);
        Vertex v7 = new Vertex( 1.4f,  0.1f, -0.5f);
        Vertex v8 = new Vertex( 1.4f, -0.5f, -0.5f);

        // draw the near face:
        GL11.glBegin(GL11.GL_POLYGON);
        {
            new Normal(v3.toVector(),v2.toVector(),v1.toVector(),v4.toVector()).submit();
            
            v3.submit();
            v2.submit();
            v1.submit();
            v4.submit();
        }
        GL11.glEnd();

        // draw the left face:
        GL11.glBegin(GL11.GL_POLYGON);
        {
            new Normal(v2.toVector(),v6.toVector(),v5.toVector(),v1.toVector()).submit();
            
        	v2.submit();
            v6.submit();
            v5.submit();
            v1.submit();
        }
        GL11.glEnd();

        // draw the right face:
        GL11.glBegin(GL11.GL_POLYGON);
        {
            new Normal(v7.toVector(),v3.toVector(),v4.toVector(),v8.toVector()).submit();
            
            v7.submit();
            v3.submit();
            v4.submit();
            v8.submit();
        }
        GL11.glEnd();

        // draw the top face:
        GL11.glBegin(GL11.GL_POLYGON);
        {
            new Normal(v7.toVector(),v6.toVector(),v2.toVector(),v3.toVector()).submit();
            
            v7.submit();
            v6.submit();
            v2.submit();
            v3.submit();
        }
        GL11.glEnd();

        // draw the bottom face:
        GL11.glBegin(GL11.GL_POLYGON);
        {
            new Normal(v4.toVector(),v1.toVector(),v5.toVector(),v8.toVector()).submit();
            
            v4.submit();
            v1.submit();
            v5.submit();
            v8.submit();
        }
        GL11.glEnd();

        // draw the far face:
        GL11.glBegin(GL11.GL_POLYGON);
        {
            new Normal(v6.toVector(),v7.toVector(),v8.toVector(),v5.toVector()).submit();
            
            v6.submit();
            v7.submit();
            v8.submit();
            v5.submit();
        }
        GL11.glEnd();
    }
    
    
    // draws a prism as the top of the car
    private void drawUnitPrism()
    {
        // the vertices for the prism
        Vertex v1 = new Vertex( -0.4f,  0.1f,  0.5f);
        Vertex v2 = new Vertex(  0.0f,  0.7f,  0.5f);
        Vertex v3 = new Vertex(  0.7f,  0.7f,  0.5f);
        Vertex v4 = new Vertex(  1.2f,  0.1f,  0.5f);
        Vertex v5 = new Vertex( -0.4f,  0.1f, -0.5f);
        Vertex v6 = new Vertex(  0.0f,  0.7f, -0.5f);
        Vertex v7 = new Vertex(  0.7f,  0.7f, -0.5f);
        Vertex v8 = new Vertex(  1.2f,  0.1f, -0.5f);

        // draw the near face:
        GL11.glBegin(GL11.GL_POLYGON);
        {
            new Normal(v3.toVector(),v2.toVector(),v1.toVector(),v4.toVector()).submit();
            
            v3.submit();
            v2.submit();
            v1.submit();
            v4.submit();
        }
        GL11.glEnd();

        // draw the left face:
        GL11.glBegin(GL11.GL_POLYGON);
        {
            new Normal(v2.toVector(),v6.toVector(),v5.toVector(),v1.toVector()).submit();
            
        	v2.submit();
            v6.submit();
            v5.submit();
            v1.submit();
        }
        GL11.glEnd();

        // draw the right face:
        GL11.glBegin(GL11.GL_POLYGON);
        {
            new Normal(v7.toVector(),v3.toVector(),v4.toVector(),v8.toVector()).submit();
            
            v7.submit();
            v3.submit();
            v4.submit();
            v8.submit();
        }
        GL11.glEnd();

        // draw the top face:
        GL11.glBegin(GL11.GL_POLYGON);
        {
            new Normal(v7.toVector(),v6.toVector(),v2.toVector(),v3.toVector()).submit();
            
            v7.submit();
            v6.submit();
            v2.submit();
            v3.submit();
        }
        GL11.glEnd();

        // draw the bottom face:
        GL11.glBegin(GL11.GL_POLYGON);
        {
            new Normal(v4.toVector(),v1.toVector(),v5.toVector(),v8.toVector()).submit();
            
            v4.submit();
            v1.submit();
            v5.submit();
            v8.submit();
        }
        GL11.glEnd();

        // draw the far face:
        GL11.glBegin(GL11.GL_POLYGON);
        {
            new Normal(v6.toVector(),v7.toVector(),v8.toVector(),v5.toVector()).submit();
            
            v6.submit();
            v7.submit();
            v8.submit();
            v5.submit();
        }
        GL11.glEnd();
    }  
    
    
    // draws a pyramid as the mountain
    private void drawUnitPyramid()
    {
        Vertex v1 = new Vertex( 0.0f,  2.0f,  0.0f);
        Vertex v2 = new Vertex(-3.0f, -0.5f, -0.9f);
        Vertex v3 = new Vertex( 3.0f, -0.5f, -0.9f);
        Vertex v4 = new Vertex( 3.0f,  0.0f,  0.9f);
        Vertex v5 = new Vertex(-3.0f,  0.0f,  0.9f);
        
        // draw the bottom face:
        GL11.glBegin(GL11.GL_POLYGON);
        {
            new Normal(v2.toVector(),v3.toVector(),v4.toVector(),v5.toVector()).submit();
            
            v2.submit();
            v3.submit();
            v4.submit();
            v5.submit();
        }
        GL11.glEnd();
        
        // draw the near face:
        GL11.glBegin(GL11.GL_TRIANGLES);
        {
            new Normal(v1.toVector(),v5.toVector(),v4.toVector()).submit();
            
            v1.submit();
            v5.submit();
            v4.submit();
        }
        GL11.glEnd();
        
        // draw the right face:
        GL11.glBegin(GL11.GL_TRIANGLES);
        {
            new Normal(v1.toVector(),v4.toVector(),v3.toVector()).submit();
            
            v1.submit();
            v4.submit();
            v3.submit();
        }
        GL11.glEnd();
        
        // draw the far face:
        GL11.glBegin(GL11.GL_TRIANGLES);
        {
            new Normal(v1.toVector(),v3.toVector(),v2.toVector()).submit();
            
            v1.submit();
            v3.submit();
            v2.submit();
        }
        GL11.glEnd();
        
        // draw the left face:
        GL11.glBegin(GL11.GL_TRIANGLES);
        {
            new Normal(v1.toVector(),v2.toVector(),v5.toVector()).submit();
            
            v1.submit();
            v2.submit();
            v5.submit();
        }
        GL11.glEnd();
    }
    
    
    // draws a cuboid as the van
    private void drawUnitCuboid2()
    {
        // the vertices for the cuboid
        Vertex v1 = new Vertex( 0.0f, -0.5f,  0.5f);
        Vertex v2 = new Vertex( 0.0f,  0.1f,  0.5f);
        Vertex v3 = new Vertex( 1.4f,  0.1f,  0.5f);
        Vertex v4 = new Vertex( 1.4f, -0.5f,  0.5f);
        Vertex v5 = new Vertex( 0.0f, -0.5f, -0.5f);
        Vertex v6 = new Vertex( 0.0f,  0.1f, -0.5f);
        Vertex v7 = new Vertex( 1.4f,  0.1f, -0.5f);
        Vertex v8 = new Vertex( 1.4f, -0.5f, -0.5f);

        // draw the near face:
        GL11.glBegin(GL11.GL_POLYGON);
        {
            new Normal(v3.toVector(),v2.toVector(),v2.toVector(),v4.toVector()).submit();
            
            v3.submit();
            v2.submit();
            v1.submit();
            v4.submit();
        }
        GL11.glEnd();

        // draw the right face:
        GL11.glBegin(GL11.GL_POLYGON);
        {
            new Normal(v7.toVector(),v3.toVector(),v4.toVector(),v8.toVector()).submit();
            
        	v7.submit();
            v3.submit();
            v4.submit();
            v8.submit();
        }
        GL11.glEnd();
        
        // draw the far face:
        GL11.glBegin(GL11.GL_POLYGON);
        {
            new Normal(v6.toVector(),v7.toVector(),v8.toVector(),v5.toVector()).submit();
            
            v6.submit();
            v7.submit();
            v8.submit();
            v5.submit();
        }
        GL11.glEnd();
        
        // draw the left face:
        GL11.glBegin(GL11.GL_POLYGON);
        {
            new Normal(v2.toVector(),v6.toVector(),v5.toVector(),v1.toVector()).submit();
            
        	v2.submit();
            v6.submit();
            v5.submit();
            v1.submit();
        }
        GL11.glEnd();

        // draw the top face:
        GL11.glBegin(GL11.GL_POLYGON);
        {
            new Normal(v7.toVector(),v6.toVector(),v2.toVector(),v3.toVector()).submit();
            
            v7.submit();
            v6.submit();
            v2.submit();
            v3.submit();
        }
        GL11.glEnd();

        // draw the bottom face:
        GL11.glBegin(GL11.GL_POLYGON);
        {
            new Normal(v4.toVector(),v1.toVector(),v5.toVector(),v8.toVector()).submit();
            
            v4.submit();
            v1.submit();
            v5.submit();
            v8.submit();
        }
        GL11.glEnd();
    }
    

    // draws a prism as the front of the van
    private void drawUnitPrism2()
    {
        // the vertices for the cuboid
        Vertex v1 = new Vertex(-0.5f, -0.5f,  0.5f);
        Vertex v2 = new Vertex(-0.5f, -0.2f,  0.5f);
        Vertex v3 = new Vertex( 0.0f,  0.1f,  0.5f);
        Vertex v4 = new Vertex( 0.0f, -0.5f,  0.5f);
        Vertex v5 = new Vertex(-0.5f, -0.5f, -0.5f);
        Vertex v6 = new Vertex(-0.5f, -0.2f, -0.5f);
        Vertex v7 = new Vertex( 0.0f,  0.1f, -0.5f);
        Vertex v8 = new Vertex( 0.0f, -0.5f, -0.5f);

        // draw the near face:
        GL11.glBegin(GL11.GL_POLYGON);
        {
            new Normal(v3.toVector(),v2.toVector(),v2.toVector(),v4.toVector()).submit();
            
            v3.submit();
            v2.submit();
            v1.submit();
            v4.submit();
        }
        GL11.glEnd();

        // draw the right face:
        GL11.glBegin(GL11.GL_POLYGON);
        {
            new Normal(v7.toVector(),v3.toVector(),v4.toVector(),v8.toVector()).submit();
            
        	v7.submit();
            v3.submit();
            v4.submit();
            v8.submit();
        }
        GL11.glEnd();

        // draw the far face:
        GL11.glBegin(GL11.GL_POLYGON);
        {
            new Normal(v6.toVector(),v7.toVector(),v8.toVector(),v5.toVector()).submit();
            
            v6.submit();
            v7.submit();
            v8.submit();
            v5.submit();
        }
        GL11.glEnd();

        // draw the left face:
        GL11.glBegin(GL11.GL_POLYGON);
        {
            new Normal(v2.toVector(),v6.toVector(),v5.toVector(),v1.toVector()).submit();
            
            v2.submit();
            v6.submit();
            v5.submit();
            v1.submit();
        }
        GL11.glEnd();
        
        // draw the top face:
        GL11.glBegin(GL11.GL_POLYGON);
        {
            new Normal(v7.toVector(),v6.toVector(),v2.toVector(),v3.toVector()).submit();
            
            v7.submit();
            v6.submit();
            v2.submit();
            v3.submit();
        }
        GL11.glEnd();

        // draw the bottom face:
        GL11.glBegin(GL11.GL_POLYGON);
        {
            new Normal(v4.toVector(),v1.toVector(),v5.toVector(),v8.toVector()).submit();
            
            v4.submit();
            v1.submit();
            v5.submit();
            v8.submit();
        }
        GL11.glEnd();
    }
}
