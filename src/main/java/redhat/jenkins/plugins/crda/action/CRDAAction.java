package redhat.jenkins.plugins.crda.action;

import hudson.model.Run;
import jenkins.model.RunAction2;
import org.json.JSONObject;
import redhat.jenkins.plugins.crda.model.Results;

public class CRDAAction implements RunAction2 {

    private transient Run run;
    private String uuid;
    private Results results;


    @Override
    public void onAttached(Run<?, ?> run) {
        this.run = run;
    }

    @Override
    public void onLoad(Run<?, ?> run) {
        this.run = run;
    }

    public Run getRun() {
        return run;
    }

    public CRDAAction(String uuid, JSONObject res) {
        this.uuid = uuid;
        this.results = new Results(res);
    }

    public String getUuid() {
            return uuid;
    }
    
    public Results getResults() {
        return results;
	}

    @Override
    public String getIconFileName() {
        return "/plugin/redhat-codeready-dependency-analysis/icons/redhat.png";
    }

    @Override
    public String getDisplayName() {
        return "CRDA Stack Report";
    }

    @Override
    public String getUrlName() {
        return "stack_report";
    }
}