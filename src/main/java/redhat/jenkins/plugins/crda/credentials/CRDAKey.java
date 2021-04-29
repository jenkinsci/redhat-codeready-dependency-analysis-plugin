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
