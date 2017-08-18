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
	public static final String OAUTH_TOKEN = "INSERT_YOUR_TOKEN_HERE";
	
//	private static final String SPAN_START = "2014-01-13";
//	private static final String SPAN_END = "2017-06-28";
	
	private static final String SPAN_END = "2017-08-16";
	
	private static final int INTERVAL_COUNT = 26;
	private static final int SPRINT_WEEKS = 2;	
		
	private static final int INTERVAL_DAYS = 14;
	//directory containing the repo and the stats.sh file!
	private static String GIT_WORKING_DIR = "/home/stefan/smarthome-master/git/smarthome";
	
	public static void main(String[] args) throws IOException, ParseException {
		//org.kohsuke api		
		GitHub github = GitHub.connect("eclipse", OAUTH_TOKEN);
//		GitHub github = GitHub.connectAnonymously();
		
		GHRepository repo = github.getUser(REPO_USER).getRepository(REPO_NAME);

		Date spanEnd = DateUtils.parseDate(SPAN_END, new String[]{"yyyy-MM-dd"});
		spanEnd = DateUtils.addSeconds(spanEnd, 15);
		
		int daysDiff = INTERVAL_COUNT * SPRINT_WEEKS * 7;
		
		Date spanStart = DateUtils.addDays(spanEnd, -daysDiff);	
		Date tmpEnd = DateUtils.addDays(spanStart, INTERVAL_DAYS-1);

		PersistStats persister = new PersistStats("smarthomeStats.csv");
		while(tmpEnd.before(spanEnd)) {			
//			System.out.println("from: " + spanStart);
//			System.out.println("until: " + tmpEnd);

			Statistics stats = new Statistics(github, repo, spanStart, tmpEnd, GIT_WORKING_DIR);
			stats.fetchData();
			
			StatisticResult result = stats.getResult();
			persister.persist(result);			
			
			spanStart = DateUtils.addDays(tmpEnd, 1);
			tmpEnd = DateUtils.addDays(spanStart, INTERVAL_DAYS-1);

			System.out.println("RateLimit: " + github.getRateLimit());
		}
		persister.finish();
				
		System.out.println("RateLimit: " + github.getRateLimit());
	}

}
