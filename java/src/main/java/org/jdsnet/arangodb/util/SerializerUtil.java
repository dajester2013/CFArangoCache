package org.jdsnet.arangodb.util;

import railo.runtime.exp.PageException;

public interface SerializerUtil {
	
	public String serialize(Object obj) throws Throwable;
	
	public String serializeJSON(Object obj, boolean serializeQueryByColumns) throws Throwable;
	
	public Object deserialize(Object obj) throws Throwable;
	
	public Object deserializeJSON(String obj) throws Throwable;
	
}
