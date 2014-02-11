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

/**
 * 
 */
package org.jdsnet.arangodb.cache.railo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

import org.jdsnet.arangodb.util.CacheUtil;
import org.jdsnet.arangodb.util.RailoSerializer;
import org.jdsnet.arangodb.util.SerializerUtil;

import at.orz.arangodb.ArangoDriver;
import at.orz.arangodb.ArangoException;
import at.orz.arangodb.entity.BaseEntity;
import at.orz.arangodb.entity.CollectionType;
import at.orz.arangodb.entity.CursorEntity;
import at.orz.arangodb.entity.DocumentEntity;
import at.orz.arangodb.entity.IndexType;
import railo.commons.io.cache.Cache;
import railo.commons.io.cache.Cache2;
import railo.commons.io.cache.CacheEntry;
import railo.commons.io.cache.CacheEntryFilter;
import railo.commons.io.cache.CacheKeyFilter;
import railo.commons.io.cache.exp.CacheException;
import railo.runtime.config.Config;
import railo.runtime.exp.PageException;
import railo.runtime.type.Struct;
import railo.runtime.type.StructImpl;

/**
 * @author jesse.shaffer
 * 
 */
public class ArangoDBCache implements Cache2 {

	private ArangoDriver driver;
	private String cacheName;
	private SerializerUtil serializer = new RailoSerializer();
	private long hits=0;
	private long misses=0;
	
	public ArangoDBCache() {}
	
	public ArangoDBCache(String cacheName, Struct arguments) throws IOException {
		init(cacheName, arguments);
	}
	
	public ArangoDBCache setSerializer(SerializerUtil su) {
		this.serializer = su;
		return this;
	}
	
	public SerializerUtil getSerializer() {
		return serializer;
	}
	
	public String getCacheName() {
		return cacheName;
	}
	
	public ArangoDriver getConnection() throws ArangoException {
		return driver;
	}
	
	/** 
	 * 
	 */
	public void init(String cacheName, Struct arguments) throws IOException {
		try {
			this.cacheName = cacheName;
			driver = ArangoDBDriverFactory.openConnection(arguments);
			initCollection();
			
			// TODO add scheduled task to purge expired...
		} catch (PageException | ArangoException e) {
			throw new IOException("Error connecting to ArangoDB.", e);
		}
	}

	private void initCollection() throws ArangoException {
		try {
			driver.getCollection(cacheName);
		} catch(ArangoException e) {
			driver.createCollection(cacheName);/*,false,true,null,false,false,CollectionType.DOCUMENT*/
			driver.createIndex(cacheName, IndexType.HASH, false, "lifeSpan");
			driver.createIndex(cacheName, IndexType.HASH, false, "expires");
			driver.createIndex(cacheName, IndexType.HASH, false, "idle");
		}
	}
	
	protected void startCleaner() {
		Timer timer = new Timer(true);
		timer.schedule(new ArangoDBCleanerTask(this), 0, 100000);
	}

	@Override
	public void init(Config webConfig, String cacheName, Struct arguments)
			throws IOException {
		init(cacheName, arguments);
	}

	@Override
	public boolean contains(String key) {
		try {
			CacheUtil.flushInvalidDocuments(this);
			return !driver.getDocument(toDocumentId(key),ArangoDBCacheDocument.class).isError();
		} catch (ArangoException e) {
			return false;
		}
	}

	@Override
	public List<CacheEntry> entries() throws IOException {
		List<CacheEntry> result = new ArrayList<CacheEntry>();
		try {
			CacheUtil.flushInvalidDocuments(this);
			CursorEntity<ArangoDBCacheDocument> cursor = driver.executeSimpleAll(cacheName, 0, 0, ArangoDBCacheDocument.class);

			checkEntityError(cursor);
			
			while (cursor.hasMore()) {
				for (ArangoDBCacheDocument doc : cursor.getResults()) {
					result.add(new ArangoDBCacheEntry(doc, serializer));
				}
			}
			// TODO: thread to update the expires on the result entries
		} catch (ArangoException e) {
			throw new IOException(e);
		}

        return result;
	}

	@Override
	public List<CacheEntry> entries(CacheKeyFilter filter) throws IOException {
		List<CacheEntry> result = new ArrayList<CacheEntry>();
        
		try {
			CacheUtil.flushInvalidDocuments(this);

			CursorEntity<ArangoDBCacheDocument> cursor = driver.executeSimpleAll(cacheName, 0, 0, ArangoDBCacheDocument.class);

			checkEntityError(cursor);
			
			while (cursor.hasMore()) {
				for (ArangoDBCacheDocument doc : cursor.getResults()) {
					if (filter.accept(doc.getKey())) {
						result.add(new ArangoDBCacheEntry(doc, serializer));
					}
				}
			}
			// TODO: thread to update the expires on the result entries
		} catch (ArangoException e) {
			throw new IOException(e);
		}

        return result;
	}

	@Override
	public List<CacheEntry> entries(CacheEntryFilter filter) throws IOException {
		List<CacheEntry> result = new ArrayList<CacheEntry>();
        
		try {
			CacheUtil.flushInvalidDocuments(this);
			
			CursorEntity<ArangoDBCacheDocument> cursor = driver.executeSimpleAll(cacheName, 0, 0, ArangoDBCacheDocument.class);

			checkEntityError(cursor);
			
			while (cursor.hasMore()) {
				for (ArangoDBCacheDocument doc : cursor.getResults()) {
					CacheEntry entry = new ArangoDBCacheEntry(doc, serializer);
					if (filter.accept(entry)) {
						result.add(entry);
					}
				}
			}
			
			// TODO: thread to update the expires on the result entries
		} catch (ArangoException e) {
			throw new IOException(e);
		}

        return result;
	}

	@Override
	public CacheEntry getCacheEntry(String key) throws IOException {
		DocumentEntity<ArangoDBCacheDocument> document;
		
		try {
			CacheUtil.flushInvalidDocuments(this);
			document = driver.getDocument(toDocumentId(key),ArangoDBCacheDocument.class);
			checkEntityError(document);
		} catch (ArangoException | CacheException e) {
			misses++;
			throw new IOException(e);
		}
		
		ArangoDBCacheDocument entity = document.getEntity();
		try {
			save(entity.hit());
		} catch (ArangoException e) {
			throw new IOException("Error updating hit count.",e);
		}
		CacheEntry entry = new ArangoDBCacheEntry(entity,serializer);
		hits++;
		return entry;
	
	}

	@Override
	public CacheEntry getCacheEntry(String key, CacheEntry defaultValue) {
		try {
			CacheUtil.flushInvalidDocuments(this);
			return getCacheEntry(key);
		} catch(IOException | ArangoException e) {
			return defaultValue;
		}
	}

	@Override
	public Struct getCustomInfo() {
		StructImpl info = new StructImpl();
		info.put("driverConfiguration"	,ArangoDBDriverFactory.getConfiguration(driver));
		info.put("hits"					,hits);
		info.put("misses"				,misses);
		info.put("cacheName"			,cacheName);
		return info;
	}

	@Override
	public Object getValue(String key) throws IOException {
		CacheEntry entry = getCacheEntry(key);
		return entry.getValue();
	}

	@Override
	public Object getValue(String key, Object defaultValue) {
		try {
			CacheEntry entry = getCacheEntry(key);
			return entry.getValue();
		} catch(IOException e) {
			return defaultValue;
		}
	}

	@Override
	public long hitCount() {
		// TODO Auto-generated method stub
		return hits;
	}

	@Override
	public List<String> keys() throws IOException {
		try {
			CacheUtil.flushInvalidDocuments(this);
			
			List<String> docHandles = driver.getDocuments(cacheName, true);
			List<String> result = new ArrayList<String>(docHandles.size());
			int i = 0;
			for (String documentHandle: docHandles) {
			    String dhp[] = documentHandle.split("\\/");
				result.add(i++,dhp[dhp.length-1]);
			}
			return result;
		} catch (ArangoException e) {
			throw new IOException(e);
		}
	}

	@Override
	public List<String> keys(CacheKeyFilter filter) throws IOException {
		try {
			CacheUtil.flushInvalidDocuments(this);
			
			List<String> docHandles = driver.getDocuments(cacheName, true);
			List<String> result = new ArrayList<String>();
			for (String documentHandle: docHandles) {
			    String dhp[] = documentHandle.split("\\/");
				documentHandle = dhp[dhp.length-1];
				
				if (filter.accept(documentHandle)) {
					result.add(documentHandle);
				}
			}
			return result;
		} catch (ArangoException e) {
			throw new IOException(e);
		}
	}

	@Override
	public List<CacheEntry> keys(CacheEntryFilter filter) throws IOException {
		/*try {
			List<String> docHandles = driver.getDocuments(cacheName, true);
			List<CacheEntry> result = new ArrayList<CacheEntry>();
			for (String documentHandle: docHandles) {
				CacheEntry entry = getCacheEntry(documentHandle);
				if (filter.accept(entry)) {
				    result.add(entry);
				}
			}
			return result;
		} catch (ArangoException e) {
			throw new IOException(e);
		}*/
		
		return entries(filter);
	}

	@Override
	public long missCount() {
		return misses;
	}

	@Override
	public void put(String key, Object value, Long idleTime, Long lifeSpan) {
		long now = System.currentTimeMillis();
		long idle = idleTime == null ? 0 : idleTime;
		long life = lifeSpan == null ? 0 : lifeSpan;
		
		ArangoDBCacheDocument doc;
		if (contains(key)) {
			try {
				doc = ((ArangoDBCacheEntry) getCacheEntry(key)).getDocument();
				doc.setData(serializer.serialize(value));
				doc.setLastUpdated(now);
				doc.setLastAccessed(now);
				doc.updateExpiration(now);
				save(doc);
			} catch (Throwable e) {}
		} else {
			doc = new ArangoDBCacheDocument();
			try {
				doc.setKey(key);
				doc.setData(serializer.serialize(value));
				doc.setCreatedOn(now);
				doc.setLastAccessed(now);
				doc.setLastUpdated(now);
				doc.setIdle(idle);
				doc.setLifeSpan(life);
				
				if (life > 0)
					doc.setExpires(now + life);
				else
					doc.setExpires(0);
				
				save(doc);
			} catch (Throwable e) {}
		}
	}

	@Override
	public boolean remove(String key) throws IOException {
		try {
			driver.deleteDocument(cacheName + "/" + key);
			return true;
		} catch (ArangoException e) {
			return false;
		}
	}

	@Override
	public int remove(CacheKeyFilter filter) throws IOException {
		try {
			CacheUtil.flushInvalidDocuments(this);
			
			List<String> docHandles = driver.getDocuments(cacheName, true);
			int result = 0;
			for (String documentHandle: docHandles) {
			    String dhp[] = documentHandle.split("\\/");
				
				if (filter.accept(dhp[dhp.length-1])) {
					driver.deleteDocument(documentHandle);
					result++;
				}
			}
			return result;
		} catch (ArangoException e) {
			throw new IOException(e);
		}
	}

	@Override
	public int remove(CacheEntryFilter filter) throws IOException {
		try {
			List<CacheEntry> entries = entries();
			int result = 0;
			for (CacheEntry entry: entries) {
				if (filter.accept(entry)) {
				    driver.deleteDocument(((ArangoDBCacheEntry)entry).getDocument().getId());
				}
			}
			return result;
		} catch (ArangoException e) {
			throw new IOException(e);
		}
	}

	@Override
	public List<Object> values() throws IOException {
		List<CacheEntry> entries = entries();
		List<Object> result = new ArrayList<Object>(entries.size());
		int i = 0;
		for (CacheEntry e : entries) {
			result.add(i++,e.getValue());
		}
		return result;
	}

	@Override
	public List<Object> values(CacheKeyFilter filter) throws IOException {
		List<CacheEntry> entries = entries(filter);
		List<Object> result = new ArrayList<Object>(entries.size());
		int i = 0;
		for (CacheEntry e : entries) {
			if (filter.accept(e.getKey())) {
				result.add(i++,e.getValue());
			}
		}
		return result;
	}

	@Override
	public List<Object> values(CacheEntryFilter filter) throws IOException {
		List<CacheEntry> entries = entries(filter);
		List<Object> result = new ArrayList<Object>(entries.size());
		int i = 0;
		for (CacheEntry e : entries) {
			if (filter.accept(e)) {
				result.add(i++,e.getValue());
			}
		}
		return result;
	}

	@Override
	public void clear() throws IOException {
		try {
			driver.truncateCollection(cacheName);
		} catch(ArangoException e) {
			throw new IOException(e);
		}
	}

	@Override
	public void verify() throws CacheException {
		try {
			checkEntityError(driver.getCollection(cacheName));
			checkEntityError(driver.getCollectionCount(cacheName));
		} catch(Throwable e) {
			throw new CacheException("Could not verify the collection.");
		}
	}

	public void close() {
		ArangoDBDriverFactory.closeConnection(driver);
	}
	
	protected String toDocumentId(String key) {
		return cacheName + "/" + key;
	}

	private void save(ArangoDBCacheDocument doc) throws ArangoException {
		if (doc.getId() != null) {
			driver.updateDocument(doc.getId(), doc);
		} else {
			driver.createDocument(cacheName, doc);
		}
	}
	
	private void checkEntityError(BaseEntity entity) throws CacheException {
		if (entity.isError()) {
			throw new CacheException(entity.getErrorMessage());
		}
	}
}
