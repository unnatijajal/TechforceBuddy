package com.techforcebuddybl.services;

import java.util.Optional;

import com.techforcebuddybl.entity.FileEntity;

public interface FileService {
	public Optional<FileEntity> findFileByFileName(String fileName);
}
