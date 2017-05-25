package org.elasticsearch.plugin.ghostboyzone;

import static org.junit.Assert.*;

import org.elasticsearch.plugin.ghostboyzone.util.Tool;
import org.junit.Test;

public class Base64Test {
	
	@Test
	public void testEncode() {
		String expectStr = "YWRtaW46YWRtaW4=";
		String encodeStr = Tool.base64Encode("admin:admin");
		assertEquals(encodeStr, expectStr);
	}
	
	@Test
	public void testDecode() {
		String expectStr = "admin:admin";
		String decodeStr = Tool.base64Decode("YWRtaW46YWRtaW4=");
		assertEquals(decodeStr, expectStr);
	}
}
