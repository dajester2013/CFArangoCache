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

package org.jdsnet.arangodb.cache.railo;

import java.io.IOException;
import java.util.HashMap;

import railo.runtime.exp.PageException;
import railo.runtime.op.CastImpl;
import railo.runtime.type.KeyImpl;
import railo.runtime.type.Struct;
import railo.runtime.util.Cast;
import at.orz.arangodb.ArangoConfigure;
import at.orz.arangodb.ArangoDriver;
import at.orz.arangodb.ArangoException;

public class ArangoDBDriverFactory {
	
	public static final String	DEFAULT_HOST	= "127.0.0.1"; 
	public static final int		DEFAULT_PORT	= 8529; 
	public static final String	DEFAULT_USER	= "root"; 
	public static final String	DEFAULT_PWRD	= "";
	public static final String	DEFAULT_DB	= "_system";

//	private static HashMap<String, ArangoDriver> argsMap = new HashMap<String,ArangoDriver>();
	private static HashMap<ArangoDriver, ArangoConfigure> configMap = new HashMap<ArangoDriver, ArangoConfigure>();
	
	public static ArangoDriver openConnection(Struct arguments) throws PageException, IOException {
		Cast caster = CastImpl.getInstance();
		ArangoConfigure config = new ArangoConfigure();
		
		config.setHost(				caster.toString(	arguments.get(KeyImpl.init("host")		,DEFAULT_HOST	)));
		config.setPort(				caster.toIntValue(	arguments.get(KeyImpl.init("port")		,DEFAULT_PORT	)));
//		config.setDefaultDatabase(	caster.toString(	arguments.get(KeyImpl.init("database")	,DEFAULT_DB		)));
		config.setUser(				caster.toString(	arguments.get(KeyImpl.init("user")		,DEFAULT_USER	)));
		config.setPassword(			caster.toString(	arguments.get(KeyImpl.init("password")	,DEFAULT_PWRD	)));
		
		config.init();
		
		String db = caster.toString(arguments.get(KeyImpl.init("database")	,DEFAULT_DB));
		ArangoDriver drv = new ArangoDriver(config);

//		String argsKey	=	caster.toString(	arguments.get(KeyImpl.init("host")		,DEFAULT_HOST	))
//						+	caster.toString(	arguments.get(KeyImpl.init("port")		,DEFAULT_PORT	))
//						+	caster.toString(	arguments.get(KeyImpl.init("user")		,DEFAULT_USER	))
//						+	caster.toString(	arguments.get(KeyImpl.init("password")	,DEFAULT_PWRD	))
//						+	caster.toString(	arguments.get(KeyImpl.init("database")	,DEFAULT_DB		));
//		
//		argsMap.put(argsKey,drv);
		configMap.put(drv,config);
		
		try {
			drv.createDatabase(db);
		} catch(ArangoException e) {
			if (e.getErrorNumber() != 1207) {
				System.err.println("Database creation error - "+e.getErrorMessage());
				throw new IOException(e);
			}
		}
		
		drv.setDefaultDatabase(db);
		return drv;
	}
	
	public static ArangoConfigure getConfiguration(ArangoDriver drv) {
		return configMap.get(drv);
	}
	
	public static void closeConnection(ArangoDriver drv) {
		configMap.get(drv).shutdown();
		configMap.remove(drv);
	}
	
}
