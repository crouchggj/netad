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
         * ����Ftp������
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
     * �ϴ��ļ�
     * @param localFilePath--�����ļ�·��
     * @param newFileName--�µ��ļ���
    */
   public void uploadFile(String localFilePath,String newFileName){
        //connectServer();
       //�ϴ��ļ�
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
    * �����ļ�
    * @param remoteFileName --�������ϵ��ļ���
    * @param localFileName--�����ļ���
   */

public static  int loadFile(String remoteFileName,String localFileName){
       if(connectServer() == 1)
    	   return 1; //connect is Error
       
       System.out.println("==============="+localFileName);
      //�����ļ�
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
     * ����FTP�ͷ��˵�����--һ����Բ�����
     * @return
     */
   private static FTPClientConfig getFtpConfig(){
        FTPClientConfig ftpConfig=new FTPClientConfig(FTPClientConfig.SYST_UNIX);
        ftpConfig.setServerLanguageCode(FTP.DEFAULT_CONTROL_ENCODING);
       return ftpConfig;
    }
   
   /**
    * �ر�����
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
    * ��ѹ��Zip�ļ�
    * 
    * 
    * */
   private static int Unzip(String zipFile, String targetDir) {
	   int BUFFER = 4096; //���ﻺ��������ʹ��4KB��
	   String strEntry; //����ÿ��zip����Ŀ����

	   try {
	    BufferedOutputStream dest = null; //���������
	    FileInputStream fis = new FileInputStream(zipFile);
	    ZipInputStream zis = new ZipInputStream(new BufferedInputStream(fis));
	    ZipEntry entry; //ÿ��zip��Ŀ��ʵ��
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