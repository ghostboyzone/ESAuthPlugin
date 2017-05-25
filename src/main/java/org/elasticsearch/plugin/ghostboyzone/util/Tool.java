package org.elasticsearch.plugin.ghostboyzone.util;

import org.apache.commons.codec.binary.Base64;

public class Tool {
	
	/**
	 * base64 encode
	 * @param v
	 * @return
	 */
	public static String base64Encode(String v) {
		byte[] bt = v.getBytes();
		return Base64.encodeBase64String(bt);
	}
	
	/**
	 * base64 decode
	 * @param v
	 * @return
	 */
	public static String base64Decode(String v) {
		return new String(Base64.decodeBase64(v));
	}
	
}
