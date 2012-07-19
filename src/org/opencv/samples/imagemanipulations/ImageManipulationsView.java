package org.opencv.samples.imagemanipulations;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.highgui.Highgui;
import org.opencv.highgui.VideoCapture;
import org.opencv.imgproc.Imgproc;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.Display;
import android.view.SurfaceHolder;
import android.view.WindowManager;
import de.fhbocholt.campuswoche.ioio.IOIOControl;
import de.fhbocholt.campuswoche.ioio.ZapfControl;

class ImageManipulationsView extends SampleCvViewBase {

    
    private Mat mRgba;
    private Mat mGray;
    private Mat mIntermediateMat;

    private int mHistSizeNum;

    float mBuff[];

    private Mat mRgbaInnerWindow;
    private Mat mGrayInnerWindow;
    private Mat mBlurWindow;
    private Mat mZoomWindow;
    private Mat mZoomCorner;
    
    private Size maxSize;
    
    private ZapfControl zapfanlage = null;
//

    private ZapfControl getZapfanlage(){
    	if(null == zapfanlage){
    		this.zapfanlage = new IOIOControl();
    	}
    	return this.zapfanlage;
    }
    
    public ImageManipulationsView(Context context) {
        super(context);
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        maxSize= new Size (display.getWidth(),display.getHeight());
    }

    @Override
	public void surfaceCreated(SurfaceHolder holder) {
        synchronized (this) {
            // initialize Mats before usage
            mGray = new Mat();
            mRgba = new Mat();
            mIntermediateMat = new Mat();           
            mHistSizeNum = 25;
            mBuff = new float[mHistSizeNum];
        }

        super.surfaceCreated(holder);
	}

    @Override
    protected Bitmap processFrame(VideoCapture capture) {

//        	capture.retrieve(mRgba, Highgui.CV_CAP_ANDROID_COLOR_FRAME_RGBA);
//        	
//        	if(ImageManipulationsActivity.initNow == 1)
//        	{
//        		
//        		base=mRgba.clone();
//        		ImageManipulationsActivity.initNow = 0;
//        		//framecounter = 0;
//        	}
//        	//framecounter ++;
//        	
//            Core.absdiff(mRgba, base, mRgba);
//            
//            Imgproc.cvtColor(mRgba, mRgba, Imgproc.COLOR_BGRA2GRAY, 4);
//            Imgproc.threshold(mRgba, mRgba, 75, 255, Imgproc.THRESH_BINARY);
        	
        	
//        	capture.retrieve(mRgba, Highgui.CV_CAP_ANDROID_COLOR_FRAME_RGBA);
//        	Imgproc.cvtColor(mRgba, mRgba, Imgproc.COLOR_BGRA2GRAY, 4);
    	
        	capture.retrieve(mRgba, Highgui.CV_CAP_ANDROID_GREY_FRAME);
        	Imgproc.resize(mRgba, mRgba, new Size(40,30));
 
            Imgproc.Canny(mRgba, mRgba, ImageManipulationsActivity.t1,ImageManipulationsActivity.t2);

            Mat lines = new Mat();
            int threshold = 3;
            int minLineSize = 5;
            int lineGap = 3;

            //Imgproc.HoughLines(mIntermediateMat,lines,1,Math.PI/180, 10);
            Imgproc.HoughLinesP(mRgba, lines, 1, Math.PI, threshold, minLineSize, lineGap);

            final int MAX = 999;
            Point start = new Point(MAX,MAX); 
            Point end = new Point(MAX,MAX);
            for (int x = 0; x < lines.cols(); x++)
            {
                  double[] vec = lines.get(0, x);
            	  if(vec[0]<start.x)
            	  {
            		  start.x =	vec[0];
            		  start.y =	vec[1];
            		  end.x	  =	vec[2];
            		  end.y   =	vec[3];
            	  }
            }
            Imgproc.cvtColor(mRgba, mRgba, Imgproc.COLOR_GRAY2BGRA, 4);
            if(start.x!=MAX)
            	Core.line(mRgba, start, end, new Scalar(255,0,0), 1);
            
            Imgproc.resize(mRgba, mRgba, maxSize);
            
        Bitmap bmp = Bitmap.createBitmap(mRgba.cols(), mRgba.rows(), Bitmap.Config.ARGB_8888);

        try {
        	Utils.matToBitmap(mRgba, bmp);
            return bmp;
        } catch(Exception e) {
        	Log.e("org.opencv.samples.puzzle15", "Utils.matToBitmap() throws an exception: " + e.getMessage());
            bmp.recycle();
            return null;
        }
    }
    
    public void zapfen(){
    	this.getZapfanlage().starteZapfen();
    }
    
    public void drehe(){
    	this.getZapfanlage().starteDrehen();
    }
    
    public void stoppeZapfen(){
    	this.getZapfanlage().stoppeZapfen();
    }
    
    public void stoppeDrehen(){
    	this.getZapfanlage().stoppeDrehen();
    }

    @Override
    public void run() {
        super.run();

        synchronized (this) {
            // Explicitly deallocate Mats
            if (mZoomWindow != null)
                mZoomWindow.release();
            if (mZoomCorner != null)
                mZoomCorner.release();
            if (mBlurWindow != null)
                mBlurWindow.release();
            if (mGrayInnerWindow != null)
                mGrayInnerWindow.release();
            if (mRgbaInnerWindow != null)
                mRgbaInnerWindow.release();
            if (mRgba != null)
                mRgba.release();
            if (mGray != null)
                mGray.release();
            if (mIntermediateMat != null)
                mIntermediateMat.release();

            mRgba = null;
            mGray = null;
            mIntermediateMat = null;
            mRgbaInnerWindow = null;
            mGrayInnerWindow = null;
            mBlurWindow = null;
            mZoomCorner = null;
            mZoomWindow = null;
        }
    }
}
