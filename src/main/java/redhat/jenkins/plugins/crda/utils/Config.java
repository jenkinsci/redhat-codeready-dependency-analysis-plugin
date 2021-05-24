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

package redhat.jenkins.plugins.crda.utils;

public class Config {
	
	public static final String CLI_CMD = "crda analyse filepath -j -c --client jenkins";
	public static final String CLI_URL = "https://github.com/fabric8-analytics/cli-tools/releases/version";
	public static final String CLI_JAR_URL = "https://github.com/fabric8-analytics/cli-tools/releases/download/version/clijar";
	public static final String DEFAULT_CLI_VERSION = "0.2.2";
	public static final String EXIT_SUCCESS = "0";
	public static final String EXIT_FAILED = "1";
	public static final String EXIT_VULNERABLE = "2";
	public static final String CLI_VERSION_CMD = "crda version";

}
