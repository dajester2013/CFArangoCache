package org.jdsnet.arangodb.cache.railo;

import java.util.TimerTask;

import org.jdsnet.arangodb.util.CacheUtil;

import at.orz.arangodb.ArangoException;

public class ArangoDBCleanerTask extends TimerTask {
	ArangoDBCache cache;
	
	public ArangoDBCleanerTask(ArangoDBCache cache) {
		this.cache = cache;
	}
	
	@Override
	public void run() {
		try {
			CacheUtil.flushInvalidDocuments(cache);
		} catch (ArangoException e) {
			// TODO Auto-generated catch block
			e.printStackTrace(System.err);
		}
	}

}
