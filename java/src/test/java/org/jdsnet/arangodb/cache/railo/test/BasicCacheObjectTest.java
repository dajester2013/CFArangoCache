package org.jdsnet.arangodb.cache.railo.test;

import java.io.IOException;

import static junit.framework.Assert.*;

import org.jdsnet.arangodb.cache.railo.ArangoDBCache;
import org.jdsnet.arangodb.util.CommonSerializer;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import at.orz.arangodb.entity.CollectionEntity;
import railo.runtime.type.KeyImpl;
import railo.runtime.type.StructImpl;

public class BasicCacheObjectTest {

	private static ArangoDBCache cache;
	
	@BeforeClass
	public static void setUp() throws IOException {
		StructImpl arguments = new StructImpl();
		
		arguments.put(KeyImpl.getInstance("database"), "test");
		
		cache = new ArangoDBCache("Cache", arguments);
		cache.setSerializer(new CommonSerializer());
	}

	@AfterClass
	public static void tearDown() {
		cache.close();
	}
	
	@Test
	public void TestCacheIsSetup() {
		assertTrue("Expected to get the collection instance.",cache.getCollection() instanceof CollectionEntity);
	}
	
}
