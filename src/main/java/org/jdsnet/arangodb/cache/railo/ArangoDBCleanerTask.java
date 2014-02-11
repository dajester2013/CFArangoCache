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
