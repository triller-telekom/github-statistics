package org.eclipse.smarthome.githubstats;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.lang3.time.DateUtils;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;

public class GithubStats {

	private static String SPAN_END;	
	private static int NUM_INTERVALS = 1;
	private static int SPRINT_WEEKS = 3;		
	
	private static String OAUTH_TOKEN;
	private static int INTERVAL_DAYS;
	
	public static void main(String[] args) throws IOException, ParseException {
		
		Options options = new Options();
		
		Option reviewDayOpt = Option.builder("rd").required(true).longOpt("review-day").desc("Day of the sprint review").hasArg().build();
		options.addOption(reviewDayOpt);
		Option numIntervalopt = Option.builder("ni").required(false).longOpt("number-of-intervals").desc("Number of intervals (How many sprints back in time), default 1").hasArg().build();
		options.addOption(numIntervalopt);
		Option sprintWeeks = Option.builder("sw").required(false).longOpt("sprint-weeks").desc("Length of interval in weeks, default 3").hasArg().build();
		options.addOption(sprintWeeks);
		Option tokenOpt = Option.builder("t").required(false).longOpt("github-token").desc("Token to authenticate with Github (optional but recommended)").hasArg().build();
		options.addOption(tokenOpt);
		
		//directory containing the repo and the stats.sh file!
		Option repoNamesOpt = Option.builder("r").required(true).longOpt("repository-names").desc("Repository information (may occur multiple times) as <githubUser>/<repoName>=/path/to/checked/out/repo").hasArg().valueSeparator().build();
		options.addOption(repoNamesOpt);

		Option helpOpt = Option.builder("h").longOpt("help").desc("Print this help message").build();
		options.addOption(helpOpt);
				
		CommandLineParser parser = new DefaultParser();
		
		CommandLine cmd = null;
		try {
			cmd = parser.parse( options, args);
		} catch (org.apache.commons.cli.ParseException e) {
			System.err.println("Commandline arguments are wrong: " + e.getMessage());
			printHelp(options);
			System.exit(-1);
		}
				
		if(cmd.hasOption("h")) {
			printHelp(options);
			System.exit(0);
		}
				
		NUM_INTERVALS = Integer.parseInt(cmd.getOptionValue("ni", String.valueOf(NUM_INTERVALS)));		
		SPRINT_WEEKS = Integer.parseInt(cmd.getOptionValue("sw", String.valueOf(SPRINT_WEEKS)));
		
		SPAN_END = cmd.getOptionValue("rd");		
		
		String repositoryArgs[] = cmd.getOptionValues("r");
		
		HashMap<String, String> repositories = new HashMap<>();		
		for(String repArg : repositoryArgs) {
			String parts[] = repArg.split("=");
			
			String repo = parts[0];
			if(!repo.contains("/"))
			{
				System.err.println("Repository '"+repo+"' has wrong format. Should be username/reponame" );
				System.exit(-1);
			}			
			repositories.put(repo, parts[1]);
		}
		
		OAUTH_TOKEN = cmd.getOptionValue("t", null);
		INTERVAL_DAYS = SPRINT_WEEKS * 7;
		
		//org.kohsuke api		
		GitHub github;
		if(OAUTH_TOKEN == null) {
			github = GitHub.connectAnonymously();			
		} else {
			github = GitHub.connect("eclipse", OAUTH_TOKEN);
		}

		Date spanEnd = DateUtils.parseDate(SPAN_END, new String[]{"yyyy-MM-dd"});
		//add a couple of seconds so github recognizes it as the next day
		spanEnd = DateUtils.addSeconds(spanEnd, 15);
		
		int daysDiff = NUM_INTERVALS * SPRINT_WEEKS * 7;
		
		Date spanStart = DateUtils.addDays(spanEnd, -daysDiff);	
		Date tmpEnd = DateUtils.addDays(spanStart, INTERVAL_DAYS-1);

		PersistStats persister = new PersistStats("combinedStats.csv");
		while(tmpEnd.before(spanEnd)) {			
			System.out.println("Fetching Interval From: " + spanStart + " To: " + tmpEnd);

			List<StatisticResult> results = new LinkedList<>();
			for(String repository : repositories.keySet()) {
				
				System.out.println("Fetching repository: " + repository);
				
				String parts[] = repository.split("/");
				String user = parts[0];
				String rep = parts[1];
			
				GHRepository repo = github.getUser(user).getRepository(rep);
				
				System.out.println("Done fetching repo meta data from Github. Fetching stats now...");
				
				Statistics stats = new Statistics(github, repo, spanStart, tmpEnd, repositories.get(repository));
				stats.fetchData();
				
				StatisticResult result = stats.getResult();
				results.add(result);
				
				System.out.println("\n" + result);				
	
				System.out.println("RateLimit left: " + github.getRateLimit());
			}
			
			spanStart = DateUtils.addDays(tmpEnd, 1);
			tmpEnd = DateUtils.addDays(spanStart, INTERVAL_DAYS-1);
			
			System.out.println("Merging results from different repositories for the current interval");
			
			StatisticResult a = new StatisticResult();
			a.setFrom(spanStart);
			a.setTo(spanEnd);
			
			for(StatisticResult sr : results) {
				a.addResult(sr);
			}
			
			System.out.println("Persisting stats in file");
			
			persister.persist(a);			
		}
		persister.finish();
		System.out.println("Done");
	}

	private static void printHelp(Options options) {
		// automatically generate the help statement
		HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp( "GithubStats.jar", options );
	}
}
