package org.jdsnet.arangodb.cache.railo.test;

import static junit.framework.Assert.*;

import java.io.IOException;

import org.jdsnet.annotation.Order;
import org.jdsnet.junit.CacheTest;
import org.jdsnet.junit.OrderedRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(OrderedRunner.class)
public class BasicCacheObjectTest extends CacheTest {
	
	public BasicCacheObjectTest() throws IOException {super();}

	protected String getDatabase() {
		return "BasicCacheObjectTest";
	}

	@Test
	@Order(order = 1)
	public void VerifyCache() throws Throwable {
		cache.verify();
	}
	
	@Test
	@Order(order = 2)
	public void TestCacheStoreObject() throws Throwable {
		cache.put("key", "value", 0L, 0L);
		assertEquals(1L,cache.getConnection().getCollectionCount(cache.getCacheName()).getCount());
	}
	
	@Test
	@Order(order = 100000)
	public void Cleanup() throws Throwable {
		this.cleanup();
	}
	
}
