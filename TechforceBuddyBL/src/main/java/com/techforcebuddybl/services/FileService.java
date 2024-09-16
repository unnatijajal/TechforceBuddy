package com.techforcebuddybl.services;

import com.techforcebuddybl.entity.FileEntity;

public interface FileService {
	public FileEntity findFileByFileName(String fileName);
}
