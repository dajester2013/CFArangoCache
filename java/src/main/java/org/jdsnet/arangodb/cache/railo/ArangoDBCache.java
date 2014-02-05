/**
 * 
 */
package org.jdsnet.arangodb.cache.railo;

import java.io.IOException;
import java.util.List;

import org.jdsnet.arangodb.util.RailoSerializer;
import org.jdsnet.arangodb.util.SerializerUtil;

import at.orz.arangodb.ArangoDriver;
import at.orz.arangodb.ArangoException;
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
			return driver.getDocument(toDocumentId(key),ArangoDBCacheDocument.class).isError();
		} catch (ArangoException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public List<CacheEntry> entries() throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<CacheEntry> entries(CacheKeyFilter filter) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<CacheEntry> entries(CacheEntryFilter filter) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CacheEntry getCacheEntry(String key) throws IOException {
		CacheEntry entry = null;
		try {
			DocumentEntity<ArangoDBCacheDocument> document = driver.getDocument(toDocumentId(key),ArangoDBCacheDocument.class);
			
			if (document.isError()) {
				throw new IOException(document.getErrorMessage());
			} else {
				ArangoDBCacheDocument entity = document.getEntity();
				entry = new ArangoDBCacheEntry(entity,serializer);
			}
		} catch (ArangoException e) {
			throw new IOException(e);
		}
		
		return entry;
	}

	@Override
	public CacheEntry getCacheEntry(String arg0, CacheEntry arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Struct getCustomInfo() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object getValue(String arg0) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object getValue(String arg0, Object arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long hitCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public List<String> keys() throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> keys(CacheKeyFilter arg0) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<CacheEntry> keys(CacheEntryFilter arg0) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long missCount() {
		// TODO Auto-generated method stub
		return 0;
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
	public boolean remove(String arg0) throws IOException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int remove(CacheKeyFilter arg0) throws IOException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int remove(CacheEntryFilter arg0) throws IOException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public List<Object> values() throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Object> values(CacheKeyFilter arg0) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Object> values(CacheEntryFilter arg0) throws IOException {
		// TODO Auto-generated method stub
		return null;
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
}
