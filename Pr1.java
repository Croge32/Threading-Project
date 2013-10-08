///Name: Cody Rogers
///File: Pr1.java
///Instructor:  Brygg Ullmer
///Class and section:  cs4103 section 1
///LogonID:  cs410332
 
/// Implementation of sobel operator in Java
/// with multi threading
 
import java.util.*;
import java.lang.*;
import javax.imageio.ImageIO;
import java.io.*;
import java.awt.image.BufferedImage;
import java.awt.*;
 
 
public class Pr1 extends Thread
{
    private int sobel_x[][] = new int[][]{
    { 1, 0, -1},
	{ 2, 0, -2},
	{ 1, 0, -1}
    };
 
    private int sobel_y[][] = new int[][]{
	{ 1,  2,  1},
	{ 0,  0,  0},
	{-1, -2, -1}
    };
	
	private static String bmpFile = " ";
	private static String threadStr = "1";
	private static int threadNum;
	private static int h;
	private static int w;
	private static int wThr;
	private static BufferedImage image;
	private static BufferedImage outImage;
	private static BufferedImage combined;
	private static File outFile;
 
    public static int[] inData;
    public static int[] outData;
 
    public static void main(String[] args)
    {
		if(args.length == 0){
	    	bmpFile = "4103.bmp";
	    }
		else if (args.length == 2){
		    bmpFile = args[0];
			threadStr = args[1];
		}
		else if (args.length == 1){
			bmpFile = "4103.bmp";
			threadStr = args[0];
		}
		else{
			bmpFile = args[0];
		}
		
		threadNum = Integer.parseInt(threadStr);
		Pr1 objSobel= new Pr1();
		objSobel.processImage(threadNum);
 
		return;
    }
 
    public void processImage(int threads)
    {
		try{
	    	File imgFile = new File(bmpFile);
	    	image = ImageIO.read(imgFile);
	    
	    	w = image.getWidth();
			wThr = (image.getWidth()/threads);
	    	h = image.getHeight();
 
			inData = new int[wThr*h];
			BufferedImage[] pieces = new BufferedImage[threads];
 
			//instantiate array used for storing pieces of image
			for (int i=0; i<threads; i++){
				pieces[i] = new BufferedImage(wThr, h, BufferedImage.TYPE_BYTE_GRAY);
			}
			
			wThr = pieces[0].getWidth();
			h = pieces[0].getHeight();
 
			//instantiate target image
			combined = new BufferedImage(w, h, BufferedImage.TYPE_BYTE_GRAY);
 
			//split into threads, each one taking a division of the image and running algorithm
	    	Thread[] threadList = new Thread[threads];
			for (int i = 0; i < threadList.length; i++) {
   				image.getRaster().getPixels((i*wThr), 0, wThr, h, inData);	
  				threadList[i] = new Pr1();
   				threadList[i].start();
   				try{
   					threadList[i].join();
   				}catch (InterruptedException ie) {}
 
   				//Write images to pieces and draw pieces individually onto target image
				pieces[i].getRaster().setPixels(0, 0, wThr, h, outData);
 
				Graphics2D g = combined.createGraphics();
				g.drawImage(pieces[i], i*(wThr), 0, null);
				g.dispose();
 
			}
	    		outFile = new File("Sobel_Output.bmp");
	    		ImageIO.write(combined, "BMP", outFile);
		}
		catch(IOException e)
	    {
		// Handle the exception here!
	    }
    }
    
    /// Implement Sobel operator
    public void findEdge(int w, int h)
    {
		int gradient_X = 0;
		int gradient_Y = 0;
		int value = 0;
 
		outData = new int[w*h];
 
		for(int y = 0; y < h; y++)
		{
		    for(int x = 0; x < w; x++)
			{
				//clips coordinates that are outside of the image
				int left = x-1<0 ? 0 : x-1;
				int right = x+1>=w ? w-1 : x+1;
				int top = y-1<0 ? 0 : y-1;
				int bottom = y+1>=h ? h-1 : y+1;
 
			    // Compute gradient in +ve x direction                                     
			    gradient_X = sobel_x[0][0] * inData[ left + top * w ]
				+ sobel_x[0][1] * inData[  x    + top * w ]
				+ sobel_x[0][2] * inData[ right + top * w ]
				+ sobel_x[1][0] * inData[ left +  y    * w ]
				+ sobel_x[1][1] * inData[  x    +  y    * w ]
				+ sobel_x[1][2] * inData[ right +  y    * w ]
				+ sobel_x[2][0] * inData[ left + bottom * w ]
				+ sobel_x[2][1] * inData[  x    + bottom * w ]
				+ sobel_x[2][2] * inData[ right + bottom * w ];
 
		    	// Compute gradient in +ve y direction                                     
		    	gradient_Y = sobel_y[0][0] * inData[ left + top * w ]
				+ sobel_y[0][1] * inData[  x    + top * w ]
				+ sobel_y[0][2] * inData[ right + top * w ]
				+ sobel_y[1][0] * inData[ left +  y    * w ]
				+ sobel_y[1][1] * inData[  x    +  y    * w ]
				+ sobel_y[1][2] * inData[ right +  y    * w ]
				+ sobel_y[2][0] * inData[ left + bottom * w ]
				+ sobel_y[2][1] * inData[  x    + bottom * w ]
				+ sobel_y[2][2] * inData[ right + bottom * w ];
		    
			    value = (int)Math.ceil( Math.sqrt( (gradient_X * gradient_X) +
						       (gradient_Y * gradient_Y)));
			    outData[x+y*w] = 255 - value;
			}
		}
    }
 
	public void run(){
			findEdge(wThr, h);
	}
}