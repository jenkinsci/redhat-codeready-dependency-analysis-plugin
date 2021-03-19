package redhat.jenkins.plugins.crda.credentials;

import javax.annotation.Nonnull;

import com.cloudbees.plugins.credentials.CredentialsScope;
import com.cloudbees.plugins.credentials.impl.BaseStandardCredentials;
import hudson.Extension;
import hudson.util.Secret;
import org.kohsuke.stapler.DataBoundConstructor;

import static hudson.util.Secret.fromString;

public class DefaultCRDAKey extends BaseStandardCredentials implements CRDAKey{
	
	@Nonnull
	private final Secret key;
	
	@DataBoundConstructor
    public DefaultCRDAKey(CredentialsScope scope, String id, String description, @Nonnull String key) {
	  super(scope, id, description);
	  this.key = fromString(key);
    }
	
	@Nonnull
	@Override
	public Secret getKey() {
	  return key;
	}

	@Extension
	public static class DefaultCRDAKeyDescriptor extends BaseStandardCredentialsDescriptor {

	  @Nonnull
	  @Override
	  public String getDisplayName() {
	    return "CRDA Key";
	  }
	}

}
