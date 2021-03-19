package redhat.jenkins.plugins.crda.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.Map;

public class CommandExecutor {
	
	private String getPrompt() {
		if (Utils.isWindows())
			return "cmd.exe";
		return "bash";
	}
	
	private String getCmd() {
		if (Utils.isWindows())
			return "/c";
		return "-c";
	}
	
	public ProcessBuilder setEnv(ProcessBuilder processBuilder, Map<String, String> envs) {
		Map<String, String> env = processBuilder.environment();
		for(Map.Entry<String, String> entry:envs.entrySet()) {
			env.put(entry.getKey(), entry.getValue());
		}
		return processBuilder;
	}
	
	public String execute(String command, PrintStream logger) {
		return execute(command, logger, null);
	}
	
	public String execute(String command, PrintStream logger, Map<String, String> envs) {
		ProcessBuilder processBuilder = new ProcessBuilder();
		if (envs != null) {
			processBuilder = this.setEnv(processBuilder, envs);
		}
		
		processBuilder.command(this.getPrompt(), this.getCmd(), command);
		 try {

			 	processBuilder.redirectErrorStream(true);
			 	Process process = processBuilder.start();			 	

		        StringBuilder output = new StringBuilder();
		        InputStreamReader isr = new InputStreamReader(process.getInputStream(), "UTF-8");
		        BufferedReader reader = new BufferedReader(isr);

		        String line;
		        while ((line = reader.readLine()) != null) {
		            output.append(line + "\n");
		        }

		        reader.close();
	            isr.close();
		        
		        int exitVal = process.waitFor();
		        if (exitVal == 0 || exitVal == 2) {
		        	logger.println("Success!");        
		            return output.toString();
		        } else {
		        	logger.println("Abnormal Interruption!");
		        	logger.println("Status returned from CLI:" + exitVal);
		        	logger.println(output.toString());
		        	return String.valueOf(exitVal);
		        }

		    } catch (IOException e) {
		    	logger.println(e);
		    } catch (InterruptedException e) {
		    	logger.println(e);
		    }
		 return "";
	}
}
