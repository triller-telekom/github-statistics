package org.eclipse.smarthome.githubstats;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;

import org.apache.commons.lang.time.DateUtils;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;

public class GithubStats {

	private static final String REPO_USER = "eclipse";
	private static final String REPO_NAME = "smarthome";
		
	private static String SPAN_END;	
	private static int NUM_INTERVALS;
	private static int SPRINT_WEEKS;		
	//directory containing the repo and the stats.sh file!
	private static String GIT_WORKING_DIR;
	public static String OAUTH_TOKEN;

	private static int INTERVAL_DAYS;
	
	public static void main(String[] args) throws IOException, ParseException {
		
		if(args.length == 5) {
			SPAN_END = args[0];
			NUM_INTERVALS = Integer.parseInt(args[1]);
			SPRINT_WEEKS = Integer.parseInt(args[2]);
			GIT_WORKING_DIR = args[3];
			OAUTH_TOKEN = args[4];
		} else if(args.length == 4) {
			SPAN_END = args[0];
			NUM_INTERVALS = Integer.parseInt(args[1]);
			SPRINT_WEEKS = Integer.parseInt(args[2]);
			GIT_WORKING_DIR = args[3];
			OAUTH_TOKEN = null;
		}
		else {
			System.out.println("Usage: java- jar GithubStats.jar <reviewDay(yyyy-mm-dd)> <numIntervals> <sprintWeeks> <path to stats.sh in repository> (<GitHubOAuthToken>)" );
			System.out.println("Example: java- jar GithubStats.jar 2017-08-23 26 2 /home/USER/smarthome-master/git/smarthome xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx" );
			System.exit(0);
		}
		
		INTERVAL_DAYS = SPRINT_WEEKS * 7;
		
		//org.kohsuke api		
		GitHub github;
		if(OAUTH_TOKEN == null) {
			github = GitHub.connectAnonymously();			
		} else {
			github = GitHub.connect("eclipse", OAUTH_TOKEN);
		}
		
		GHRepository repo = github.getUser(REPO_USER).getRepository(REPO_NAME);

		Date spanEnd = DateUtils.parseDate(SPAN_END, new String[]{"yyyy-MM-dd"});
		//add a couple of seconds so github recognizes it as the next day
		spanEnd = DateUtils.addSeconds(spanEnd, 15);
		
		int daysDiff = NUM_INTERVALS * SPRINT_WEEKS * 7;
		
		Date spanStart = DateUtils.addDays(spanEnd, -daysDiff);	
		Date tmpEnd = DateUtils.addDays(spanStart, INTERVAL_DAYS-1);

		PersistStats persister = new PersistStats("smarthomeStats.csv");
		while(tmpEnd.before(spanEnd)) {			
			System.out.println("Fetching Intervall From: " + spanStart + " To: " + tmpEnd);

			Statistics stats = new Statistics(github, repo, spanStart, tmpEnd, GIT_WORKING_DIR);
			stats.fetchData();
			
			StatisticResult result = stats.getResult();
			persister.persist(result);			
			
			spanStart = DateUtils.addDays(tmpEnd, 1);
			tmpEnd = DateUtils.addDays(spanStart, INTERVAL_DAYS-1);

			System.out.println("RateLimit left: " + github.getRateLimit());
		}
		persister.finish();
	}

}
