package com.shuffle.vnt.core.security;

import java.io.IOException;

import org.mindrot.jbcrypt.BCrypt;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

public class OneWayPasswordJsonDeserializer extends JsonDeserializer<String> {

	@Override
	public String deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
		return BCrypt.hashpw(p.getValueAsString(), BCrypt.gensalt());
	}

}