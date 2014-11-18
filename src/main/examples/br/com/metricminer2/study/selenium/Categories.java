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
