package org.jdsnet.arangodb.cache.railo;

import java.io.Serializable;
import java.util.Date;


import railo.commons.io.cache.CacheEntry;
import railo.runtime.type.Struct;

public class ArangoDBCacheEntry implements CacheEntry {
	
	private int hits = 0;
	private Serializable value;

	public ArangoDBCacheEntry setValue(Serializable value) {
		this.value = value;
		return this;
	}
	
	public static ArangoDBCacheEntry instance(Serializable value) {
		return (new ArangoDBCacheEntry()).setValue(value);
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
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object getValue() {
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
