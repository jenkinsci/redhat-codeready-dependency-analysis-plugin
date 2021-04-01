package redhat.jenkins.plugins.crda.utils;

import org.junit.Test;
import static org.junit.Assert.assertNotNull;

import java.util.Map;

import org.apache.commons.collections.map.HashedMap;

public class CommandExecutorTest {
	
	@Test
	public void testCmdFunctions() {
		CommandExecutor ce = new CommandExecutor();
		System.setProperty("os.name", "Linux");
		assertNotNull(ce.execute("date", System.out));
		
		Map<String, String> env = new HashedMap();
		env.put("key", "value");
		System.setProperty("os.name", "Windows");
		assertNotNull(ce.execute("date", System.out), env);
	}

}
