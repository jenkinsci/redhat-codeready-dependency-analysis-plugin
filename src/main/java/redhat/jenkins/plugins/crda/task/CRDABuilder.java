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

package redhat.jenkins.plugins.crda.task;

import java.io.IOException;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.ServletException;

import org.json.JSONObject;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;
import org.kohsuke.stapler.QueryParameter;

import com.cloudbees.plugins.credentials.common.StandardListBoxModel;
import com.cloudbees.plugins.credentials.CredentialsProvider;
import org.kohsuke.stapler.AncestorInPath;
import hudson.model.Item;
import jenkins.model.Jenkins;
import hudson.security.ACL;

import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.AbstractProject;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Builder;
import hudson.util.FormValidation;
import hudson.util.ListBoxModel;
import jenkins.tasks.SimpleBuildStep;
import redhat.jenkins.plugins.crda.action.CRDAAction;
import redhat.jenkins.plugins.crda.utils.Config;
import redhat.jenkins.plugins.crda.utils.Utils;
import redhat.jenkins.plugins.crda.credentials.CRDAKey;

public class CRDABuilder extends Builder implements SimpleBuildStep {

    private String file;
    private String crdaKeyId;
    private String cliVersion;
    private boolean consentTelemetry = false;

    @DataBoundConstructor
    public CRDABuilder(String file, String crdaKeyId, String cliVersion, boolean consentTelemetry) {
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
        
        String baseDir = Utils.doInstall(cliVersion, logger);
        if (baseDir.equals("Failed")) {
        	logger.println("Error during installation process.");
        	return;
        }
        
        String cmd = Config.CLI_CMD.replace("filepath", this.getFile());
        cmd = baseDir + cmd;
        logger.println("Contribution towards anonymous usage stats is set to " + this.getConsentTelemetry());
        logger.println("Analysis Begins");        
        Map<String, String> envs = new HashMap<>();
        envs.put("CRDA_KEY", crdaUuid);
        envs.put("CONSENT_TELEMETRY", String.valueOf(this.getConsentTelemetry()));
        String results = Utils.doExecute(cmd, logger, envs);        
        
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
	        logger.println("----- CRDA Analysis Ends -----");
	        run.addAction(new CRDAAction(crdaUuid, res));
        }
    }

    @Extension
    public static final class BuilderDescriptorImpl extends BuildStepDescriptor<Builder> {

        public BuilderDescriptorImpl() {
        	load();
        }
    	
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
        
        @SuppressWarnings("deprecation")
		public ListBoxModel doFillCrdaKeyIdItems(@AncestorInPath Item item, @QueryParameter String crdaKeyId) {
            StandardListBoxModel model = new StandardListBoxModel();
            if (item == null) {
              
			Jenkins jenkins = Jenkins.getInstance();
              if (!jenkins.hasPermission(Jenkins.ADMINISTER)) {
                return model.includeCurrentValue(crdaKeyId);
              }
            } else {
              if (!item.hasPermission(Item.EXTENDED_READ) && !item.hasPermission(CredentialsProvider.USE_ITEM)) {
                return model.includeCurrentValue(crdaKeyId);
              }
            }
            return model.includeEmptyValue()
                        .includeAs(ACL.SYSTEM, item, CRDAKey.class)
                        .includeCurrentValue(crdaKeyId);
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
