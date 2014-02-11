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

package org.jdsnet.arangodb.util;

public interface SerializerUtil {
	
	public String serialize(Object obj) throws Throwable;
	
	public String serializeJSON(Object obj, boolean serializeQueryByColumns) throws Throwable;
	
	public Object deserialize(Object obj) throws Throwable;
	
	public Object deserializeJSON(String obj) throws Throwable;
	
}
