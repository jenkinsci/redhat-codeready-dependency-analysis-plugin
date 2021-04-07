package redhat.jenkins.plugins.crda.utils;

import org.junit.Test;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

public class UtilsTest {
	
	@Test
	public void testUtilsFunctions() {
		System.setProperty("os.name", "Linux");
		assertTrue(Utils.isLinux());
		assertFalse(Utils.isWindows());
		assertFalse(Utils.isMac());
		
		System.setProperty("sun.arch.data.model", "64");
		assertTrue(Utils.is64());
		assertFalse(Utils.is32());
		
		String validJson = "{ 'a_b': 10}";
		assertTrue(Utils.isJSONValid(validJson));
		
		String invalidJson = "abcdefgh";
		assertFalse(Utils.isJSONValid(invalidJson));
		
		String validUrl = "https://github.com/fabric8-analytics/cli-tools/releases/v0.0.1";
		assertTrue(Utils.urlExists(validUrl));
		
		String invalidUrl = "https://github.com/fabric8-analytics/cli-tools/releases/version";
		assertFalse(Utils.urlExists(invalidUrl));
	}

}
