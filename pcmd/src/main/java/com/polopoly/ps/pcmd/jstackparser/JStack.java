package com.polopoly.ps.pcmd.jstackparser;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class JStack implements Iterable<JStackThread> {
	private List<JStackThread> threads = new ArrayList<JStackThread>();
	private Map<String, JStackThread> threadsById = new HashMap<String, JStackThread>();
	private Date date;

	public void add(JStackThread thread) {
		threads.add(thread);
		threadsById.put(thread.getId(), thread);
	}

	public JStackThread getThread(int i) {
		return threads.get(i);
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public Date getDate() {
		return date;
	}

	public JStackThread getThread(String id) {
		return threadsById.get(id);
	}

	@Override
	public Iterator<JStackThread> iterator() {
		return threads.iterator();
	}

}
