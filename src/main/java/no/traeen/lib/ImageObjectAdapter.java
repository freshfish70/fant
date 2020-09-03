package no.traeen.lib;

import no.traeen.lib.resource.Image;

import java.util.Set;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.bind.adapter.JsonbAdapter;

public class ImageObjectAdapter implements JsonbAdapter<Set<Image>, JsonArray> {
	@Override
	public JsonArray adaptToJson(Set<Image> mos) throws Exception {
		JsonArrayBuilder result = Json.createArrayBuilder();
		mos.forEach(mo -> result.add(mo.getId()));
		return result.build();
	}

	@Override
	public Set<Image> adaptFromJson(JsonArray mediaid) throws Exception {
		return null;
	}
}
