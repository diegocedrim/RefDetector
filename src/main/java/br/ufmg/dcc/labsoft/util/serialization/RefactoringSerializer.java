package br.ufmg.dcc.labsoft.util.serialization;

import gr.uom.java.xmi.diff.Refactoring;

import java.lang.reflect.Type;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class RefactoringSerializer implements JsonSerializer<Refactoring> {

	@Override
	public JsonElement serialize(Refactoring object, Type type, JsonSerializationContext context) {
		JsonObject element = (JsonObject)context.serialize(object);
		element.addProperty("type", object.getRefactoringType().getDisplayName());
		return element;
	}

}
