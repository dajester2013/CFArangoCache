package org.jdsnet.arangodb.util;

import java.util.Map;

import org.jdsnet.arangodb.cache.railo.ArangoDBCache;
import org.jdsnet.arangodb.cache.railo.ArangoDBCacheDocument;

import at.orz.arangodb.ArangoException;
import at.orz.arangodb.CursorResultSet;
import at.orz.arangodb.util.MapBuilder;
import railo.commons.io.cache.Cache;
import railo.commons.io.cache.CacheEntry;
import railo.runtime.op.CastImpl;
import railo.runtime.type.Struct;
import railo.runtime.type.StructImpl;
import railo.runtime.type.dt.TimeSpan;

public class CacheUtil {
	
	public static Struct getInfo(CacheEntry entry) {
		return new StructImpl();
	}
	
	public static Struct getInfo(Cache cache) {
		return new StructImpl();
	}
	
	public static Object toTimespan(long timespan) {
		if (timespan == 0) return "";
		TimeSpan ts = CastImpl.getInstance().toTimespan(new Double(timespan / (24D*60D*1000)),null);
		if (ts == null) return "";
		return ts;
	}
	
	public String toString(CacheEntry entry) {
		return	"created:       " + entry.created()
			+ "\nlast-hit:      " + entry.lastHit()
			+ "\nlast-modified: " + entry.lastModified()
			+ "\nidle-time:     " + entry.idleTimeSpan()
			+ "\nlive-time:     " + entry.liveTimeSpan()
			+ "\nhit-count:     " + entry.hitCount()
			+ "\nsize:          " + entry.size()
			;
	}
	
	public static int flushInvalidDocuments(ArangoDBCache cache) throws ArangoException {
		Map<String, Object> bindVars = new MapBuilder()
			.put("now", System.currentTimeMillis())
			.get();
		
		CursorResultSet<ArangoDBCacheDocument> cursor = cache.getConnection().executeQueryWithResultSet(
			 "for d in "+cache.getCacheName()+" filter d.expires > 0 && d.expires < @now return d"
			,bindVars
			,ArangoDBCacheDocument.class
			,true
			,1000
		);
		
		int count = cursor.getTotalCount();
		if (count > 0) {
			for (ArangoDBCacheDocument doc : cursor) {
				System.out.println("Expired");
				cache.getConnection().deleteDocument(doc.getId());
			}
		}
		return count;
	}
	
}
