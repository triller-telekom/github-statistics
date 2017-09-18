package org.eclipse.smarthome.githubstats;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.commons.lang.time.DateUtils;
import org.kohsuke.github.GHIssue;
import org.kohsuke.github.GHIssueComment;
import org.kohsuke.github.GHIssueSearchBuilder;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;

public class CommentHandler extends AbstractHandler {

	private List<GHIssue> issuesAndPRsWithComments = new ArrayList<>();
	private List<GHIssueComment> comments = new ArrayList<>();
	
	public CommentHandler(GitHub github, GHRepository repo) {
		super(github,repo);
	}
	
	@Override
	public void fetchData(Date from, Date to) {		
		String from_s = DateFormatUtils.format(from, "yyyy-MM-dd");
		String to_s = DateFormatUtils.format(to, "yyyy-MM-dd");
		//YYYY-MM-DDTHH:MM:SSZ
//		GHIssueSearchBuilder isb = github.searchIssues().q("repo:" + repo.getFullName() + " type:issue updated:"+from_s +".."+to_s);
		GHIssueSearchBuilder isb = github.searchIssues().q("repo:" + repo.getFullName() + " updated:"+from_s +".."+to_s);
		
		issuesAndPRsWithComments = isb.list().asList();
//		for(GHIssue i : issuesAndPRsWithComments)
//		{
//			System.out.println("issue: " + i.getNumber() + ": " + i.getTitle());
//		}
		for(GHIssue i : issuesAndPRsWithComments)
		{
			//TODO: this is an ugly hack relying on a patched github-api-1.90-SNAPSHOT.jar otherwise the listComments() runs into a NPE
			i.setOwner(repo);
			try {				
				for(GHIssueComment comment : i.listComments()) {					
					Date commentDate = comment.getCreatedAt();
					
					if(from.before(commentDate) && to.after(commentDate)) {
						comments.add(comment);
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public int getCommentCount() {
		return comments.size();
	}

}
