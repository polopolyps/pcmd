package com.polopoly.util.contentid;

import org.junit.Assert;
import org.junit.Test;

import com.polopoly.cm.ContentId;

public class TestContentIdUtil {

    @Test
    public void testUndefinedVersion() {
        Assert.assertEquals("1.100", new ContentIdUtil(null, new ContentId(1, 100)).toString());
    }

}
