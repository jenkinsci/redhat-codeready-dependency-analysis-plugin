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

package redhat.jenkins.plugins.crda.step;

import java.io.IOException;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletException;

import org.jenkinsci.Symbol;
import org.jenkinsci.plugins.workflow.steps.Step;
import org.jenkinsci.plugins.workflow.steps.StepContext;
import org.jenkinsci.plugins.workflow.steps.StepDescriptor;
import org.jenkinsci.plugins.workflow.steps.StepExecution;
import org.jenkinsci.plugins.workflow.steps.SynchronousNonBlockingStepExecution;
import org.json.JSONObject;
import org.kohsuke.stapler.AncestorInPath;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;
import org.kohsuke.stapler.QueryParameter;

import hudson.Extension;
import hudson.FilePath;
import hudson.model.Item;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.util.FormValidation;
import hudson.util.ListBoxModel;
import redhat.jenkins.plugins.crda.action.CRDAAction;
import redhat.jenkins.plugins.crda.task.CRDABuilder.BuilderDescriptorImpl;
import redhat.jenkins.plugins.crda.utils.Config;
import redhat.jenkins.plugins.crda.utils.Utils;

public final class CRDAStep extends Step {
    private String file;
    private String crdaKeyId;
    private String cliVersion;
    private boolean consentTelemetry = false;

    @DataBoundConstructor
    public CRDAStep(String file, String crdaKeyId, String cliVersion, boolean consentTelemetry) {
        this.file = file;
        this.crdaKeyId = crdaKeyId;
        this.cliVersion = cliVersion;
        this.consentTelemetry = consentTelemetry;
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
    public void setCrdaKeyId(String crdaKeyId) {
        this.crdaKeyId = crdaKeyId;
    }
    
    public boolean getConsentTelemetry() {
        return consentTelemetry;
    }

    @DataBoundSetter
    public void setConsentTelemetry(boolean consentTelemetry) {
        this.consentTelemetry = consentTelemetry;
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
            String crdaUuid = "";
            
            String filePath = step.getFile();
            if (filePath == null) {
            	logger.println("Filepath for the manifest file not provided. Please configure the build properly and retry.");
                return Config.EXIT_FAILED;
            }
            
            
        	crdaUuid = Utils.getCRDACredential(step.crdaKeyId);
        	if (crdaUuid == null) {
        		logger.println("CRDA Key id '" + step.crdaKeyId + "' was not found in the credentials. Please configure the build properly and retry.");
                return Config.EXIT_FAILED;
        	}
            
            
            if(crdaUuid.equals("")) {
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
            
            String baseDir = Utils.doInstall(cliVersion, logger);
            if (baseDir.equals("Failed"))
            	return Config.EXIT_FAILED;
            logger.println("Contribution towards anonymous usage stats is set to " + step.getConsentTelemetry());
            String cmd = Config.CLI_CMD.replace("filepath", filePath);
            cmd = baseDir + cmd;
            logger.println("Analysis Begins");
            Map<String, String> envs = new HashMap<>();
            envs.put("CRDA_KEY", crdaUuid);
            envs.put("CONSENT_TELEMETRY", String.valueOf(step.getConsentTelemetry()));
            String results = Utils.doExecute(cmd, logger, envs);
            
            
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
            run.addAction(new CRDAAction(crdaUuid, res));
            logger.println("----- CRDA Analysis Ends -----");
            return res.getInt("total_vulnerabilities") == 0 ? Config.EXIT_SUCCESS : Config.EXIT_VULNERABLE;
        }     

        private static final long serialVersionUID = 1L;
    }


    @Extension
    @Symbol("crdaAnalysis")
    public static class DescriptorImpl extends StepDescriptor {

    	private final BuilderDescriptorImpl builderDescriptor;

        public DescriptorImpl() {
          builderDescriptor = new BuilderDescriptorImpl();
        }
        
        @SuppressWarnings("unused")
        public ListBoxModel doFillCrdaKeyIdItems(@AncestorInPath Item item, @QueryParameter String crdaKeyId) {
          return builderDescriptor.doFillCrdaKeyIdItems(item, crdaKeyId);
        }
        
        @SuppressWarnings("unused")
        public FormValidation doCheckCrdaKeyId(@QueryParameter String crdaKeyId) throws IOException, ServletException {
          return builderDescriptor.doCheckCrdaKeyId(crdaKeyId);
        }
        
        @SuppressWarnings("unused")
        public FormValidation doCheckFile(@QueryParameter String file) throws IOException, ServletException {
          return builderDescriptor.doCheckFile(file);
        }
        
        @SuppressWarnings("unused")
        public FormValidation doCheckCliVersion(@QueryParameter String cliVersion) throws IOException, ServletException {
          return builderDescriptor.doCheckCliVersion(cliVersion);
        }
    	
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
