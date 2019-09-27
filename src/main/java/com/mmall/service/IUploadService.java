package com.mmall.service;

import org.springframework.web.multipart.MultipartFile;

import com.mmall.common.ServerResponse;

public interface IUploadService {
	public ServerResponse<String> upload(MultipartFile file);
	
	public ServerResponse<String> mutilUpload(MultipartFile file, String temPath);
}
