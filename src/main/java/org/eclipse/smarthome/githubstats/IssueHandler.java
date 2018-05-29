package org.eclipse.smarthome.githubstats;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.kohsuke.github.GHIssue;
import org.kohsuke.github.GHIssueSearchBuilder;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;

public class IssueHandler extends AbstractHandler{

	private List<GHIssue> issuesNew = new ArrayList<>();
	private List<GHIssue> issuesClosed = new ArrayList<>();

	public IssueHandler(GitHub github, GHRepository repo) {
		super(github,repo);
	}

	@Override
	public void fetchData(Date from, Date to) {
		String from_s = DateFormatUtils.format(from, "yyyy-MM-dd");
		String to_s = DateFormatUtils.format(to, "yyyy-MM-dd");
		GHIssueSearchBuilder isb = github.searchIssues().q("repo:" + repo.getFullName() + " type:issue created:"+from_s +".."+to_s);
		
		System.out.println("Fetching Issues from: " + from + " to: " + to);
		
		issuesNew = isb.list().asList();
//		for(GHIssue i : issues)
//		{
//			System.out.println("issue: " + i.getNumber() + ": " + i.getTitle());
//		}
		
		isb = github.searchIssues().q("repo:" + repo.getFullName() + " type:issue closed:"+from_s +".."+to_s);
		issuesClosed = isb.list().asList();
	}

	public int countNewlyOpenedIssues() {
		return issuesNew.size();
	}

	public int countClosedIssues() {
		return issuesClosed.size();
	}

}
