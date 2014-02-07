package org.jdsnet.arangodb.cache.railo;

import com.google.gson.annotations.SerializedName;

public class ArangoDBCacheDocument {

	@SerializedName("_id")
	private String id;
	@SerializedName("_key")
	private String key;
	private String data;
	private int createdOn = 0;
	private int idle = -1;
	private int lifeSpan = -1;
	private int expires = -1;
	private int hits = 0;
	private int lastAccessed = 0;
	private int lastUpdated = 0;
	
	
	public ArangoDBCacheDocument hit() {
		this.hits++;
		return this;
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

	public ArangoDBCacheDocument setCreatedOn(int createdOn) {
		this.createdOn = createdOn;
		return this;
	}

	public ArangoDBCacheDocument setIdle(int idle) {
		this.idle = idle;
		return this;
	}

	public ArangoDBCacheDocument setLifeSpan(int lifeSpan) {
		this.lifeSpan = lifeSpan;
		return this;
	}

	public ArangoDBCacheDocument setExpires(int expires) {
		this.expires = expires;
		return this;
	}

	public ArangoDBCacheDocument setHits(int hits) {
		this.hits = hits;
		return this;
	}

	public ArangoDBCacheDocument setLastAccessed(int lastAccess) {
		this.lastAccessed = lastAccess;
		return this;
	}

	public ArangoDBCacheDocument setLastUpdated(int lastUpdate) {
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

	public int getCreatedOn() {
		return this.createdOn;
	}

	public int getIdle() {
		return this.idle;
	}

	public int getLifeSpan() {
		return this.lifeSpan;
	}

	public int getExpires() {
		return this.expires;
	}

	public int getHits() {
		return this.hits;
	}

	public int getLastAccessed() {
		return this.lastAccessed;
	}

	public int getLastUpdated() {
		return this.lastUpdated;
	}

}