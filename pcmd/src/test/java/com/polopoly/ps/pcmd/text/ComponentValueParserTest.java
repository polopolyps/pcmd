package com.polopoly.ps.pcmd.text;

import static org.junit.Assert.*;

import org.junit.Test;

public class ComponentValueParserTest {

	
	
	@Test
	public void testName() throws Exception {
		ComponentValueParser target = new ComponentValueParser();
		String escape = target.escape("this is the value http://this is an example");
		assertEquals("this is the value http\\://this is an example", escape);
		
		String test = "\"{\"dimensions\":[{\"id\":\"dimension.Company\",\"name\":\"Company\",\"enumerable\":false,\"entities\":[{\"id\":\"Atex\",\"name\":\"Atex\",\"entities\":[],\"attributes\":[],\"childrenOmitted\":false}]},{\"id\":\"dimension.Organisation\",\"name\":\"Organisation\",\"enumerable\":false,\"entities\":[]},{\"id\":\"dimension.Location\",\"name\":\"Location\",\"enumerable\":false,\"entities\":[]},{\"id\":\"dimension.Person\",\"name\":\"Person\",\"enumerable\":false,\"entities\":[]},{\"id\":\"dimension.Tag\",\"name\":\"Tag\",\"enumerable\":false,\"entities\":[]},{\"id\":\"dimension.IPTC\",\"name\":\"IPTC\",\"enumerable\":true,\"entities\":[{\"id\":\"iptc-04\",\"name\":\"economy, business and finance\",\"entities\":[{\"id\":\"iptc-04001\",\"name\":\"agriculture\",\"entities\":[],\"attributes\":[],\"childrenOmitted\":false}],\"attributes\":[],\"childrenOmitted\":false},{\"id\":\"iptc-04\",\"name\":\"economy, business and finance\",\"entities\":[{\"id\":\"iptc-04002\",\"name\":\"chemicals\",\"entities\":[],\"attributes\":[],\"childrenOmitted\":false}],\"attributes\":[],\"childrenOmitted\":false}]}]}\"";
		String escape2 = target.escape(test);
		
		
		System.out.println(escape2);
	}
}
