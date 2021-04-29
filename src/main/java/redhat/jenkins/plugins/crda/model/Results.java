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

package redhat.jenkins.plugins.crda.model;

import org.json.JSONObject;

public class Results {
	
	private int totalDirect;
	private int totalTrans;
	private int totalVuln;
	private int commonVuln;
	private int uniqueVuln;
	private int vulnDeps;
	private int lowSeverity;
	private int medSeverity;
	private int highSeverity;
	private int criticalSeverity;
	private String saUrl;
	
	public Results(JSONObject res) {
		this.totalDirect = res.getInt("total_scanned_dependencies");
		this.totalTrans = res.getInt("total_scanned_transitives");
		this.totalVuln = res.getInt("total_vulnerabilities");
		this.commonVuln = res.getInt("publicly_available_vulnerabilities");
		this.uniqueVuln = res.getInt("vulnerabilities_unique_to_synk");
		this.vulnDeps = res.getInt("direct_vulnerable_dependencies");
		this.lowSeverity = res.getInt("low_vulnerabilities");
		this.medSeverity = res.getInt("medium_vulnerabilities");
		this.highSeverity = res.getInt("high_vulnerabilities");
		this.criticalSeverity = res.getInt("critical_vulnerabilities");
		this.saUrl = res.getString("report_link");
	}
	
	public String getSaUrl() {
		return saUrl;
	}

	public void setSaUrl(String saUrl) {
		this.saUrl = saUrl;
	}

	public int getTotalDirect() {
		return totalDirect;
	}
	
	public void setTotalDirect(int totalDirect) {
		this.totalDirect = totalDirect;
	}
	
	public int getTotalTrans() {
		return totalTrans;
	}
	
	public void setTotalTrans(int totalTrans) {
		this.totalTrans = totalTrans;
	}
	
	public int getTotalVuln() {
		return totalVuln;
	}
	
	public void setTotalVuln(int totalVuln) {
		this.totalVuln = totalVuln;
	}
	
	public int getCommonVuln() {
		return commonVuln;
	}
	
	public void setCommonVuln(int commonVuln) {
		this.commonVuln = commonVuln;
	}
	
	public int getUniqueVuln() {
		return uniqueVuln;
	}
	
	public void setUniqueVuln(int uniqueVuln) {
		this.uniqueVuln = uniqueVuln;
	}
	
	public int getVulnDeps() {
		return vulnDeps;
	}
	
	public void setVulnDeps(int vulnDeps) {
		this.vulnDeps = vulnDeps;
	}
	
	public int getLowSeverity() {
		return lowSeverity;
	}
	
	public void setLowSeverity(int lowSeverity) {
		this.lowSeverity = lowSeverity;
	}
	
	public int getMedSeverity() {
		return medSeverity;
	}
	
	public void setMedSeverity(int medSeverity) {
		this.medSeverity = medSeverity;
	}
	
	public int getHighSeverity() {
		return highSeverity;
	}
	
	public void setHighSeverity(int highSeverity) {
		this.highSeverity = highSeverity;
	}
	
	public int getCriticalSeverity() {
		return criticalSeverity;
	}
	
	public void setCriticalSeverity(int criticalSeverity) {
		this.criticalSeverity = criticalSeverity;
	}
	
	

}
