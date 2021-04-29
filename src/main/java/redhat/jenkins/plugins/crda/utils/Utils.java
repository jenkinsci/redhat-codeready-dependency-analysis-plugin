/* Copyright © 2021 Red Hat Inc.
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
# Author: Yusuf Zainee <yzainee@redhat.com>
*/

package redhat.jenkins.plugins.crda.utils;

import static com.cloudbees.plugins.credentials.CredentialsMatchers.withId;
import static com.cloudbees.plugins.credentials.CredentialsProvider.lookupCredentials;

import java.io.IOException;
import java.io.PrintStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.cloudbees.plugins.credentials.CredentialsMatchers;

import hudson.security.ACL;
import jenkins.model.Jenkins;
import redhat.jenkins.plugins.crda.credentials.CRDAKey;

public class Utils {	

	public static String doInstall(String cliVersion, PrintStream logger) {
    	CRDAInstallation cri = new CRDAInstallation();
    	return cri.install(cliVersion, logger);
    }
	
	public static String doExecute(String cmd, PrintStream logger, Map<String, String> envs) {
		return new CommandExecutor().execute(cmd, logger, envs);
    }
	
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
