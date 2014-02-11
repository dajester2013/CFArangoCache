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
import org.jdsnet.arangodb.util.CommonSerializer;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import at.orz.arangodb.ArangoException;
import railo.runtime.type.KeyImpl;
import railo.runtime.type.StructImpl;

public class IdleTest {
	
	private static String dbname = "IDLE_Test";
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
	public void TestCacheIdleTimeoutObject() throws Throwable {
		String key = "idlekey";
		long startedAt = System.currentTimeMillis();
		
		cache.put(key, startedAt, 0L, 5000L);
		Thread.sleep(2000);
		assertEquals(startedAt,cache.getValue(key));
		Thread.sleep(2000);
		assertEquals(startedAt,cache.getValue(key));
		// let it timeout
		Thread.sleep(5100);
		assertEquals(0, cache.getValue(key,0));
	}
	
}
