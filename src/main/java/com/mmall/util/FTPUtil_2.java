package com.mmall.util;

import org.apache.commons.net.ftp.FTPClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.mmall.util.PropertiesUtil;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
public class FTPUtil_2 {
	//测试报错，设置了被动模式
	 public static void main(String[] args) {
			try {  
		        List<File> fils = new ArrayList<File>();
		        fils.add(new File("C:\\Users\\ASUS\\Pictures\\Screenshots\\A.png"));
		        boolean flag = uploadFile(fils);  
		        System.out.println(flag);  
		    } catch (Exception e) {  
		        e.printStackTrace();  
		    }  
	 }


	    private static  final Logger logger = LoggerFactory.getLogger(FTPUtil_2.class);

	    private static String ftpIp = PropertiesUtil.getProperty("FTP_HOST");
	    private static String ftpUser = PropertiesUtil.getProperty("FTP_USERNAME");
	    private static String ftpPass = PropertiesUtil.getProperty("FTP_PASSWORD");

	    public FTPUtil_2(String ip,int port,String user,String pwd){
	        this.ip = ip;
	        this.port = port;
	        this.user = user;
	        this.pwd = pwd;
	    }
	    public static boolean uploadFile(List<File> fileList) throws IOException {
	        FTPUtil_2 ftpUtil = new FTPUtil_2(ftpIp,21,ftpUser,ftpPass);
	        logger.info("开始连接ftp服务器");
	        boolean result = ftpUtil.uploadFile("/home/ftpuser/image",fileList);
	        logger.info("开始连接ftp服务器,结束上传,上传结果:{}");
	        return result;
	    }


	    private boolean uploadFile(String remotePath,List<File> fileList) throws IOException {
	        boolean uploaded = true;
	        FileInputStream fis = null;
	        //连接FTP服务器
	        if(connectServer(this.ip,this.port,this.user,this.pwd)){
	            try {
	                ftpClient.changeWorkingDirectory(remotePath);
	                ftpClient.setBufferSize(1024);
	                ftpClient.setControlEncoding("UTF-8");
	                ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
	                ftpClient.enterLocalPassiveMode();
	                for(File fileItem : fileList){
	                    fis = new FileInputStream(fileItem);
	                    ftpClient.storeFile(fileItem.getName(),fis);
	                }

	            } catch (IOException e) {
	                logger.error("上传文件异常",e);
	                uploaded = false;
	                e.printStackTrace();
	            } finally {
	                fis.close();
	                ftpClient.disconnect();
	            }
	        }
	        return uploaded;
	    }



	    private boolean connectServer(String ip,int port,String user,String pwd){

	        boolean isSuccess = false;
	        ftpClient = new FTPClient();
	        try {
	            ftpClient.connect(ip);
	            isSuccess = ftpClient.login(user,pwd);
	        } catch (IOException e) {
	            logger.error("连接FTP服务器异常",e);
	        }
	        return isSuccess;
	    }











	    private String ip;
	    private int port;
	    private String user;
	    private String pwd;
	    private FTPClient ftpClient;

	    public String getIp() {
	        return ip;
	    }

	    public void setIp(String ip) {
	        this.ip = ip;
	    }

	    public int getPort() {
	        return port;
	    }

	    public void setPort(int port) {
	        this.port = port;
	    }

	    public String getUser() {
	        return user;
	    }

	    public void setUser(String user) {
	        this.user = user;
	    }

	    public String getPwd() {
	        return pwd;
	    }

	    public void setPwd(String pwd) {
	        this.pwd = pwd;
	    }

	    public FTPClient getFtpClient() {
	        return ftpClient;
	    }

	    public void setFtpClient(FTPClient ftpClient) {
	        this.ftpClient = ftpClient;
	    }
	    
	  
	    
}


