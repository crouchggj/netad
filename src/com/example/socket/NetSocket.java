package com.example.socket;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import com.example.network.FtpUnit;

import android.os.Handler;
import android.util.Log;

public class NetSocket {
	static enum Status{
		IDLE,
		GETOK,
		DOWNOK,
		DOWNERRO
	}
	static String message;
	static PrintWriter out;
	static InputStream  in;
	static Socket client = null;
	static byte[] buf = new byte[100]; 
	static String receive_info = null;
	protected static Status status = Status.IDLE;  //update info
	static String split_info[] = null;  
	static int current_status = 0;  //0 is keep now 1 is Update
    public static void client_start()
    {
    	new Thread(new Runnable(){
    		public void run(){
    			String msg;
    			try {
    				client = new Socket("192.168.1.68", 10000);
    			} catch (UnknownHostException e) {
    				e.printStackTrace();
    			} catch (IOException e) {
    				e.printStackTrace();
    			}
   
				try {
					message = "seuic_022";
					Log.d("TCP", "C: Sending: '" + message + "'");
					// 获取 Client 端的输出流
					out = new PrintWriter(client.getOutputStream());
					in = client.getInputStream();	
					out.println(message);
					out.flush();
					
				} catch (Exception e) {
					Log.e("TCP", "Socket: Error", e);
				} 

				while(true)
				{
					if (status == Status.GETOK) {
						out.println("GETOK" + "," + split_info[2]);
						out.flush();
						status = Status.IDLE;
					} else if (status == Status.DOWNOK) {
						out.println("DOWNOK" + "," + split_info[2]);
						out.flush();
						status = Status.IDLE;
					} else if (status == Status.DOWNERRO) {
						out.println("DOWNERRO" + "," + split_info[2]);
						out.flush();
						status = Status.IDLE;
					} else {
						out.println("ACK");
						out.flush();
					}

					try {
						Thread.sleep(2000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			
				}

    	}
    		
     ).start();
    	    	
    }



    
    
    public static void ReceiveThread()
    {
    	new Thread(new Runnable(){

			@Override
			public void run() {
				// TODO Auto-generated method stub
				while(true)
				{
					try {
						int rc = in.read(buf);
						if( rc > 0){
							receive_info = new String(buf);
							//Log.d("Socket Rec","Msg:" + new String(buf)+"rc:"+rc);
							status  = Status.GETOK;
							split_info = receive_info.split(",");
							
							new Thread(new Runnable(){

								@Override
								public void run() {
									// TODO Auto-generated method stub
									Log.d("NetSocket","Remote:guang/"+split_info[0]);
									if(FtpUnit.loadFile("guang/"+split_info[0],"ggj.zip") == 0){
										status  = Status.DOWNOK;
										current_status = 1;
										
									}else
										status = Status.DOWNERRO;
								}
				    			
				    		}).start();
							Log.d("Socket Receive","Msg:" + receive_info);
						}
							
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
    	
    	}
    	).start();
    	
    }
    
}
