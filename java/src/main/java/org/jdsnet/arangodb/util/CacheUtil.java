package org.jdsnet.arangodb.util;

import railo.commons.io.cache.Cache;
import railo.commons.io.cache.CacheEntry;
import railo.loader.engine.CFMLEngineFactory;
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
		TimeSpan ts = CFMLEngineFactory.getInstance().getCastUtil().toTimespan(new Double(timespan / (24D*60D*1000)),null);
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
	
}
