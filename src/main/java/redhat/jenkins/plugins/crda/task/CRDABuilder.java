package redhat.jenkins.plugins.crda.task;

import java.io.IOException;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.ServletException;

import org.jenkinsci.Symbol;
import org.json.JSONObject;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;
import org.kohsuke.stapler.QueryParameter;

import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.AbstractProject;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Builder;
import hudson.util.FormValidation;
import jenkins.tasks.SimpleBuildStep;
import redhat.jenkins.plugins.crda.action.CRDAAction;
import redhat.jenkins.plugins.crda.credentials.CRDAKey;
import redhat.jenkins.plugins.crda.utils.CRDAInstallation;
import redhat.jenkins.plugins.crda.utils.CommandExecutor;
import redhat.jenkins.plugins.crda.utils.Config;
import redhat.jenkins.plugins.crda.utils.Utils;

public class CRDABuilder extends Builder implements SimpleBuildStep {

    private String file;
    private String crdaKeyId;
    private String cliVersion;

    @DataBoundConstructor
    public CRDABuilder(String file, String crdaKeyId, String cliVersion) {
        this.file = file;
        this.crdaKeyId = crdaKeyId;
        this.cliVersion = cliVersion;
    }

    public String getFile() {
        return file;
    }

    @DataBoundSetter
    public void setFile(String file) {
        this.file = file;
    }
    
    public String getCliVersion() {
        return cliVersion;
    }

    @DataBoundSetter
    public void setCliVersion(String cliVersion) {
        this.cliVersion = cliVersion;
    }

    public String getCrdaKeyId() {
        return crdaKeyId;
    }

    @DataBoundSetter
    public void setCrdakeyid(String crdaKeyId) {
        this.crdaKeyId = crdaKeyId;
    }

    @Override
    public void perform(Run<?, ?> run, FilePath workspace, Launcher launcher, TaskListener listener) throws InterruptedException, IOException {
    	PrintStream logger = listener.getLogger();
    	logger.println("----- CRDA Analysis Begins -----");
    	String crdaUuid = Utils.getCRDACredential(this.getCrdaKeyId());
        
        String cliVersion = this.getCliVersion();
        if (cliVersion == null) {
        	cliVersion = Config.DEFAULT_CLI_VERSION;
        	logger.println("No CRDA Cli version provided. Taking the default version " + cliVersion);                
        }
        if (cliVersion.startsWith("v")) {
        	cliVersion = cliVersion.replace("v", "");
        }
        
        CRDAInstallation cri = new CRDAInstallation();
        String baseDir = cri.install(cliVersion, logger);
        if (baseDir.equals("Failed")) {
        	logger.println("Error during installation process.");
        	return;
        }
        
        String cmd = Config.CLI_CMD.replace("filepath", this.getFile());
        cmd = baseDir + cmd;
        logger.println("Analysis Begins");
        Map<String, String> envs = new HashMap<>();
        envs.put("CRDA_KEY", crdaUuid);
        String results = new CommandExecutor().execute(cmd, logger, envs);
        
        
        if (results.equals("") || results.equals("0") || ! Utils.isJSONValid(results)) {
        	logger.println("Analysis returned no results.");
        	return;
        }
        else {
        
        	logger.println("....Analysis Summary....");
        	JSONObject res = new JSONObject(results);
	        Iterator<String> keys = res.keys();
	        String key;
	        while(keys.hasNext()) {
	            key = keys.next();
	            logger.println("\t" + key.replace("_", " ") + " : " + res.get(key));
	        }
	        
	        logger.println("Click on the CRDA Stack Report icon to view the detailed report.");
	        logger.println("----- CRDA Analysis Ends ----");
	        run.addAction(new CRDAAction(crdaUuid, res));
        }
    }

    @Extension
    public static final class DescriptorImpl extends BuildStepDescriptor<Builder> {

        public FormValidation doCheckFile(@QueryParameter String file)
                throws IOException, ServletException {
            if (file.length() == 0){
                return FormValidation.error(Messages.CRDABuilder_DescriptorImpl_errors_missingFileName());
            }
            return FormValidation.ok();
        }

        public FormValidation doCheckCrdaKeyId(@QueryParameter String crdaKeyId)
                        throws IOException, ServletException {
            int len = crdaKeyId.length();
            if (len == 0){
                return FormValidation.error(Messages.CRDABuilder_DescriptorImpl_errors_missingUuid());
            }
            if (len > 0 && len < 36){
                return FormValidation.error(Messages.CRDABuilder_DescriptorImpl_errors_incorrectUuid());
            }
            return FormValidation.ok();
        }
        
        public FormValidation doCheckCliVersion(@QueryParameter String cliVersion)
                throws IOException, ServletException {
        	int len = cliVersion.length();
            if (len == 0){
                return FormValidation.ok();
            }
            if (!Utils.urlExists(Config.CLI_URL.replace("version", cliVersion))) {
            	return FormValidation.error(Messages.CRDABuilder_DescriptorImpl_errors_incorrectCli());
        	}
        	return FormValidation.ok();        	
        }

        @Override
        public boolean isApplicable(Class<? extends AbstractProject> aClass) {
            return true;
        }

        @Override
        public String getDisplayName() {
            return Messages.CRDABuilder_DescriptorImpl_DisplayName();
        }
    }

}
