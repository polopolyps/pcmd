package com.polopoly.ps.pcmd.jstackparser;

import java.io.IOException;
import java.io.Reader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.logging.Level;
import java.util.logging.Logger;

public class JStackParser {
	private static final Logger LOGGER = Logger.getLogger(JStackParser.class.getName());

	public void parse(Reader nonBufferedReader, ThreadVisitor visitor) throws JStackParseException {
		LineCountingBufferedReader reader = new LineCountingBufferedReader(nonBufferedReader);

		try {
			String line = "";

			do {
				try {
					JStack jstack = new JStack();

					String date = reader.readLine();

					if (date == null) {
						// done
						break;
					}

					try {
						jstack.setDate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(date));
					} catch (ParseException e) {
						throw new JStackParseException(reader, "Expected date in format yyyy-MM-dd HH:mm:ss.");
					}

					String name = reader.readLine();

					if (!name.startsWith("Full thread dump")) {
						throw new JStackParseException(reader, "Expected line to start with \"Full thread dump\".");
					}

					if (!"".equals(reader.readLine())) {
						throw new JStackParseException(reader, "Expected line to be empty.");
					}

					do {
						String threadHeader = reader.readLine();

						if (threadHeader.startsWith("JNI global references:")) {
							// end of stack trace
							visitor.visit(jstack);

							// empty line
							reader.readLine();

							break;
						}

						if (!threadHeader.startsWith("\"")) {
							throw new JStackParseException(reader,
									"Expected line to start with name of thread in quotation marks.");
						}

						int i = threadHeader.indexOf('"', 1);

						if (i < 0) {
							throw new JStackParseException(reader,
									"Expected line to start with name of thread in quotation marks.");
						}

						JStackThread thread = new JStackThread(jstack, threadHeader.substring(1, i));

						String threadState = reader.readLine();

						if (threadState.equals("")) {
							// thread without stacktrace.
							continue;
						}

						if (threadState.startsWith("\"")) {
							// sometimes there's no empty line after one thread
							reader.push(threadState);

							continue;
						}

						if (threadState.startsWith("JNI global references:")) {
							// end of stack trace
							visitor.visit(jstack);

							// empty line
							reader.readLine();

							break;
						}

						if (!threadState.startsWith("   ")) {
							throw new JStackParseException(reader,
									"Expected line to be thread state and start with three spaces.");
						}

						do {
							line = reader.readLine();

							if (line == null || line.equals("")) {
								break;
							}

							if (line.startsWith("\t")) {
								line = line.trim();

								if (line.startsWith("at ")) {
									thread.addStackTrace(line.substring(3));
								}
							} else {
								throw new JStackParseException(reader,
										"Expected line to be part of stack trace and start with a tab.");
							}
						} while (!"".equals(line));

						jstack.add(thread);
					} while (line != null);
				} catch (JStackParseException e) {
					LOGGER.log(Level.WARNING, e.getMessage());
				}
			} while (line != null);
		} catch (IOException e) {
			throw new JStackParseException(reader, e.toString());
		}

	}
}
