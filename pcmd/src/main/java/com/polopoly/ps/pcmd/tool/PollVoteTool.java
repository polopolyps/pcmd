package com.polopoly.ps.pcmd.tool;

import com.polopoly.pcmd.tool.Tool;
import com.polopoly.ps.pcmd.tool.RequiresPollServer;
import com.polopoly.poll.client.PollManager;
import com.polopoly.ps.pcmd.FatalToolException;
import com.polopoly.util.client.PolopolyContext;

public class PollVoteTool implements Tool<PollParameters>, RequiresPollServer {

	@Override
	public void execute(PolopolyContext context, PollParameters parameters)
			throws FatalToolException {

		PollManager pollManager = context.getPollManager();
		boolean hasVotingSucceeded = pollManager.vote(parameters.getPollId(),
				parameters.getQuestionId(), parameters.getOptionId());

		String result = hasVotingSucceeded ? "successfully." : "with an error.";

		System.out.println("The vote for " 
				+ PollParameters.PARAM_POLL_ID + ":"
				+ parameters.getPollId() + " "
				+ PollParameters.PARAM_POLL_QUESTION_ID + ":"
				+ parameters.getQuestionId() + " "
				+ PollParameters.PARAM_POLL_OPTION_ID + ":"
				+ parameters.getOptionId() + " was registered " + result +".");
	}

	@Override
	public PollParameters createParameters() {
		return new PollParameters();
	}

	@Override
	public String getHelp() {
		return "Vote for a specified option in a poll question.";
	}

}
