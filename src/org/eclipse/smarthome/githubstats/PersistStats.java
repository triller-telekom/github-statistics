package org.eclipse.smarthome.githubstats;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.commons.lang.time.DateFormatUtils;

public class PersistStats {

	private FileWriter fw;

	public PersistStats(String fileName) {
		File file = new File(fileName);
		if (file.exists()) {
			file.delete();
		}
		try {
			this.fw = new FileWriter(file);
			fw.append(buildHeadLine());
			fw.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private String buildHeadLine() {
		StringBuilder headLineBuilder = new StringBuilder();
		headLineBuilder.append("Timespan,");
		headLineBuilder.append("IssuesNew,");
		headLineBuilder.append("IssuesClosed,");
		headLineBuilder.append("PRsNew,");
		headLineBuilder.append("PRsMerged,");
		headLineBuilder.append("LinesAdded,");
		headLineBuilder.append("LinesDeleted\n");

		return headLineBuilder.toString();
	}

	public void persist(StatisticResult result) {
		try {
			fw.append(resultToCSVLine(result));
			fw.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private String resultToCSVLine(StatisticResult result) {
		StringBuilder sb = new StringBuilder();

		String from = DateFormatUtils.format(result.getFrom(), "yyyy-MM-dd");
		String to = DateFormatUtils.format(result.getTo(), "yyyy-MM-dd");

		sb.append(from + " - " + to + ",");
		sb.append(result.getIssuesNew() + ",");
		sb.append(result.getIssuesClosed() + ",");
		sb.append(result.getPrsNew() + ",");
		sb.append(result.getPrsMerged() + ",");
		sb.append(result.getAddedLines() + ",");
		sb.append(result.getDeletedLines() + "\n");

		return sb.toString();
	}

	public void finish() {
		try {
			fw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
