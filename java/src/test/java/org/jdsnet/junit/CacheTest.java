package org.jdsnet.junit;

import java.io.IOException;

import org.jdsnet.arangodb.cache.railo.ArangoDBCache;
import org.jdsnet.arangodb.util.CommonSerializer;

import railo.runtime.type.KeyImpl;
import railo.runtime.type.StructImpl;

public class CacheTest {

	protected ArangoDBCache cache;
	
	public CacheTest() throws IOException {
		StructImpl arguments = new StructImpl();
		
		arguments.put(KeyImpl.getInstance("database"),getDatabase());
		
		cache = new ArangoDBCache("Cache", arguments);
		cache.setSerializer(new CommonSerializer());
	}
	
	public void cleanup() throws Throwable {
		cache.clear();
		cache.getConnection().deleteDatabase(getDatabase());
		cache.close();
	}
	
	protected String getDatabase() {
		return null;
	}
	
}