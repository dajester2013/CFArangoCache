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

package org.jdsnet.arangodb.cache.railo;

import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import org.jdsnet.arangodb.util.SerializerUtil;

import railo.commons.io.cache.CacheEntry;
import railo.commons.lang.SizeOf;
import railo.runtime.type.Struct;

public class ArangoDBCacheEntry implements CacheEntry {
	
	private int hits = 0;
	private ArangoDBCacheDocument value;
	private SerializerUtil serializer;
	
	public ArangoDBCacheEntry(ArangoDBCacheDocument value, SerializerUtil serializer) {
		this.value = value;
		this.serializer = serializer;
	}
	
	public ArangoDBCacheDocument getDocument() {
		return value;
	}
	
	@Override
	public Date created() {
		Calendar c = Calendar.getInstance(TimeZone.getDefault());
		c.setTimeInMillis(value.getCreatedOn());
		return c.getTime();
	}

	@Override
	public Struct getCustomInfo() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getKey() {
		return value.getKey();
	}

	@Override
	public Object getValue() {
		try {
			return serializer.deserialize(value.getData());
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			try {
				e.printStackTrace(new PrintStream("/home/jesse.shaffer/Desktop/test.log"));
			} catch (FileNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		return value;
	}

	@Override
	public int hitCount() {
		return hits;
	}

	@Override
	public long idleTimeSpan() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Date lastHit() {
		Calendar c = Calendar.getInstance(TimeZone.getDefault());
		c.setTimeInMillis(value.getLastAccessed());
		return c.getTime();
	}

	@Override
	public Date lastModified() {
		Calendar c = Calendar.getInstance(TimeZone.getDefault());
		c.setTimeInMillis(value.getLastUpdated());
		return c.getTime();
	}

	@Override
	public long liveTimeSpan() {
		return value.getLifeSpan();
	}

	@Override
	public long size() {
		return SizeOf.size(value);
	}

}
