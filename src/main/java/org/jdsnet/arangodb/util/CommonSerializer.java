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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.common.util.Base64Coder;

import railo.runtime.exp.PageException;
import railo.runtime.op.CastImpl;

public class CommonSerializer implements SerializerUtil {

	@Override
	public String serialize(Object obj) throws IOException, PageException {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(bos);
		oos.writeObject(obj);
		oos.close();
		return CastImpl.getInstance().toBase64(bos.toByteArray()); //new String(Base64Coder.encode(bos.toByteArray()));
	}

	@Override
	public String serializeJSON(Object obj, boolean serializeQueryByColumns)
			throws PageException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object deserialize(Object obj) throws PageException, IOException, ClassNotFoundException {
		String serStr = CastImpl.getInstance().toString(obj,"");
		if (serStr.length() > 0) {
			ByteArrayInputStream binstr = new ByteArrayInputStream(Base64Coder.decode(serStr));
			ObjectInputStream instr = new ObjectInputStream(binstr);
			return instr.readObject();
		}
		return null;
	}

	@Override
	public Object deserializeJSON(String obj) throws PageException {
		// TODO Auto-generated method stub
		return null;
	}

}
