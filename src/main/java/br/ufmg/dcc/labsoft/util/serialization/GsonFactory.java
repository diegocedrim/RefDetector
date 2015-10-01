package br.ufmg.dcc.labsoft.util.serialization;

import gr.uom.java.xmi.diff.Refactoring;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class GsonFactory {
	
	private Gson gson;

	private static GsonFactory singleton;
	
	static {
		singleton = new GsonFactory();
	}
	
	private GsonFactory() {
		GsonBuilder builder = new GsonBuilder();
		builder.setExclusionStrategies(new CustomGsonExclusionStrategy());
		builder.registerTypeAdapter(Refactoring.class, new RefactoringSerializer());
		builder.serializeNulls();
		this.gson = builder.create();
	}
	
	public static GsonFactory getInstance() {
		return singleton;
	}
	
	public Gson create() {
		return gson;
	}
}
