package br.ufmg.dcc.labsoft.util.serialization;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;

public class CustomGsonExclusionStrategy implements ExclusionStrategy {

	@Override
	public boolean shouldSkipClass(Class<?> clazz) {
		return false;
	}

	@Override
	public boolean shouldSkipField(FieldAttributes field) {
		return field.getAnnotation(JsonIgnore.class) != null;
	}

}
