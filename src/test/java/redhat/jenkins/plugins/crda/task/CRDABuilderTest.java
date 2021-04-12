package redhat.jenkins.plugins.crda.task;

import static org.mockito.ArgumentMatchers.any;

import java.io.PrintStream;
import java.util.Map;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.jvnet.hudson.test.JenkinsRule;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import hudson.model.FreeStyleBuild;
import hudson.model.FreeStyleProject;
import redhat.jenkins.plugins.crda.utils.Utils;


@RunWith(PowerMockRunner.class)
@PrepareForTest({Utils.class, CRDABuilder.class})
@PowerMockIgnore({"javax.net.ssl.*", "javax.crypto.*"})
public class CRDABuilderTest {

    @Rule
    public JenkinsRule jenkins = new JenkinsRule();
    
    public String retStr = "{'total_scanned_dependencies': 0, 'total_scanned_transitives': 0, 'total_vulnerabilities': 0,"
    		+ "'publicly_available_vulnerabilities': 0, 'vulnerabilities_unique_to_synk': 0, 'direct_vulnerable_dependencies': 0,"
    		+ "'low_vulnerabilities': 0, 'medium_vulnerabilities': 0, 'high_vulnerabilities': 0, 'critical_vulnerabilities': 0,"
    		+ "'report_link': 'http://www.example.com'}";
    

    @Test
    public void testCRDATask() throws Exception {
        FreeStyleProject project = jenkins.createFreeStyleProject();
        CRDABuilder crdb = new CRDABuilder("/tmp/pom.xml", "ede6d550-b75e-4a2e-bfac-22222e77b48b", "v0.0.1");
        CRDABuilder mockObj = PowerMockito.mock(crdb.getClass());
        PowerMockito.mockStatic(Utils.class);
        PowerMockito.when(Utils.getCRDACredential("ede6d550-b75e-4a2e-bfac-22222e77b48b")).thenReturn("1234");
        PowerMockito.when(Utils.doInstall(any(String.class), any(PrintStream.class))).thenReturn("/home/crda/");
        PowerMockito.when(Utils.doExecute(any(String.class), any(PrintStream.class), any(Map.class))).thenReturn(retStr);
        PowerMockito.when(Utils.isJSONValid(retStr)).thenReturn(true);
        project.getBuildersList().add(crdb);
        FreeStyleBuild build = jenkins.buildAndAssertSuccess(project);
        jenkins.assertLogContains("----- CRDA Analysis Begins -----", build);
        jenkins.assertLogContains("Click on the CRDA Stack Report icon to view the detailed report.", build);
        jenkins.assertLogContains("----- CRDA Analysis Ends -----", build);
    }
}