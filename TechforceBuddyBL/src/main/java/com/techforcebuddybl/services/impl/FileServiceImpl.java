package com.techforcebuddybl.services.impl;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.techforcebuddybl.entity.FileEntity;
import com.techforcebuddybl.services.FileService;

@Service
public class FileServiceImpl implements FileService {

	@Override
	public Optional<FileEntity> findFileByFileName(String fileName) {
		// TODO Auto-generated method stub
		return Optional.empty();
	}

}
