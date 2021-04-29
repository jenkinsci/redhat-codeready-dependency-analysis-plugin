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
