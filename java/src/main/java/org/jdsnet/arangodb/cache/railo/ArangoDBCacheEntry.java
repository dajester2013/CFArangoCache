package org.jdsnet.arangodb.cache.railo;

import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.Date;

import org.jdsnet.arangodb.util.SerializerUtil;

import railo.commons.io.cache.CacheEntry;
import railo.runtime.type.Struct;

public class ArangoDBCacheEntry implements CacheEntry {
	
	private int hits = 0;
	private ArangoDBCacheDocument value;
	private SerializerUtil serializer;
	
	public ArangoDBCacheEntry(ArangoDBCacheDocument value, SerializerUtil serializer) {
		this.value = value;
		this.serializer = serializer;
	}
	
	@Override
	public Date created() {
		// TODO Auto-generated method stub
		return null;
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
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Date lastModified() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long liveTimeSpan() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long size() {
		// TODO Auto-generated method stub
		return 0;
	}

}
