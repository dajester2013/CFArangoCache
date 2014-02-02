package org.jdsnet.arangodb.cache.railo;

import java.util.Properties;

import railo.loader.engine.CFMLEngineFactory;
import railo.runtime.exp.PageException;
import railo.runtime.util.Cast;
import at.orz.arangodb.ArangoConfigure;
import at.orz.arangodb.ArangoDriver;

public class ArangoDriverFactory {
	
	public static ArangoDriver instance(Properties properties) throws PageException {
		Cast caster = CFMLEngineFactory.getInstance().getCastUtil();
		ArangoConfigure config = new ArangoConfigure();
		
		config.setHost(properties.getProperty("host", "127.0.0.1"));
		config.setPort(caster.toIntValue(properties.getProperty("port", "8529")));
		config.setDefaultDatabase(properties.getProperty("database", "_system"));
		config.setUser(properties.getProperty("user", "root"));
		config.setPassword(properties.getProperty("password", ""));
		
		return new ArangoDriver(config);
	}
	
}
