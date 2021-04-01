package redhat.jenkins.plugins.crda.task;

import hudson.model.FreeStyleBuild;
import hudson.model.FreeStyleProject;
import hudson.model.Label;
import redhat.jenkins.plugins.crda.utils.CRDAInstallation;
import redhat.jenkins.plugins.crda.utils.Utils;

import org.jenkinsci.plugins.workflow.cps.CpsFlowDefinition;
import org.jenkinsci.plugins.workflow.job.WorkflowJob;
import org.jenkinsci.plugins.workflow.job.WorkflowRun;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.junit.runner.RunWith;
/*
@RunWith(PowerMockRunner.class)
@PrepareForTest(Utils.class)
@PowerMockIgnore("javax.net.ssl.*")*/
public class CRDABuilderTest {
	/*
    @Rule
    public JenkinsRule jenkins = new JenkinsRule();
    

    @Test
    public void testConfigRoundtrip() throws Exception {
        FreeStyleProject project = jenkins.createFreeStyleProject();
        CRDABuilder crdb = new CRDABuilder("/tmp/pom.xml", "ede6d550-b75e-4a2e-bfac-21d38e77b48b", "v0.0.1");
        PowerMockito.mockStatic(Utils.class);
        PowerMockito.when(Utils.getCRDACredential("123456789")).thenReturn("1234");
        //Mockito.when(CRDAInstallation.install("v0.0.1", System.out)).thenReturn("1234");
        project.getBuildersList().add(crdb);
        project = jenkins.configRoundtrip(project);
        System.out.println(project.getBuildersList());
    }
    
    

    @Test
    public void testConfigRoundtripFrench() throws Exception {
        FreeStyleProject project = jenkins.createFreeStyleProject();
        HelloWorldBuilder builder = new HelloWorldBuilder(name);
        builder.setUseFrench(true);
        project.getBuildersList().add(builder);
        project = jenkins.configRoundtrip(project);

        HelloWorldBuilder lhs = new HelloWorldBuilder(name);
        lhs.setUseFrench(true);
        jenkins.assertEqualDataBoundBeans(lhs, project.getBuildersList().get(0));
    }

    @Test
    public void testBuild() throws Exception {
        FreeStyleProject project = jenkins.createFreeStyleProject();
        HelloWorldBuilder builder = new HelloWorldBuilder(name);
        project.getBuildersList().add(builder);

        FreeStyleBuild build = jenkins.buildAndAssertSuccess(project);
        jenkins.assertLogContains("Hello, " + name, build);
    }

    @Test
    public void testBuildFrench() throws Exception {

        FreeStyleProject project = jenkins.createFreeStyleProject();
        HelloWorldBuilder builder = new HelloWorldBuilder(name);
        builder.setUseFrench(true);
        project.getBuildersList().add(builder);

        FreeStyleBuild build = jenkins.buildAndAssertSuccess(project);
        jenkins.assertLogContains("Bonjour, " + name, build);
    }

    @Test
    public void testScriptedPipeline() throws Exception {
        String agentLabel = "my-agent";
        jenkins.createOnlineSlave(Label.get(agentLabel));
        WorkflowJob job = jenkins.createProject(WorkflowJob.class, "test-scripted-pipeline");
        String pipelineScript
                = "node {\n"
                + "  greet '" + name + "'\n"
                + "}";
        job.setDefinition(new CpsFlowDefinition(pipelineScript, true));
        WorkflowRun completedBuild = jenkins.assertBuildStatusSuccess(job.scheduleBuild2(0));
        String expectedString = "Hello, " + name + "!";
        jenkins.assertLogContains(expectedString, completedBuild);
    }
    */

}