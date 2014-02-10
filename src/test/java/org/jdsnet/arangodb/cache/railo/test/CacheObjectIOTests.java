package org.jdsnet.arangodb.cache.railo.test;

import static junit.framework.Assert.*;

import java.io.IOException;
import java.util.Date;

import org.jdsnet.annotation.Order;
import org.jdsnet.arangodb.cache.railo.ArangoDBCache;
import org.jdsnet.arangodb.cache.railo.ArangoDBCacheEntry;
import org.jdsnet.arangodb.util.CommonSerializer;
import org.jdsnet.junit.OrderedRunner;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import at.orz.arangodb.ArangoException;
import railo.commons.io.cache.CacheEntry;
import railo.runtime.type.KeyImpl;
import railo.runtime.type.Struct;
import railo.runtime.type.StructImpl;

@RunWith(OrderedRunner.class)
public class CacheObjectIOTests {

	private static String dbname = "CoIO_test";
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
	public void TestCachePut() throws Throwable {
		StructImpl test = new StructImpl();
		test.put("asdf", "fdsa");
		for (int i=0; i < 3000; i++) {
			cache.put("test-entry"+i, test, 0L, 0L);
		}
	}
	
	@Test
	@Order(order = 2)
	public void TestCacheSize() throws Throwable {
		assertEquals("Expected 3000 entries.", 3000, cache.keys().size());
	}

	@Test
	@Order(order = 3)
	public void TestHitCounts() throws Throwable {
		ArangoDBCacheEntry e = null;
		for (int i=0; i < 10; i++) {
			e = (ArangoDBCacheEntry)cache.getCacheEntry("test-entry1");
		}
		
		assertEquals("Expected 10 hits on the document.",10,e.getDocument().getHits());
		assertEquals("Expected 10 hits on the cache.",10,cache.hitCount());
		assertEquals("Expected 0 misses on the cache.",0,cache.missCount());
	}
	

	@Test
	@Order(order = 4)
	public void TestHitExistsAndRemove() throws Throwable {
		for (int i=0; i < 10; i++) {
			assertTrue(cache.contains("test-entry"+i));
			assertTrue(cache.remove("test-entry"+i));
			assertFalse(cache.contains("test-entry"+i));
		}
	}
	
	@Test(expected = IOException.class)
	@Order(order = 5)
	public void TestMiss() throws IOException {
		cache.getCacheEntry("test-entry0");
	}
	
	@Test
	@Order(order = 6)
	public void TestMissWithDefault() throws IOException {
		CacheEntry e = new CacheEntry() {
			@Override
			public long size() {return 0;}
			@Override
			public long liveTimeSpan() {return 0;}
			@Override
			public Date lastModified() {return null;}
			@Override
			public Date lastHit() {return null;}
			@Override
			public long idleTimeSpan() {return 0;}
			@Override
			public int hitCount() {return 0;}
			@Override
			public Object getValue() {return "Default Value";}
			@Override
			public String getKey() {return null;}
			@Override
			public Struct getCustomInfo() {return null;}
			@Override
			public Date created() {return null;}
		};
		assertEquals("Expected the passed default value.",e,cache.getCacheEntry("test-entry0",e));
	}
	
	@Test
	@Order(order = 7)
	public void TestMissCount() throws IOException {
		assertEquals("Expected 2 misses.",2,cache.missCount());
	}
	
}
	