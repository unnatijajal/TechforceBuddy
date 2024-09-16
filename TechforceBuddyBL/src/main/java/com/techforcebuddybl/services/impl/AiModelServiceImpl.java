package com.techforcebuddybl.services.impl;

import org.springframework.stereotype.Service;

import com.techforcebuddybl.services.AiModelService;

import weka.classifiers.functions.SMO;
import weka.core.Instances;

@Service
public class AiModelServiceImpl implements AiModelService {

	private Instances trainingData;
	private SMO model;
	
	
}
