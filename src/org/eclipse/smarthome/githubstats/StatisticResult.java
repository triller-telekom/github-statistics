package org.eclipse.smarthome.githubstats;

import java.util.Date;

public class StatisticResult {
	
	private Date from;
	private Date to;
	
	private int issuesNew;
	private int issuesClosed;
	
	private int prsNew;
	private int prsMerged;
	
	private long addedLines;
	private long deletedLines;
	
	private int newComments;
	
	public Date getFrom() {
		return from;
	}
	public void setFrom(Date from) {
		this.from = from;
	}
	public Date getTo() {
		return to;
	}
	public void setTo(Date to) {
		this.to = to;
	}
	public int getIssuesNew() {
		return issuesNew;
	}
	public void setIssuesNew(int issuesNew) {
		this.issuesNew = issuesNew;
	}
	public int getIssuesClosed() {
		return issuesClosed;
	}
	public void setIssuesClosed(int issuesClosed) {
		this.issuesClosed = issuesClosed;
	}
	public int getPrsNew() {
		return prsNew;
	}
	public void setPrsNew(int prsNew) {
		this.prsNew = prsNew;
	}
	public int getPrsMerged() {
		return prsMerged;
	}
	public void setPrsMerged(int prsMerged) {
		this.prsMerged = prsMerged;
	}
	public long getAddedLines() {
		return addedLines;
	}
	public void setAddedLines(long addedLines) {
		this.addedLines = addedLines;
	}
	public long getDeletedLines() {
		return deletedLines;
	}
	public void setDeletedLines(long deletedLines) {
		this.deletedLines = deletedLines;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();		
		sb.append("Statistic Result: " + from + " until "+ to + "\n");
		sb.append("Issues New: " + getIssuesNew() + "\n");
		sb.append("Issues Closed: " + getIssuesClosed() + "\n");
		sb.append("PRs New: " + getPrsNew() + "\n");
		sb.append("PRs Merged: " + getPrsMerged() + "\n");
		sb.append("PRs Added Lines: " + getAddedLines() + "\n");
		sb.append("PRs Deleted Lines: " + getDeletedLines() + "\n");
		sb.append("PRs Line Diff: " + (getAddedLines()-getDeletedLines()) + "\n");
		sb.append("New Comments: " + getNewComments() + "\n");
		sb.append("---\n");
		return sb.toString();
	}
	public int getNewComments() {
		return newComments;
	}
	public void setNewComments(int newComments) {
		this.newComments = newComments;
	}

}
