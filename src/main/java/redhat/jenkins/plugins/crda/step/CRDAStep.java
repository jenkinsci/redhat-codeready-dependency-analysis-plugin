package redhat.jenkins.plugins.crda.step;

import java.io.PrintStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import org.json.JSONObject;

import org.jenkinsci.Symbol;
import org.jenkinsci.plugins.workflow.steps.Step;
import org.jenkinsci.plugins.workflow.steps.StepContext;
import org.jenkinsci.plugins.workflow.steps.StepDescriptor;
import org.jenkinsci.plugins.workflow.steps.StepExecution;
import org.jenkinsci.plugins.workflow.steps.SynchronousNonBlockingStepExecution;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;

import hudson.Extension;
import hudson.FilePath;
import hudson.model.Run;
import hudson.model.TaskListener;
import redhat.jenkins.plugins.crda.action.CRDAAction;
import redhat.jenkins.plugins.crda.credentials.CRDAKey;
import redhat.jenkins.plugins.crda.utils.Config;
import redhat.jenkins.plugins.crda.utils.Utils;
import redhat.jenkins.plugins.crda.utils.CommandExecutor;
import redhat.jenkins.plugins.crda.utils.CRDAInstallation;

public final class CRDAStep extends Step {
    private String file;
    private String crdaKeyId;
    private String cliVersion;

    @DataBoundConstructor
    public CRDAStep(String file, String crdaKeyId, String cliVersion) {
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
    public DescriptorImpl getDescriptor() {
        return (DescriptorImpl) super.getDescriptor();
    }

    @Override
    public StepExecution start(StepContext context) throws Exception {
        return new Execution(this, context);
    }

    public static class Execution extends SynchronousNonBlockingStepExecution<String> {

        private transient final CRDAStep step;

        protected Execution(CRDAStep step, StepContext context) {
            super(context);
            this.step = step;
        }

        @Override
        protected String run() throws Exception {

            PrintStream logger = getContext().get(TaskListener.class).getLogger();
            logger.println("----- CRDA Analysis Begins -----");
            
            String filePath = step.getFile();
            if (filePath == null) {
            	logger.println("Filepath for the manifest file not provided. Please configure the build properly and retry.");
                return Config.EXIT_FAILED;
            }
            
            CRDAKey crdaKey = Utils.getCRDACredential(step.crdaKeyId);
            if (crdaKey == null) {
            	logger.println("CRDA Key id '" + step.crdaKeyId + "' was not found in the credentials. Please configure the build properly and retry.");
                return Config.EXIT_FAILED;
            }
            
            String cliVersion = step.getCliVersion();
            if (cliVersion == null) {
            	cliVersion = Config.DEFAULT_CLI_VERSION;
            	logger.println("No CRDA Cli version provided. Taking the default version " + cliVersion);                
            }
            else {
            	if (!Utils.urlExists(Config.CLI_URL.replace("version", cliVersion))) {
            		cliVersion = Config.DEFAULT_CLI_VERSION;
            		logger.println("No such version of CRDA CLI exist. Taking default version " + cliVersion);            		
            	}
            	else {
            		cliVersion = cliVersion.replace("v", "");
            	}
            }            
            
            String crdaUuid = crdaKey.getKey().getPlainText();
            
            CRDAInstallation cri = new CRDAInstallation();
            String baseDir = cri.install(cliVersion, logger);
            if (baseDir.equals("Failed"))
            	return Config.EXIT_FAILED;
            
            String cmd = Config.CLI_CMD.replace("filepath", filePath);
            cmd = baseDir + cmd;
            logger.println("Analysis Begins");
            Map<String, String> envs = new HashMap<>();
            envs.put("CRDA_KEY", crdaUuid);
            String results = new CommandExecutor().execute(cmd, logger, envs);
            
            
            if (results.equals("") || results.equals("0") || ! Utils.isJSONValid(results)) {
            	logger.println("Analysis returned no results.");
            	return Config.EXIT_FAILED;
            }
            
            logger.println("....Analysis Summary....");
            JSONObject res = new JSONObject(results);
            
            Iterator<String> keys = res.keys();
	        String key;
	        while(keys.hasNext()) {
	            key = keys.next();
	            logger.println("\t" + key.replace("_", " ") + " : " + res.get(key));
	        }
	        
	        logger.println("Click on the CRDA Stack Report icon to view the detailed report.");
            
            Run run = getContext().get(Run.class);
            run.addAction(new CRDAAction(crdaUuid, "66c6afdce5bb45aca860f5c343f7368f", res));
            logger.println("----- CRDA Analysis Ends ----");
            return Config.EXIT_SUCCESS;
        }     

        private static final long serialVersionUID = 1L;
    }


    @Extension
    @Symbol("crdaAnalysis")
    public static class DescriptorImpl extends StepDescriptor {

        @Override
        public String getFunctionName() {
            return "crdaAnalysis";
        }

        @Override
        public String getDisplayName() {
            return "Invoke CRDA Analysis";
        }

        @Override
        public Set< Class<?>> getRequiredContext() {
            return Collections.unmodifiableSet(new HashSet<Class<?>>(Arrays.asList(FilePath.class, Run.class, TaskListener.class)));
        }
    }
}
