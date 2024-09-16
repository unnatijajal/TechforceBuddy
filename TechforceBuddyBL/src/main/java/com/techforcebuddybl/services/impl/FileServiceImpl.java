package com.techforcebuddybl.services.impl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.techforcebuddybl.entity.FileEntity;
import com.techforcebuddybl.repo.FileRepository;
import com.techforcebuddybl.services.FileService;

@Service
public class FileServiceImpl implements FileService {

	@Autowired
	private FileRepository fileRepository;
	
	@Override
	public FileEntity findFileByFileName(String fileName) {
		
		return fileRepository.findByFileName(fileName);
	}
	
	

}
