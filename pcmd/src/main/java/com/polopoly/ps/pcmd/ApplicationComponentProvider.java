package com.polopoly.ps.pcmd;

import com.polopoly.application.Application;
import com.polopoly.application.IllegalApplicationStateException;

public interface ApplicationComponentProvider {

	void add(Application appication) throws IllegalApplicationStateException;

}
