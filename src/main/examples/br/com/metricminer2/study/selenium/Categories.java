/**
 * Copyright 2014 Maurício Aniche

 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at

 * http://www.apache.org/licenses/LICENSE-2.0

 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package br.com.metricminer2.study.selenium;

public enum Categories {

	DRIVER("driver"),
	FINDELEMENT(".findElement"),
	CLICK(".click"),
	SUBMIT(".submit"),
	GET(".get("),
	GETTEXT(".getText()"),
	SENDKEYS(".sendKeys", "fill("),
	CLEAR(".clear"),
	UNTIL(".until"),
	EXPECTEDCONDITIONS("ExpectedConditions"),
	ASSERT("assert"),
	GETPAGESOURCE(".getPageSource"),
	ISDISPLAYED("isDisplayed()"),

	BYID("By.id", "byId("),
	BYCSSSELECTOR("By.cssSelector"),
	BYCLASSNAME("By.className", "byClass("),
	BYTAGNAME("By.tagName"),
	BYNAME("By.name"),
	BYLINKTEXT("By.linkText", "byLinkText("),
	
	;

	private String[] chunks;

	Categories(String... chunks) {
		this.chunks = chunks;
	}
	
	public String[] getChunks() {
		return chunks;
	}
	
	public boolean isContainedIn(String codeLine) {
		for(String chunk : chunks) {
			if(codeLine.contains(chunk)) return true;
		}
		return false;
	}
}
