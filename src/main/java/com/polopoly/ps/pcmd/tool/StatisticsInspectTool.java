package com.polopoly.ps.pcmd.tool;

import java.util.Enumeration;
import java.util.Iterator;

import com.polopoly.application.Application;
import com.polopoly.pcmd.tool.Tool;
import com.polopoly.ps.pcmd.FatalToolException;
import com.polopoly.statistics.client.StatisticsClient;
import com.polopoly.statistics.thinclient.ThinAnalyzerContext;
import com.polopoly.statistics.thinclient.ThinManager;
import com.polopoly.statistics.thinclient.ThinTimeBin;
import com.polopoly.statistics.thinclient.ThinTimeBinGroup;
import com.polopoly.statistics.time.TimeMultiSelection;
import com.polopoly.statistics.time.TimeResolution;
import com.polopoly.statistics.time.TimeSelection;
import com.polopoly.util.client.PolopolyContext;

public class StatisticsInspectTool implements Tool<StatisticsInspectParameters>, RequiresStatisticsServer {

	@Override
	public StatisticsInspectParameters createParameters() {
		return new StatisticsInspectParameters();
	}

	@Override
	public void execute(PolopolyContext polopolyContext, StatisticsInspectParameters parameters)
			throws FatalToolException {
		Application application = polopolyContext.getApplication();

		StatisticsClient statisticsClient = (StatisticsClient) application
				.getApplicationComponent(StatisticsClient.DEFAULT_COMPOUND_NAME);

		ThinManager manager = statisticsClient.getThinManager();

		String[] analyzerNames;

		if (parameters.getAnalyzer() != null) {
			analyzerNames = new String[] { parameters.getAnalyzer() };
		} else {
			analyzerNames = statisticsClient.getThinManager().listAnalyzers();
		}

		String key = parameters.getKey();

		for (int i = 0; i < analyzerNames.length; i++) {
			System.out.println("Analyzer " + analyzerNames[i]);

			ThinAnalyzerContext context = manager.getThinAnalyzerContext(analyzerNames[i]);
			TimeResolution res = new TimeResolution(TimeResolution.HOUR, manager.getCalendar());
			TimeMultiSelection tms = context.getAvailableTimeBinGroups(res);

			if (tms == null) {
				System.out.println("No data stored.");
				continue;
			}

			System.out.println("There is data.");

			for (@SuppressWarnings("unchecked")
			Iterator<TimeSelection> t = tms.timeSelectionIterator(); t.hasNext();) {
				TimeSelection sel = t.next();
				ThinTimeBinGroup group = context.getThinTimeBinGroup(sel);

				if (group != null) {
					if (key != null) {
						System.out.println(key + ": " + toString(group.getThinTimeBin(key)));
					} else {
						@SuppressWarnings("unchecked")
						Enumeration<String> keyEnum = group.enumerateKeys();
						int remaining = parameters.getKeyCount();

						while (remaining-- > 0 && keyEnum.hasMoreElements()) {
							String atKey = keyEnum.nextElement();
							System.out.println(atKey + ": " + toString(group.getThinTimeBin(atKey)));
						}

						if (keyEnum.hasMoreElements()) {
							System.out.println("...");
						}
					}
				} else {
					System.out.println(sel + ": no data.");
				}
			}

			System.out.println();
		}

	}

	private String toString(ThinTimeBin thinTimeBin) {
		if (thinTimeBin == null) {
			return "null";
		}

		StringBuffer buffer = new StringBuffer(50);
		String[] fieldNames = thinTimeBin.getFieldNames();

		for (int i = 0; i < fieldNames.length; i++) {
			buffer.append(fieldNames[i]);
			buffer.append("=");
			buffer.append(thinTimeBin.getField(fieldNames[i]));

			if (i < fieldNames.length - 1) {
				buffer.append(",");
			}
		}

		return buffer.toString();
	}

	@Override
	public String getHelp() {
		return "Displays information stored in the statistics server";
	}

}
