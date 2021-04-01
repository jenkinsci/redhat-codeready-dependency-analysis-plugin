package redhat.jenkins.plugins.crda.utils;

import static com.cloudbees.plugins.credentials.CredentialsMatchers.withId;
import static com.cloudbees.plugins.credentials.CredentialsProvider.lookupCredentials;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.cloudbees.plugins.credentials.CredentialsMatchers;

import hudson.security.ACL;
import jenkins.model.Jenkins;
import redhat.jenkins.plugins.crda.credentials.CRDAKey;

public class Utils {
	
	public static boolean isJSONValid(String test) {
	    try {
	        new JSONObject(test);
	    } catch (JSONException ex) {
	        try {
	            new JSONArray(test);
	        } catch (JSONException ex1) {
	            return false;
	        }
	    }
	    return true;
	}
	
	public static boolean urlExists(String urlStr) {
		int responseCode = 404;
		try {
			URL url = new URL(urlStr);
			HttpURLConnection huc = (HttpURLConnection) url.openConnection();		
			responseCode = huc.getResponseCode();
		} catch (IOException e) {
			e.printStackTrace();
		}
		 
		return HttpURLConnection.HTTP_OK == responseCode;
	}
	
	public static String getOperatingSystem() {
	    String os = System.getProperty("os.name");
	    return os;
	}
	
	public static boolean isWindows() {
		String os = getOperatingSystem();
		return os.toLowerCase().contains("win");
	}
	
	public static boolean isLinux() {
		String os = getOperatingSystem();
		return os.toLowerCase().contains("lin");
	}
	
	public static boolean isMac() {
		String os = getOperatingSystem();
		return os.toLowerCase().contains("mac");
	}
	
	public static String getCRDACredential(String id) throws IOException, InterruptedException {
		CRDAKey crdaKey = CredentialsMatchers.firstOrNull(lookupCredentials(CRDAKey.class, Jenkins.getInstanceOrNull(), ACL.SYSTEM, Collections.emptyList()),
                                               withId(id));
		return (crdaKey != null)?crdaKey.getKey().getPlainText():null;
    }
	
	public static boolean is32() {
		return System.getProperty("sun.arch.data.model").equals("32");
	}
	
	public static boolean is64() {
		return System.getProperty("sun.arch.data.model").equals("64");
	}

}
