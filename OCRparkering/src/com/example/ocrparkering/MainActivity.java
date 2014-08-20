package com.example.ocrparkering;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.googlecode.tesseract.android.TessBaseAPI;

import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.text.method.DateTimeKeyListener;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
public class MainActivity extends Activity{
	
	public final static String ALARM_MESSAGE = "com.example.ocrparking.MESSAGE";
	protected Button _button;
	protected ImageView _image;
	protected TextView _field;
	public static final String DATA_PATH = Environment.getExternalStorageDirectory().toString() + "/OCRparkering/";
	protected String _path;
	protected boolean _taken;
	private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
	private static final String TAG = "MainActivity.java";
	public static final String lang = "eng";
	
	protected static final String PHOTO_TAKEN = "photo_taken";

	@Override
	public void onCreate(Bundle savedInstanceState) {
	 super.onCreate(savedInstanceState);
	 setContentView(R.layout.camerview);
	
	 File folder = new File(DATA_PATH);
	 boolean success = true;
	 if (!folder.exists()) {
	     success = folder.mkdir();
	 }
	 if (success == true) {
		 	
		 _image = ( ImageView ) findViewById( R.id.image );
		 _field = ( TextView ) findViewById( R.id.field );
		 _button = ( Button ) findViewById( R.id.button );
		 _button.setOnClickListener( new CameraHandler() );
		        
		 _path = DATA_PATH + "/ocr.jpg";
		 
		 
	 } else {
		 Log.i( "MakeMachine", "Failed to make folder");
	 }
	 if (!(new File(DATA_PATH + "tessdata/eng.traineddata")).exists()) {
         try {

                 AssetManager assetManager = getAssets();
                 InputStream in = assetManager.open("tessdata/eng.traineddata");
        
                 OutputStream out = new FileOutputStream(DATA_PATH
                                 + "tessdata/eng.traineddata");

              
                 byte[] buf = new byte[1024];
                 int len;
                 
                 while ((len = in.read(buf)) > 0) {
                         out.write(buf, 0, len);
                 }
                 in.close();
                 //gin.close();
                 out.close();
                 
                 
         } catch (IOException e) {
                 Log.e(TAG, "Was unable to copy " + lang + " traineddata " + e.toString());
         }
 }
	 
	 
	}
	public class CameraHandler implements View.OnClickListener {
		public void onClick(View view) {
			// startCameraActivity();
			onPhotoTaken();
		}
	}
	protected void startCameraActivity(){
		File file = new File(_path);
		Uri outputFileUri = Uri.fromFile(file);
		
		final Intent intent1 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		intent1.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
		
		
		Log.i( "MakeMachine", "camera storage done");
		
		startActivityForResult(intent1, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
		
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		Log.i("Camera", "resultcode" +resultCode);
		
			if(resultCode == RESULT_OK){
		onPhotoTaken();			
			
		}
		else{
			Log.i("This failed", "failed on photo");
		}
	}
	
	protected void onPhotoTaken(){
		_taken = true;
		
		 BitmapFactory.Options options = new BitmapFactory.Options();
		 options.inPreferQualityOverSpeed = true;
         options.inSampleSize = 15;
         
		
		Bitmap bitmap = BitmapFactory.decodeFile(_path, options);
		
		
		
		try{
			ExifInterface exif = new ExifInterface(_path);
            int exifOrientation = exif.getAttributeInt(
                            ExifInterface.TAG_ORIENTATION,
                            ExifInterface.ORIENTATION_NORMAL);
            int rotate = 0;
            switch (exifOrientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                    rotate = 90;
                    break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                    rotate = 180;
                    break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                    rotate = 270;
                    break;
            }
            
            Log.i( "MakeMachine", "Interface Rotation Found");
            
            if (rotate != 0) {

                // Getting width & height of the given image.
                int w = bitmap.getWidth();
                int h = bitmap.getHeight();

                // Setting pre rotate
                Matrix mtx = new Matrix();
                mtx.preRotate(rotate);

                // Rotating Bitmap
                bitmap = Bitmap.createBitmap(bitmap, 0, 0, w, h, mtx, false);
                Log.i( "MakeMachine", "BitmapDone");
        }
            
        bitmap = toGrayscale(bitmap);
        // Convert to ARGB_8888, required by tess
        bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
        
        
        
        Log.i( "MakeMachine", "BitmapCopyed");

		}catch(IOException e){
			Log.e(TAG, "correct orientation failed" +e.toString());
		}
		
		
		TessBaseAPI baseApi = new TessBaseAPI();
		Log.i( "MakeMachine", "Tessbase import");
        baseApi.setDebug(true);
        Log.i( "MakeMachine", "Debug true");
        
        baseApi.init(DATA_PATH, lang);
        Log.i( "MakeMachine", "Path and lang");
        baseApi.setImage(bitmap);
        Log.i( "MakeMachine", "Image is set");
        
        String recognizedText = baseApi.getUTF8Text();
        Log.i( "MakeMachine", "String is done");
        
        baseApi.end();
        Log.i( "MakeMachine", "Tessbase DONE");
      
        
        recognizedText = recognizedText.trim();
   
    	
        String re1="((?:(?:[0-1][0-9])|(?:[2][0-3])|(?:[0-9])):(?:[0-5][0-9])?)";

        Pattern p = Pattern.compile(re1,Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
        Matcher m = p.matcher(recognizedText);
        if (m.find())
        {
            String time1=m.group(1);
           
            
            Intent alarm = new Intent(this, AlarmActivity.class);
            alarm.putExtra(ALARM_MESSAGE, time1.toString());
            startActivity(alarm);
        }
        else{
        	Log.i("MakeMachine", "String finder failed");
        }

  
	
	}
	public Bitmap toGrayscale(Bitmap OriginalBitmap)
	{
		int width, height;
		height = OriginalBitmap.getHeight();
		width = OriginalBitmap.getWidth();
		
		Bitmap grayscale = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		Canvas c = new Canvas(grayscale);
		
		Paint paint = new Paint();
		ColorMatrix cm = new ColorMatrix();
		
		cm.setSaturation(0);
		ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
		paint.setColorFilter(f);
		
		c.drawBitmap(OriginalBitmap, 0, 0, paint);
		
		return grayscale;
	}
	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		Log.i( "MakeMachine", "onRestoreInstanceState()");
    	if( savedInstanceState.getBoolean( MainActivity.PHOTO_TAKEN ) ) {
    		onPhotoTaken();
    	}

	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		outState.putBoolean( MainActivity.PHOTO_TAKEN, _taken );
    }

	
	
	
}
