package com.example.socket;


import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;


public class SocketActivity<ReceiveThread> extends Activity implements Callback {
    /** Called when the activity is first created. */
	private static Button rev=null;
	private MediaPlayer mMediaPlayer = null; 
	private SurfaceView mSurfaceView;
	private SurfaceHolder mSurfaceHolder; 
	private static String TAG = "Main";
	private static ImageView mImageSwicther;
	private static int num = 1;
	private static Drawable drawable;
	static Bitmap bit;
	private static Handler handler;
	private static boolean videoisplaying =false;

		
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        mImageSwicther = (ImageView) findViewById(R.id.ImageView); 
        handler=new Handler(){
        	 @Override   
             public void handleMessage(Message msg) { 
        		 switch(msg.what){
        		 	case 0:
		        			 bit = BitmapFactory
								.decodeFile("/sdcard/NetAd/currentupload/mdl"+num +".jpg");
						 drawable = new BitmapDrawable(bit);
						 mImageSwicther.setImageDrawable(drawable); 						
	        			 break;
	        			 
        		 	case 1:
        		 		playVideo("/sdcard/gg.mp4");
        		 }
        		 
             }   
        };  		
        
        mSurfaceView = (SurfaceView) findViewById(R.id.mSurfaceView); //显示动画用的容器 
        
        mSurfaceHolder = mSurfaceView.getHolder(); 
        mSurfaceHolder.addCallback(this); 
        mSurfaceHolder.setFixedSize(176,144); 
        mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS); 

        
        rev=(Button)findViewById(R.id.rev);    
        rev.setText("Playing");
        rev.setOnClickListener(new receiverlistenr());
        
		NetSocket.client_start();
		 new Handler().postDelayed(new Runnable()
 	    {
 	      public void run()
 	      {
 	    	
 	    	 NetSocket.ReceiveThread();

 	      }
 	    }, 2000);
		 

    }
    
    class receiverlistenr implements OnClickListener{
    	public void onClick(View v) {
    		// TODO Auto-generated method stub
    		new Thread(new Runnable(){

				@Override
				public void run() {
					// TODO Auto-generated method stub
					Message msg;
					while(true)
					{
						
						if(num > 10)
							num =1;
						if(videoisplaying == false)
						{
							msg =handler.obtainMessage(1,this); 
							msg.what = 1;
							handler.sendMessage(msg);
							videoisplaying= true;
						}
						try {
							Thread.sleep(2*1000);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						msg =handler.obtainMessage(0,this); 
						msg.what = 0;
						handler.sendMessage(msg);
						num ++;
						
						//has update?
						if(NetSocket.current_status == 1){
							NetSocket.current_status = 0;
						}	
					}
				}
    			
    		}).start();
    		
    	}
    }

    
    private void playVideo(String strPath) 
    {  
    	if(mMediaPlayer == null){
    		mMediaPlayer = new MediaPlayer(); 
    	      mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC); 
    	      mMediaPlayer.setDisplay(mSurfaceHolder); 
    	      
    	      try 
    	      {  
    	        mMediaPlayer.setDataSource(strPath); 
    	      } 
    	      catch (Exception e) 
    	      {  
    	        // TODO Auto-generated catch block 
    	      } 
    	       
    	      try 
    	      {  
    	        mMediaPlayer.prepare(); 
    	      } 
    	      catch (Exception e) 
    	      {  
    	        // TODO Auto-generated catch block 
    	      } 
    	      mMediaPlayer.setOnCompletionListener 
    	      (new MediaPlayer.OnCompletionListener() 
    	      { 
    	        @Override 
    	        public void onCompletion(MediaPlayer arg0) 
    	        { 
    	          // TODO Auto-generated method stub 
    	        	Log.d(TAG,"Mediaplayer is onCompletion");
    	        	mMediaPlayer.release();
    	        	mMediaPlayer = null;
    	        	videoisplaying = false;
    	        } 
    	      }); 
    	}  		
      mMediaPlayer.start();      
    }


	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		
	} 

}