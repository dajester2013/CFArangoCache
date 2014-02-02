/**
 * 
 */
package org.jdsnet.arangodb.cache.railo;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import org.jdsnet.arangodb.util.CacheUtil;

import at.orz.arangodb.ArangoDriver;
import at.orz.arangodb.ArangoException;
import at.orz.arangodb.entity.CollectionEntity;
import at.orz.arangodb.entity.DocumentEntity;
import railo.commons.io.cache.Cache;
import railo.commons.io.cache.Cache2;
import railo.commons.io.cache.CacheEntry;
import railo.commons.io.cache.CacheEntryFilter;
import railo.commons.io.cache.CacheKeyFilter;
import railo.commons.io.cache.exp.CacheException;
import railo.loader.engine.CFMLEngineFactory;
import railo.runtime.config.Config;
import railo.runtime.exp.PageException;
import railo.runtime.type.Collection.Key;
import railo.runtime.type.Struct;
import railo.runtime.util.Cast;

/**
 * @author jesse.shaffer
 *
 */
public class ArangoDBCache implements Cache2, Cache {
	
	private ArangoDriver driver;
	private CollectionEntity collection;
	private String cacheName;
	
	/* (non-Javadoc)
	 * @see railo.commons.io.cache.Cache#init(railo.runtime.config.Config, java.lang.String, railo.runtime.type.Struct)
	 */
	@Override
	public void init(Config webConfig, String cacheName, Struct arguments) throws IOException {
		Cast caster = CFMLEngineFactory.getInstance().getCastUtil();
		
		Properties props = new Properties();
		try {
			Iterator<Key> propNames = arguments.keyIterator();
			while (propNames.hasNext()) {
				Key propName = propNames.next();
				props.setProperty(caster.toString(propName), caster.toString(arguments.get(propName)));
			}
		} catch (PageException e) {
			throw new IOException("Error reading the arguments.", e);
		}
		
		try {
			this.cacheName = cacheName;
			driver = ArangoDriverFactory.instance(props);
			collection = driver.createCollection(cacheName);
		} catch (PageException | ArangoException e) {
			throw new IOException("Error connection to ArangoDB.", e);
		}
	}

	/* (non-Javadoc)
	 * @see railo.commons.io.cache.Cache#contains(java.lang.String)
	 */
	@Override
	public boolean contains(String key) {
		return false;
	}

	/* (non-Javadoc)
	 * @see railo.commons.io.cache.Cache#entries()
	 */
	@Override
	public List<CacheEntry> entries() throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see railo.commons.io.cache.Cache#entries(railo.commons.io.cache.CacheKeyFilter)
	 */
	@Override
	public List<CacheEntry> entries(CacheKeyFilter arg0) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see railo.commons.io.cache.Cache#entries(railo.commons.io.cache.CacheEntryFilter)
	 */
	@Override
	public List<CacheEntry> entries(CacheEntryFilter arg0) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see railo.commons.io.cache.Cache#getCacheEntry(java.lang.String)
	 */
	@Override
	public CacheEntry getCacheEntry(String arg0) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see railo.commons.io.cache.Cache#getCacheEntry(java.lang.String, railo.commons.io.cache.CacheEntry)
	 */
	@Override
	public CacheEntry getCacheEntry(String arg0, CacheEntry arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see railo.commons.io.cache.Cache#getCustomInfo()
	 */
	@Override
	public Struct getCustomInfo() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see railo.commons.io.cache.Cache#getValue(java.lang.String)
	 */
	@Override
	public Object getValue(String arg0) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see railo.commons.io.cache.Cache#getValue(java.lang.String, java.lang.Object)
	 */
	@Override
	public Object getValue(String arg0, Object arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see railo.commons.io.cache.Cache#hitCount()
	 */
	@Override
	public long hitCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	/* (non-Javadoc)
	 * @see railo.commons.io.cache.Cache#keys()
	 */
	@Override
	public List<String> keys() throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see railo.commons.io.cache.Cache#keys(railo.commons.io.cache.CacheKeyFilter)
	 */
	@Override
	public List<String> keys(CacheKeyFilter arg0) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see railo.commons.io.cache.Cache#keys(railo.commons.io.cache.CacheEntryFilter)
	 */
	@Override
	public List<CacheEntry> keys(CacheEntryFilter arg0) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see railo.commons.io.cache.Cache#missCount()
	 */
	@Override
	public long missCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	/* (non-Javadoc)
	 * @see railo.commons.io.cache.Cache#put(java.lang.String, java.lang.Object, java.lang.Long, java.lang.Long)
	 */
	@Override
	public void put(String arg0, Object arg1, Long arg2, Long arg3) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see railo.commons.io.cache.Cache#remove(java.lang.String)
	 */
	@Override
	public boolean remove(String arg0) throws IOException {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see railo.commons.io.cache.Cache#remove(railo.commons.io.cache.CacheKeyFilter)
	 */
	@Override
	public int remove(CacheKeyFilter arg0) throws IOException {
		// TODO Auto-generated method stub
		return 0;
	}

	/* (non-Javadoc)
	 * @see railo.commons.io.cache.Cache#remove(railo.commons.io.cache.CacheEntryFilter)
	 */
	@Override
	public int remove(CacheEntryFilter arg0) throws IOException {
		// TODO Auto-generated method stub
		return 0;
	}

	/* (non-Javadoc)
	 * @see railo.commons.io.cache.Cache#values()
	 */
	@Override
	public List<Object> values() throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see railo.commons.io.cache.Cache#values(railo.commons.io.cache.CacheKeyFilter)
	 */
	@Override
	public List<Object> values(CacheKeyFilter arg0) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see railo.commons.io.cache.Cache#values(railo.commons.io.cache.CacheEntryFilter)
	 */
	@Override
	public List<Object> values(CacheEntryFilter arg0) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see railo.commons.io.cache.Cache2#clear()
	 */
	@Override
	public void clear() throws IOException {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see railo.commons.io.cache.Cache2#verify()
	 */
	@Override
	public void verify() throws CacheException {
		// TODO Auto-generated method stub
		
	}

	protected String toDocumentId(String key) {
		return cacheName + "/" + key;
	}
	
}
