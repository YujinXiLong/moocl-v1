package com.mmall.service.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.mmall.common.ServerResponse;
import com.mmall.service.IUploadService;
import com.mmall.util.FTPUtil_1;
import com.mmall.util.RandomUtil;
@Service("iUploadService")
public class UploadServiceImpl implements IUploadService {
		
		@Value("${FTP_HOST}")
		String host;
		@Value("${FTP_PORT}")
		Integer port;
		@Value("${FTP_USERNAME}")
		String username;
		@Value("${FTP_PASSWORD}")
		String password;
		@Value("${FTP_BASEPATH}")
		String basePath;
		@Value("${FTP_IMAGEURL}")
		String imageURL;
	
		
		
		//该上传方式，直接读到内存，然后上传，如果设定的内存（在springMVC配置中可以设置大小）不够用，就会放到临时硬盘（可以设置目录）；
		public ServerResponse<String> upload(MultipartFile file) {
			String originoName = file.getOriginalFilename();
			String path = new DateTime().toString("/yyyy/MM/dd/");
			String extensionName = originoName.substring(originoName.lastIndexOf(".")+1);
			String newName = RandomUtil.getName() + "." +extensionName;
			try {
				System.out.println(host + "    a   ");
				boolean result = FTPUtil_1.upload(host, port, username, password, basePath, path, newName, file.getInputStream());
				System.out.println(host);
				System.out.println(port);
				System.out.println(username);
				System.out.println(password);
				System.out.println(basePath);
				System.out.println(path);
				System.out.println(newName);
				
				if (!result) {
					return ServerResponse.creatByError("上传失败");
				}
			} catch (IOException e) {
				e.printStackTrace();
				return ServerResponse.creatByError("上传过程中出现异常");
			}
			return ServerResponse.creatBySuccess(imageURL+path+newName);
		}
		
		
		//当服务器的setvlet收到request请求时，发现里面的数据过大，则会缓存到硬盘，比较小时，则保存到内存，当必要大时，tomcat会直接缓存到硬盘。
		//再开始读取硬盘上对应的数据，以流的方式读取到内存，再上传
		public ServerResponse<String> mutilUpload(MultipartFile file,String temPath) {
				if (file == null) {
					return ServerResponse.creatByError("上传文件不能为空");
				}
				//ie下的originName可能为/a/b/a.png
				String originName = file.getOriginalFilename();
				//下面可以解决
				String extenstionName = originName.substring(originName.lastIndexOf(".")+1);
				String newName = RandomUtil.getName()+"." + extenstionName;
				String path = new DateTime().toString("/yyyy/MM/dd/");
				
				File temFilePath = new File(temPath);
				if (!temFilePath.exists()) {
					temFilePath.setWritable(true);
					temFilePath.mkdirs();
				}
				File temFile = new File(temFilePath, newName);
				boolean result;
				try {
					//file表示的数据可能再硬盘上也可能再内存中这个看大小。此时就是把数据以流的方式读到temFile（硬盘中的）中，temFile起到缓冲的作用，但是将读到的直接上传，不好吗？
					//开始上传。
					file.transferTo(temFile);
					System.out.println(host+"      ");
					result = FTPUtil_1.upload(host, port, username, password, basePath, path, newName,new FileInputStream(temFile));
					if (!result) {
						return ServerResponse.creatByError("上传失败");
					}
					temFile.delete();
				} catch (Exception e) {
					e.printStackTrace();
					return ServerResponse.creatByError("上传中出现异常");
				}
				//io流的关闭在FTPUtil_1.upload里面。
				return ServerResponse.creatBySuccess(imageURL+path+newName);
		}

		//当MultipartFile file[]时，FTPUtil_2支持多文件上传,但是他的uploadFileo有问题
		//FTPUtil_2.uploadFile(List<File> fileList)
		
//		public static void main(String[] args) {
//			String dir[] = "/yyyy/MM/dd/".split("/");
//			for (String string : dir) {
//				System.out.println("".equals(string));
//				System.out.println(string);
//			}
//			
//		}
}
