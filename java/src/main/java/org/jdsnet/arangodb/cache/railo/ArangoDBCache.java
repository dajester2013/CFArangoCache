/**
 * 
 */
package org.jdsnet.arangodb.cache.railo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jdsnet.arangodb.util.RailoSerializer;
import org.jdsnet.arangodb.util.SerializerUtil;

import at.orz.arangodb.ArangoDriver;
import at.orz.arangodb.ArangoException;
import at.orz.arangodb.entity.BaseEntity;
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

/**
 * @author jesse.shaffer
 * 
 */
public class ArangoDBCache implements Cache2, Cache {

	private ArangoDriver driver;
	private String cacheName;
	private SerializerUtil serializer = new RailoSerializer();
	private long hits=0;
	private long misses=0;

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
			driver.createCollection(cacheName);
			driver.createIndex(cacheName, IndexType.HASH, false, "lifeSpan");
			driver.createIndex(cacheName, IndexType.HASH, false, "expires");
			driver.createIndex(cacheName, IndexType.HASH, false, "idle");
		}
	}

	@Override
	public void init(Config webConfig, String cacheName, Struct arguments)
			throws IOException {
		init(cacheName, arguments);
	}

	@Override
	public boolean contains(String key) {
		try {
			return !driver.getDocument(toDocumentId(key),ArangoDBCacheDocument.class).isError();
		} catch (ArangoException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public List<CacheEntry> entries() throws IOException {
		List<CacheEntry> result = new ArrayList<CacheEntry>();
        
		try {
			CursorEntity<ArangoDBCacheDocument> cursor = driver.executeSimpleAll(cacheName, 0, 0, ArangoDBCacheDocument.class);

			checkEntityError(cursor);
			
			while (cursor.hasMore()) {
				for (ArangoDBCacheDocument doc : cursor.getResults()) {
					result.add(new ArangoDBCacheEntry(doc, serializer));
				}
			}
		} catch (ArangoException e) {
			throw new IOException(e);
		}

        return result;
	}

	@Override
	public List<CacheEntry> entries(CacheKeyFilter filter) throws IOException {
		List<CacheEntry> result = new ArrayList<CacheEntry>();
        
		try {
			CursorEntity<ArangoDBCacheDocument> cursor = driver.executeSimpleAll(cacheName, 0, 0, ArangoDBCacheDocument.class);

			checkEntityError(cursor);
			
			while (cursor.hasMore()) {
				for (ArangoDBCacheDocument doc : cursor.getResults()) {
					if (filter.accept(doc.getKey())) {
						result.add(new ArangoDBCacheEntry(doc, serializer));
					}
				}
			}
		} catch (ArangoException e) {
			throw new IOException(e);
		}

        return result;
	}

	@Override
	public List<CacheEntry> entries(CacheEntryFilter filter) throws IOException {
		List<CacheEntry> result = new ArrayList<CacheEntry>();
        
		try {
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
		} catch (ArangoException e) {
			throw new IOException(e);
		}

        return result;
	}

	@Override
	public CacheEntry getCacheEntry(String key) throws IOException {
		DocumentEntity<ArangoDBCacheDocument> document;
		
		try {
			document = driver.getDocument(toDocumentId(key),ArangoDBCacheDocument.class);
		} catch (ArangoException e) {
			misses++;
			throw new CacheException(e.getMessage());
		}
		
		checkEntityError(document);
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
			return getCacheEntry(key);
		} catch(IOException e) {
			return defaultValue;
		}
	}

	@Override
	public Struct getCustomInfo() {
		// TODO Auto-generated method stub
		return null;
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
		int created = ((Long) System.currentTimeMillis()).intValue();
		// int idle = idleTime == null ? 0 : idleTime.intValue(); idle not
		// supported since version 2
		int idle = 0;
		int life = lifeSpan == null ? 0 : lifeSpan.intValue();

		ArangoDBCacheDocument doc = new ArangoDBCacheDocument();
		try {
			doc.setKey(key);
			doc.setData(serializer.serialize(value));
			doc.setCreatedOn(created);
			doc.setIdle(idle);
			doc.setLifeSpan(life);
			
			save(doc);
		} catch (Throwable e) {
			e.printStackTrace();
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
			result.add(i++,e.getValue());
		}
		return result;
	}

	@Override
	public List<Object> values(CacheEntryFilter filter) throws IOException {
		List<CacheEntry> entries = entries(filter);
		List<Object> result = new ArrayList<Object>(entries.size());
		int i = 0;
		for (CacheEntry e : entries) {
			result.add(i++,e.getValue());
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
			driver.getCollection(cacheName);
			driver.getCollectionCount(cacheName);
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
		Long now = System.currentTimeMillis();

		doc.setLastAccessed(now.intValue());
		doc.setLastUpdated(now.intValue());
		
		if (doc.getId() != null) {
			driver.updateDocument(doc.getId(), doc);
		} else {
			driver.createDocument(cacheName, doc);
		}
	}
	
	private void checkEntityError(BaseEntity entity) throws CacheException {
		if (entity.isError()) {
			misses++;
			throw new CacheException(entity.getErrorMessage());
		}
	}
}
