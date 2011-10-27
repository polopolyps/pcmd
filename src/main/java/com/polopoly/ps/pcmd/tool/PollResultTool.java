package com.polopoly.ps.pcmd.tool;

import com.polopoly.pcmd.tool.Tool;
import com.polopoly.ps.pcmd.tool.RequiresPollServer;
import com.polopoly.poll.client.PollManager;
import com.polopoly.ps.pcmd.FatalToolException;
import com.polopoly.user.server.Caller;
import com.polopoly.util.client.PolopolyContext;

public class PollResultTool implements Tool<PollParameters>, RequiresPollServer {

	@Override
	public void execute(PolopolyContext context, PollParameters parameters)
			throws FatalToolException {

		PollManager pollManager = context.getPollManager();
		int votes = pollManager.getVotes(parameters.getPollId(),
				parameters.getQuestionId(), parameters.getOptionId(),
				Caller.NOBODY_CALLER);

		System.out.println("The number of votes for "
				+ PollParameters.PARAM_POLL_ID + ":" 
				+ parameters.getPollId()+ " " 
				+ PollParameters.PARAM_POLL_QUESTION_ID + ":"
				+ parameters.getQuestionId() + " "
				+ PollParameters.PARAM_POLL_OPTION_ID + ":"
				+ parameters.getOptionId() + " are " + votes + ".");
	}

	@Override
	public PollParameters createParameters() {
		return new PollParameters();
	}

	@Override
	public String getHelp() {
		return "Shows the number of votes given to a specified poll question.";
	}

}
