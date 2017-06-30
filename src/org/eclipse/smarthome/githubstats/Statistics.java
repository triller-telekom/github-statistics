package org.eclipse.smarthome.githubstats;

import java.util.Date;

import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;

public class Statistics {
	
	private IssueHandler ih;
	private PRHandler ph;
	private Date from;
	private Date to;

	public Statistics(GitHub github, GHRepository repo, Date from, Date to, String gitWorkingDir) {
		ih = new IssueHandler(github, repo);
		ph = new PRHandler(github, repo, gitWorkingDir);
		this.from = from;
		this.to = to;
	}

	public void fetchData() {
		ih.fetchData(from, to);
		ph.fetchData(from, to);
	}
	
	public StatisticResult getResult() {
		StatisticResult result = new StatisticResult();
		
		result.setFrom(from);
		result.setTo(to);
		result.setIssuesNew(ih.countNewlyOpenedIssues());
		result.setIssuesClosed(ih.countClosedIssues());
		result.setPrsNew(ph.countNewPRs());
		result.setPrsMerged(ph.countMergedPRs());
		result.setAddedLines(ph.countAddedLines());
		result.setDeletedLines(ph.countDeletedLines());
		
		return result;
	}
	
}
