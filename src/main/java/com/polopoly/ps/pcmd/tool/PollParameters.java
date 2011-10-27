package com.polopoly.ps.pcmd.tool;


import com.polopoly.ps.pcmd.argument.ArgumentException;
import com.polopoly.ps.pcmd.argument.Arguments;
import com.polopoly.ps.pcmd.argument.NotProvidedException;
import com.polopoly.ps.pcmd.argument.ParameterHelp;
import com.polopoly.ps.pcmd.argument.Parameters;
import com.polopoly.util.client.PolopolyContext;

public class PollParameters implements Parameters {
	protected static final String PARAM_POLL_ID = "pollId";
	protected static final String PARAM_POLL_QUESTION_ID = "questionId";
	protected static final String PARAM_POLL_OPTION_ID = "optionId";

	private String pollId;
	private String questionId;
	private String optionId;
	
	public String getPollId() {
		return pollId;
	}

	public void setPollId(String pollId) {
		this.pollId = pollId;
	}


	public String getOptionId() {
		return optionId;
	}


	public void setOptionId(String optionId) {
		this.optionId = optionId;
	}

	public String getQuestionId() {
		return questionId;
	}
	
	
	public void setQuestionId(String questionId) {
		this.questionId = questionId;
	}


	@Override
	public void parseParameters(Arguments args, PolopolyContext context)
			throws ArgumentException {
		
		try {
			setPollId(args.getOptionString(PARAM_POLL_ID));
		} catch (NotProvidedException e) {
			throw new ArgumentException("You need to provide a poll Id.");
		}
		
		try {
			setQuestionId(args.getOptionString(PARAM_POLL_QUESTION_ID));
		} catch (NotProvidedException e) {
			setQuestionId("0");
		}
		
		try {
			setOptionId(args.getOptionString(PARAM_POLL_OPTION_ID));
		} catch (NotProvidedException e) {
			setOptionId("0");
		}
		
	}

	@Override
	public void getHelp(ParameterHelp help) {
		help.addOption(
				PARAM_POLL_ID,
				null,
				"The poll content id that will be used for the voting.");
		help.addOption(
				PARAM_POLL_OPTION_ID,
				null,
				"Denotes the option to which the vote will be added.");
		help.addOption(
				PARAM_POLL_QUESTION_ID,
				null,
				"Denotes the question of the poll.");
	}

}
