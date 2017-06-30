package org.eclipse.smarthome.githubstats;

import java.util.Date;

import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;

public abstract class AbstractHandler {

	protected GitHub github;
	protected GHRepository repo;
	
	public AbstractHandler(GitHub github, GHRepository repo) {
		this.github = github;
		this.repo = repo;
	}
	public abstract void fetchData(Date from, Date to);
}
