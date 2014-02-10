package org.jdsnet.arangodb.cache.railo;

import com.google.gson.annotations.SerializedName;

public class ArangoDBCacheDocument {

	@SerializedName("_id")
	private String id;
	@SerializedName("_key")
	private String key;
	private String data;
	private long createdOn = 0;
	private long idle = -1;
	private long lifeSpan = -1;
	private long expires = -1;
	private long hits = 0;
	private long lastAccessed = 0;
	private long lastUpdated = 0;
	
	
	public ArangoDBCacheDocument hit() {
		long now = System.currentTimeMillis();
		this.hits++;
		setLastAccessed(now);
		return this;
	}
	
	public ArangoDBCacheDocument updateExpiration() {
		return updateExpiration(System.currentTimeMillis());
	}
	public ArangoDBCacheDocument updateExpiration(long when) {
		if (expires > 0)
			this.setExpires(when + lifeSpan);
		return this;
	}
	
	public boolean isExpired() {
		return expires > 0 && expires < System.currentTimeMillis();
	}
	
///////////////////////////////////////////////////////////////////////////////
// setters
///////////////////////////////////////////////////////////////////////////////
	public ArangoDBCacheDocument setId(String id) {
		this.id = id;
		return this;
	}

	public ArangoDBCacheDocument setKey(String key) {
		this.key = key;
		return this;
	}

	public ArangoDBCacheDocument setData(String data) {
		this.data = data;
		return this;
	}

	public ArangoDBCacheDocument setCreatedOn(long createdOn) {
		this.createdOn = createdOn;
		return this;
	}

	public ArangoDBCacheDocument setIdle(long idle) {
		this.idle = idle;
		return this;
	}

	public ArangoDBCacheDocument setLifeSpan(long lifeSpan) {
		this.lifeSpan = lifeSpan;
		return this;
	}

	public ArangoDBCacheDocument setExpires(long expires) {
		this.expires = expires;
		return this;
	}

	public ArangoDBCacheDocument setHits(long hits) {
		this.hits = hits;
		return this;
	}

	public ArangoDBCacheDocument setLastAccessed(long lastAccess) {
		this.lastAccessed = lastAccess;
		updateExpiration(lastAccess);
		return this;
	}

	public ArangoDBCacheDocument setLastUpdated(long lastUpdate) {
		this.lastUpdated = lastUpdate;
		return this;
	}
	

///////////////////////////////////////////////////////////////////////////////
// getters
///////////////////////////////////////////////////////////////////////////////
	public String getId() {
		return this.id;
	}

	public String getKey() {
		return this.key;
	}

	public String getData() {
		return this.data;
	}

	public long getCreatedOn() {
		return this.createdOn;
	}

	public long getIdle() {
		return this.idle;
	}

	public long getLifeSpan() {
		return this.lifeSpan;
	}

	public long getExpires() {
		return this.expires;
	}

	public long getHits() {
		return this.hits;
	}

	public long getLastAccessed() {
		return this.lastAccessed;
	}

	public long getLastUpdated() {
		return this.lastUpdated;
	}

}