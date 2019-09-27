package com.mmall.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
//Positive
//Completion
//reply
//
//正
//完成
//答复
public class FTPUtil_1 {
	public static boolean upload(String host,int port,String user,String password,String basePath,String filePath,String fileName,InputStream in) {
		FTPClient ftpClient = new FTPClient();
		boolean result = false;
		try {
			int reply ;
			
			if(!ftpClient.isConnected()){
				ftpClient.connect(host, port);
				System.out.println(ftpClient.isConnected());
			}
			System.out.println(ftpClient.login(user, password));
			System.out.println("asd");
			reply = ftpClient.getReplyCode();
			System.out.println(reply);
			if (!FTPReply.isPositiveCompletion(reply)) {
				return result;
			}
			
			//切换到上传目录basePath+filePath 该目录可能不存在,但是basePath目录一定要存在，这个目录是从服务器拿过来的。
			// /home/ftpuser/image =basePath，a.txt=filePath
			//basePath+filePath就等于home/ftpuser/imagea.txt，所以出错
			//String a = "a.tet";
			//String b[] = a.split("/");
			//System.out.println(b[0]);legth为0，结果a.tet
			System.out.println("sadsa");
			ftpClient.enterLocalPassiveMode();  //被动模式
			if (!ftpClient.changeWorkingDirectory(basePath+filePath)) {
				String dirs[] = filePath.split("/");
				StringBuffer temPathBuffer= new StringBuffer(basePath);
				
				
				
//				for(String dir:dirs) {
//					if (dir!=null && !"".equals(dir)){
//						temPathBuffer.append("/").append(dir);
//					}
//				}
//				String temPath = temPathBuffer.toString();
//				System.out.println(basePath+temPath);
//				if (!ftpClient.changeWorkingDirectory(basePath+temPath)) {
//					//创建目录不成功，可能是用户权限不够
//					boolean makeResult = ftpClient.makeDirectory(basePath+temPath);//创建的目录必须是完整目录,还要助剂创建才行，不然目录创建不出来
//					
//					System.out.println(makeResult+"sadsad");
//					if (!makeResult) {
//						return result;
//					}
//					//切换到该目录下
//					ftpClient.changeWorkingDirectory(basePath+temPath);
//				}
				
				//创建的目录必须是完整目录,还要助剂创建才行，不然目录创建不出来
				for(String dir:dirs) {
					if (dir!=null && !"".equals(dir)){
						temPathBuffer.append("/").append(dir);
					}
					if (!ftpClient.changeWorkingDirectory(temPathBuffer.toString())) {
						//创建目录不成功，可能是用户权限不够
						boolean makeResult = ftpClient.makeDirectory(temPathBuffer.toString());//创建的目录必须是完整目录,还要助剂创建才行，不然目录创建不出来
						System.out.println(makeResult+"sadsad");
						if (!makeResult) {
							return result;
						}
						//切换到该目录下
						ftpClient.changeWorkingDirectory(temPathBuffer.toString());
					}
				}
				
				
				
				
			}
			//此时basePath+filePath存在了
			ftpClient.setControlEncoding("UTF-8");
			ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
			//开始上传
			if (!ftpClient.storeFile(fileName, in)) {
				System.out.println("上传失败");
				return result;
			}
			in.close();
			ftpClient.logout();//断开链接
			result = true;
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			//出现异常，in不用关吗，
			if (ftpClient.isConnected()) {
					try {
						ftpClient.disconnect();//断开链接
					} catch (IOException e) {
						e.printStackTrace();
					}
			}
			
		}
		return result;
	}
	
	public static void main(String[] args) {
		
			try {
				FileInputStream in=new FileInputStream(new File("C:\\Users\\ASUS\\Pictures\\Screenshots\\新建位图图像3.png"));  
				System.out.println(upload("192.168.195.130", 21, "ftpuser", "ftpuser", "/home/ftpuser/image","/2019/09/03", "C.png", in));
			} catch (Exception e) {												// /home/ftpuser/image	///2016/03/21
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}
}
