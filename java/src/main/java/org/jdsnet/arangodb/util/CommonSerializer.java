package org.jdsnet.arangodb.util;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

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
