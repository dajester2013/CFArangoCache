/*
 *  Copyright 2014 Jesse Shaffer
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

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
