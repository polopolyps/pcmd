package com.polopoly.util.content;

import com.polopoly.cm.ContentId;
import com.polopoly.cm.ExternalContentId;
import com.polopoly.cm.client.CMException;
import com.polopoly.cm.client.CMRuntimeException;
import com.polopoly.cm.client.ContentRead;

public class RuntimeExceptionContentWrapper extends DelegatingContent implements
		RuntimeExceptionContent {

	public RuntimeExceptionContentWrapper(ContentRead delegate) {
		super(delegate);
	}

	private RuntimeException toRuntimeException(Exception e, String operation) {
		return new CMRuntimeException("While performing operation " + operation
				+ " on " + this + ": " + e.getMessage(), e);
	}

	@Override
	public String getName() {
		try {
			return super.getName();
		} catch (CMException e) {
			throw toRuntimeException(e, "getName");
		}
	}

	@Override
	public String[] getComponentGroupNames() {
		try {
			return super.getComponentGroupNames();
		} catch (CMException e) {
			throw toRuntimeException(e, "getComponentGroupNames");
		}
	}

	@Override
	public String[] getComponentNames(String groupName) {
		try {
			return super.getComponentNames(groupName);
		} catch (CMException e) {
			throw toRuntimeException(e, "getComponentNames");
		}
	}

	@Override
	public String getComponent(String groupName, String name) {
		try {
			return super.getComponent(groupName, name);
		} catch (CMException e) {
			throw toRuntimeException(e, "getComponent");
		}
	}

	@Override
	public String[] getContentReferenceGroupNames() {
		try {
			return super.getContentReferenceGroupNames();
		} catch (CMException e) {
			throw toRuntimeException(e, "getContentReferenceGroupNames");
		}
	}

	@Override
	public String[] getContentReferenceNames(String groupName) {
		try {
			return super.getContentReferenceNames(groupName);
		} catch (CMException e) {
			throw toRuntimeException(e, "getContentReferenceNames");
		}
	}

	@Override
	public ContentId getContentReference(String groupName, String name) {
		try {
			return super.getContentReference(groupName, name);
		} catch (CMException e) {
			throw toRuntimeException(e, "getContentReference");
		}
	}

	@Override
	public ContentId getInputTemplateId() {
		try {
			return super.getInputTemplateId();
		} catch (CMException e) {
			throw toRuntimeException(e, "getInputTemplateId");
		}
	}

	@Override
	@Deprecated
	public ContentId getOutputTemplateId(String mode) {
		try {
			return super.getOutputTemplateId(mode);
		} catch (CMException e) {
			throw toRuntimeException(e, "getOutputTemplateId");
		}
	}

	@Override
	public void setComponent(String groupName, String name, String value) {
		try {
			super.setComponent(groupName, name, value);
		} catch (CMException e) {
			throw toRuntimeException(e, "setComponent");
		}
	}

	@Override
	public ExternalContentId getExternalId() {
		try {
			return super.getExternalId();
		} catch (CMException e) {
			throw toRuntimeException(e, "getExternalId");
		}
	}

}
