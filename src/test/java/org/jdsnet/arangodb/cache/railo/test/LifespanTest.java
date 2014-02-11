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

import org.jdsnet.arangodb.cache.railo.ArangoDBCache;
import org.jdsnet.arangodb.cache.railo.ArangoDBCacheEntry;
import org.jdsnet.arangodb.util.CommonSerializer;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import at.orz.arangodb.ArangoException;
import railo.runtime.type.KeyImpl;
import railo.runtime.type.StructImpl;

public class LifespanTest {
	
	public LifespanTest() throws IOException {super();}
	
	private static String dbname = "LS_Test";
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
	public void TestCacheTimeoutObject() throws Throwable {
		String key = "key";
		String value = "value";
		
		long startedAt = System.currentTimeMillis();
		cache.put(key, value, 0L, 5000L);

		ArangoDBCacheEntry e;
		e = (ArangoDBCacheEntry)cache.getCacheEntry(key);
		assertEquals(e.getDocument().getExpires(), e.getDocument().getLastAccessed() + e.getDocument().getLifeSpan());
		
		// hit 3 times at halfway through the life span. should not timeout
		for (int i=0; i<3; i++) {
			Thread.sleep(2500);
			
			e = (ArangoDBCacheEntry)cache.getCacheEntry(key);
			assertEquals(e.getDocument().getExpires(), e.getDocument().getLastAccessed() + e.getDocument().getLifeSpan());
			assertTrue(e.lastHit().getTime() > startedAt);
		}
		
		// let it timeout
		Thread.sleep(5100);
		
		String defVal = "defaultValue";
		assertEquals(defVal,cache.getValue(key,defVal));
		assertEquals(0, cache.getConnection().getDocuments(cache.getCacheName()).size());
	}
	
}
