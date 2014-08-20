package com.example.ocrparkering;


import android.app.Activity;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.FrameLayout;

public class CameraActivity extends Activity  {
	
	 private Camera mCamera;
	 private CameraPreview mPreview;
	
	 @Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		   setContentView(R.layout.camerapreview);

	        // Create an instance of Camera
	        mCamera = getCameraInstance();

	        // Create our Preview view and set it as the content of our activity.
	        mPreview = new CameraPreview(this, mCamera);
	        FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
	        preview.addView(mPreview);
	}
	 
	public static Camera getCameraInstance()
	{
		Camera c = null;
		
		try{
			c = Camera.open();
			
		}catch (Exception e )
		{
			Log.e("Camera", "Cant start camera instance");
		}
		
		return c;
	}
	
	
}
