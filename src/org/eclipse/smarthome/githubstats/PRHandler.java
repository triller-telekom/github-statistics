package org.eclipse.smarthome.githubstats;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.time.DateFormatUtils;
import org.kohsuke.github.GHIssue;
import org.kohsuke.github.GHIssueSearchBuilder;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;

public class PRHandler extends AbstractHandler {

	private List<GHIssue> prs = new ArrayList<>();
	private List<GHIssue> mergedPrs = new ArrayList<>();
	
	private long addedLines;
	private long deletedLines;
	private String gitWorkingDir;

	public PRHandler(GitHub github, GHRepository repo, String gitWorkingDir) {
		super(github, repo);
		this.gitWorkingDir = gitWorkingDir;
	}

	@Override
	public void fetchData(Date from, Date to) {
		String from_s = DateFormatUtils.format(from, "yyyy-MM-dd");
		String to_s = DateFormatUtils.format(to, "yyyy-MM-dd");
		GHIssueSearchBuilder isb = github.searchIssues()
				.q("repo:" + repo.getFullName() + " type:pr created:" + from_s + ".." + to_s);
		
//		System.out.println("pr query: " + "repo:" + repo.getFullName() + " type:pr created:" + from_s + ".." + to_s);
		System.out.println("Fetching PRs from: " + from + " to: " + to);

		prs = isb.list().asList();

		// for(GHIssue i : prs)
		// {
		// System.out.println("pr: " + i.getNumber() + ": " + i.getTitle());
		// }

		isb = github.searchIssues().q("repo:" + repo.getFullName() + " type:pr merged:" + from_s + ".." + to_s);
		mergedPrs = isb.list().asList();

		calculateAffectedLines(from_s, to_s);
	}

	private void calculateAffectedLines(String from, String to) {
		ProcessBuilder builder = new ProcessBuilder();
		builder.command("bash", "-c", "./stats.sh " + from + " " + to);

		builder.directory(new File(gitWorkingDir));
		try {
			Process process = builder.start();
			process.waitFor();
			BufferedReader stdInput = new BufferedReader(new InputStreamReader(process.getInputStream()));

			String s = null;
			while ((s = stdInput.readLine()) != null) {
//				System.out.println("Line from shell: " +s);
				this.addedLines = Long.parseLong(s.split(",")[0]);
				this.deletedLines = Long.parseLong(s.split(",")[1]);
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public int countMergedPRs() {
		return mergedPrs.size();
	}

	public int countNewPRs() {
		return prs.size();
	}

	public long countAddedLines() {
		return addedLines;
	}

	public long countDeletedLines() {
		return deletedLines;
	}

}
