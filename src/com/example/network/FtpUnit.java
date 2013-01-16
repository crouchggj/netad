package com.example.network;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;



import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPClientConfig;
import org.apache.commons.net.ftp.FTPReply;
//import org.apache.tools.zip.ZipEntry;
//import org.apache.tools.zip.ZipFile;

import android.os.Environment;
import android.util.Log;

public class FtpUnit {
        private static FTPClient ftpClient = null;
        private static String SDPATH;
        private static String TAG = "FTPUnit";
        public FtpUnit(){
                SDPATH =Environment.getExternalStorageDirectory()+"/";
        }
        
        /**
         * 连接Ftp服务器
         */
        public static  int connectServer(){
                if(ftpClient == null){
                        int reply;
                        try{
                        		SDPATH =Environment.getExternalStorageDirectory()+"/";
                                ftpClient = new FTPClient();
                                ftpClient.setDefaultPort(21);
                                ftpClient.configure(getFtpConfig());
                                ftpClient.connect("192.168.1.68",21);
                                ftpClient.login("anonymous","");
                                ftpClient.setDefaultPort(21);  
                                ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
                                reply = ftpClient.getReplyCode();
                                Log.d(TAG,"reply:" + reply+"----");
				                if (!FTPReply.isPositiveCompletion(reply)) {
				                     ftpClient.disconnect();
				                     Log.d(TAG,"FTP server refused connection.");
				                 }
				                ftpClient.enterLocalPassiveMode();
				                ftpClient.setControlEncoding("gbk");
				                return 0;        
                        }catch(Exception e){
                                e.printStackTrace();
                                return 1;
                        }
                }
				return 0;

        }
        
        /**
     * 上传文件
     * @param localFilePath--本地文件路径
     * @param newFileName--新的文件名
    */
   public void uploadFile(String localFilePath,String newFileName){
        //connectServer();
       //上传文件
        BufferedInputStream buffIn=null;
       try{
            buffIn=new BufferedInputStream(new FileInputStream(SDPATH+"/"+localFilePath));
            System.out.println(SDPATH+"/"+localFilePath);
            System.out.println("start="+System.currentTimeMillis());
            ftpClient.setFileType(FTP.ASCII_FILE_TYPE);
            ftpClient.storeFile("a1.mp3", buffIn);
            System.out.println("end="+System.currentTimeMillis());
        }catch(Exception e){
            e.printStackTrace();
        }finally{
           try{
               if(buffIn!=null)
                    buffIn.close();
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }
   
   /**
    * 下载文件
    * @param remoteFileName --服务器上的文件名
    * @param localFileName--本地文件名
   */

public static  int loadFile(String remoteFileName,String localFileName){
       if(connectServer() == 1)
    	   return 1; //connect is Error
       
       System.out.println("==============="+localFileName);
      //下载文件
       BufferedOutputStream buffOut=null;
		try {
			buffOut = new BufferedOutputStream(new FileOutputStream(SDPATH
					+ localFileName));
			long start = System.currentTimeMillis();
			ftpClient.retrieveFile(remoteFileName, buffOut);
			long end = System.currentTimeMillis();
			System.out.println(end - start);
			buffOut.flush();
			Log.d(TAG, "Unzip:" + SDPATH + localFileName);
			if(Unzip(SDPATH + localFileName, SDPATH + "/NetAd/current") == 1)
			{
				Log.d(TAG, "Unzip is Erro");
				return 1;
			}
			Log.d(TAG, "Unzip is OK");

		} catch (Exception e) {
			e.printStackTrace();
			return 1;
		}

		try {
			if (buffOut != null)
				buffOut.close();
			if(closeConnect() == 1)
				return 1;
			return 0;
		} catch (Exception e) {
			e.printStackTrace();
			return 1;
		}
       
   }
  
        /**
     * 设置FTP客服端的配置--一般可以不设置
     * @return
     */
   private static FTPClientConfig getFtpConfig(){
        FTPClientConfig ftpConfig=new FTPClientConfig(FTPClientConfig.SYST_UNIX);
        ftpConfig.setServerLanguageCode(FTP.DEFAULT_CONTROL_ENCODING);
       return ftpConfig;
    }
   
   /**
    * 关闭连接
   */
  public static  int closeConnect(){
      try{
          if(ftpClient!=null){
               ftpClient.logout();
               ftpClient.disconnect();
           }
          return 0;
       }catch(Exception e){
           e.printStackTrace();
           return 1;
       }
   }

   /*
    * 解压缩Zip文件
    * 
    * 
    * */
   private static int Unzip(String zipFile, String targetDir) {
	   int BUFFER = 4096; //这里缓冲区我们使用4KB，
	   String strEntry; //保存每个zip的条目名称

	   try {
	    BufferedOutputStream dest = null; //缓冲输出流
	    FileInputStream fis = new FileInputStream(zipFile);
	    ZipInputStream zis = new ZipInputStream(new BufferedInputStream(fis));
	    ZipEntry entry; //每个zip条目的实例
	    while ((entry = zis.getNextEntry()) != null) {

	     try {
	       //Log.i("Unzip: ","="+ entry);
	      int count; 
	      byte data[] = new byte[BUFFER];
	      strEntry = entry.getName();

	      File entryFile = new File(targetDir + strEntry);
	      //Log.d(TAG,"entryFile:" + targetDir + strEntry);
	      if(entryFile.isDirectory()){
	    	  if(!entryFile.exists())
	    		  entryFile.mkdirs();
	      }else{
	    	  File entryDir = new File(entryFile.getParent());
		      if (!entryDir.exists()) {
		       entryDir.mkdirs();
		      }
		      FileOutputStream fos = new FileOutputStream(entryFile);
		      dest = new BufferedOutputStream(fos, BUFFER);
		      while ((count = zis.read(data, 0, BUFFER)) != -1) {
		       dest.write(data, 0, count);
		      }
		      dest.flush();
		      dest.close();
	      }
	      
	     } catch (Exception ex) {
	      ex.printStackTrace();
	      return 1;
	     }
	    }
	    zis.close();
	   } catch (Exception cwj) {
	    cwj.printStackTrace();
	    return 1;
	   }
	   return 0;
	  }

  
}