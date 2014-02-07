package org.jdsnet.arangodb.cache.railo.test;

import static junit.framework.Assert.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.jdsnet.annotation.Order;
import org.jdsnet.arangodb.cache.railo.ArangoDBCacheEntry;
import org.jdsnet.junit.CacheTest;
import org.jdsnet.junit.OrderedRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import at.orz.arangodb.util.MapBuilder;
import railo.commons.io.cache.CacheEntry;
import railo.runtime.text.xml.ThrowingErrorHandler;
import railo.runtime.type.StructImpl;

@RunWith(OrderedRunner.class)
public class CacheObjectIOTests extends CacheTest {
	
	public CacheObjectIOTests() throws IOException {super();}
	
	public String key = "key";
	protected String getDatabase() {
		return "CacheObjectPutTests";
	}

	@Test
	@Order(order = 1)
	public void TestCachePut() throws Throwable {
		StructImpl test = new StructImpl();
		test.put("asdf", "fdsa");
		for (int i=0; i < 5000; i++) {
			cache.put("test-entry"+i, test, 0L, 0L);
		}
	}
	
	@Test
	@Order(order = 2)
	public void TestCacheSize() throws Throwable {
		assertEquals("Expected 5000 entries.", 5000, cache.keys().size());
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
		cache.getValue("test-entry0");
	}
	
	@Test
	@Order(order = 6)
	public void TestMissWithDefault() throws IOException {
		assertEquals("Expected the passed default value.","default",cache.getValue("test-entry0","default"));
	}
	
	@Test
	@Order(order = 100000)
	public void Cleanup() throws Throwable {
		this.cleanup();
	}
	
}
	