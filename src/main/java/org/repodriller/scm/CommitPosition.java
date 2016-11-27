package org.repodriller.scm;

public class CommitPosition {

	private int absolutePosition;
	private float relativePosition;
	
	public CommitPosition(int absolutePosition, float relativePosition) {
		this.absolutePosition = absolutePosition;
		this.relativePosition = relativePosition;
	}

	public int getAbsolutePosition() {
		return absolutePosition;
	}

	public float getRelativePosition() {
		return relativePosition;
	}

}
