package org.jdsnet.arangodb.cache.railo.test;

import static junit.framework.Assert.*;

import java.io.IOException;

import org.jdsnet.annotation.Order;
import org.jdsnet.arangodb.cache.railo.ArangoDBCache;
import org.jdsnet.arangodb.util.CommonSerializer;
import org.jdsnet.junit.OrderedRunner;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import at.orz.arangodb.ArangoException;
import railo.commons.io.cache.CacheEntry;
import railo.runtime.type.KeyImpl;
import railo.runtime.type.StructImpl;

@RunWith(OrderedRunner.class)
public class BasicCacheObjectTest {
	
	public BasicCacheObjectTest() throws IOException {super();}
	
	private static String dbname = "BCO_Test";
	private static ArangoDBCache cache;

	@BeforeClass
	public static void setUp() throws IOException {
		StructImpl arguments = new StructImpl();
		
		arguments.put(KeyImpl.getInstance("database"),dbname);
		if (cache == null) {
			cache = new ArangoDBCache("Cache", arguments);
			cache.setSerializer(new CommonSerializer());
		}
	}
	@AfterClass
	public static void tearDown() throws IOException, ArangoException {
		cache.clear();
		cache.getConnection().deleteDatabase(dbname);
		cache.close();
	}
	
	@Test
	@Order(order = 1)
	public void VerifyCache() throws Throwable {
		cache.verify();
	}
	
	@Test
	@Order(order = 2)
	public void TestCacheStoreObject() throws Throwable {
		String key = "key";
		String value = "value";
		
		cache.put(key, value, 0L, 0L);
		assertEquals(1L,cache.getConnection().getCollectionCount(cache.getCacheName()).getCount());
		assertTrue(cache.getCacheEntry(key) instanceof CacheEntry && cache.getCacheEntry(key) != null);
		assertEquals(value,cache.getCacheEntry(key).getValue());
	}
	
}
