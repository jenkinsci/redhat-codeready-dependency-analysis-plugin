package redhat.jenkins.plugins.crda.utils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.URL;
import java.net.URLConnection;

import org.codehaus.plexus.archiver.tar.TarGZipUnArchiver;
import org.codehaus.plexus.logging.console.ConsoleLogger;

import redhat.jenkins.plugins.crda.utils.CommandExecutor;
import redhat.jenkins.plugins.crda.utils.Config;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CRDAInstallation {
	
	private Boolean isWindows;
	//private Boolean isLinux;
	private Boolean isMac;
	
	public CRDAInstallation() {
		//this.isLinux = Utils.isLinux();
		this.isMac = Utils.isMac();
		this.isWindows = Utils.isWindows();
	}
	
	private Boolean downloadFile(String fromUrl, String dirPath, String localFileName, PrintStream logger) throws IOException,SecurityException {
		File theDir = new File(dirPath);
		Boolean success = false;
		if (!theDir.exists()) {
			success = theDir.mkdirs();
			if (! success) {
				logger.println("Directory creation failed.");
				return success;
			}				
		}
		
	    File localFile = new File(localFileName);
	    if (localFile.exists()) {
	    	success = localFile.delete();
	    	if (! success) {
				logger.println("Deletion of existing file failed.");
				return success;
			}
	    }
	    success = localFile.createNewFile();
	    if (! success) {
			logger.println("New file creation failed.");
			return success;
		}
	    URL url = new URL(fromUrl);
	    OutputStream out = new BufferedOutputStream(new FileOutputStream(localFileName));
	    URLConnection conn = url.openConnection();
	    InputStream in = conn.getInputStream();
	    byte[] buffer = new byte[1024];

	    int numRead;
	    while ((numRead = in.read(buffer)) != -1) {
	        out.write(buffer, 0, numRead);
	    }
	    if (in != null) {
	        in.close();
	    }
	    if (out != null) {
	        out.close();
	    }
	    return success;
	}
	
	private String getCliJarName(String version) {
		String name = "Linux_64bit.tar.gz";

		if (this.isWindows) {
			name = "Windows_64bit.tar.gz";
		}
		else if (this.isMac) {
			name = "macOS_64bit.tar.gz";
		}
		return "crda_" + version + "_" + name;
	}
	
	private String getFileLocation() {
		 String baseDir = System.getenv("HOME") + File.separator + ".crda";
		 return baseDir;
	}
	
	private Boolean isVersionCorrect(String fileLoc, String cliVersion, PrintStream logger) {
		String results = new CommandExecutor().execute(Config.CLI_VERSION_CMD.replace("crda", fileLoc), logger);
		Pattern pattern = Pattern.compile("^v[0-9]+\\.[0-9]+\\.[0-9]+.*", Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(results);
		if(matcher.find()) {
			String curVer = results.split("-")[0].replace("v", "");
			if (curVer.startsWith(cliVersion)) {
				logger.println("CRDA CLI version is up to date as per the requirement " + cliVersion);
				return true;
			}			
			logger.println("CRDA CLI version " + curVer + " is currently installed. Will be updated with " + cliVersion);
			return false;
		}
		logger.println("Cannot verify current installed CRDA CLI version");
		return false;
	}
	
	private Boolean isCliInstalled(String fileLoc, String cliVersion, PrintStream logger) {
		fileLoc = fileLoc + File.separator + "crda";
		File localFile = new File(fileLoc);
	    if (localFile.exists()) {
	    	if(this.isVersionCorrect(fileLoc, cliVersion, logger)) {	    		
	    		return true;
	    	}
	    	Boolean delStatus = localFile.delete();
	    	if(delStatus)
	    		logger.println("Current CRDA CLI successfully deleted");
	    	return false;
	    }
	    return false;
	}
	
	private void extractArtifact(String destDir, String fileZip) {
		TarGZipUnArchiver ua = new TarGZipUnArchiver(new File(fileZip));
		ua.setDestDirectory(new File(destDir));
		ua.enableLogging(new ConsoleLogger(ConsoleLogger.LEVEL_DISABLED, "console"));
		ua.extract();
	}
	
	public String install(String cliVersion, PrintStream logger) {
		CRDAInstallation cr = new CRDAInstallation();
		String url = Config.CLI_JAR_URL;
		
		url = url.replace("version", "v"+cliVersion);
		String filename = cr.getCliJarName(cliVersion);
		url = url.replace("clijar", filename);
		String fileLoc = cr.getFileLocation();
		String fileZip = fileLoc + File.separator + filename;
		
		if (! cr.isCliInstalled(fileLoc, cliVersion, logger)) {
			logger.println("Installation step for CRDA CLI in progress.");
			try {
				Boolean success = cr.downloadFile(url, fileLoc, fileZip, logger);
				if (success) {
					cr.extractArtifact(fileLoc, fileZip);
					logger.println("Installation of CRDA CLI finished.");
				}
				else {
					logger.println("Download step for CRDA CLI failed.");
					return "Failed";
				}
			} catch (IOException e) {
				logger.println(e);
			}
			catch (SecurityException e) {
				logger.println(e);
			}
		}
		else {
			logger.println("CRDA CLI already installed.Ready to use.");
		}		
		return fileLoc + File.separator;
	}
}
