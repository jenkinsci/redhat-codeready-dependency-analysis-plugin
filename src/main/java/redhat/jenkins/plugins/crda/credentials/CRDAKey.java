package redhat.jenkins.plugins.crda.credentials;

import javax.annotation.Nonnull;
import java.io.IOException;

import com.cloudbees.plugins.credentials.CredentialsNameProvider;
import com.cloudbees.plugins.credentials.NameWith;
import com.cloudbees.plugins.credentials.common.StandardCredentials;
import hudson.Util;
import hudson.util.Secret;

@NameWith(value = CRDAKey.NameProvider.class, priority = 1)
public interface CRDAKey extends StandardCredentials {
	
	@Nonnull
	Secret getKey() throws IOException, InterruptedException;
	
	class NameProvider extends CredentialsNameProvider<CRDAKey> {

	    @Nonnull
	    @Override
	    public String getName(@Nonnull CRDAKey credentials) {
	      String description = Util.fixEmptyAndTrim(credentials.getDescription());
	      return description != null ? description : credentials.getId();
	    }
	}

}
