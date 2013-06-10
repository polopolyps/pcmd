package com.polopoly.ps.contentimporter.hotdeploy.client;

public enum Major
    implements Comparable<Major>
{
    UNKNOWN(-1, "unknown"),
    MAJOR_CONFIG(0, "majorconfig"),
    ARTICLE(1, "article"),
    DEPARTMENT(2, "department"),
    CONTENT(3, "content"),
    LAYOUT_ELEMENT(7, "layoutelement"),
    WORKFLOW_TYPE(10, "workflowtype"),
    WORKFLOW(11, "workflow"),
    REFERENCE_METADATA(13, "referencemetadata"),
    INPUT_TEMPLATE(14, "inputtemplate"),
    OUTPUT_TEMPLATE(15, "outputtemplate"),
    APP_CONFIG(17, "appconfig"),
    USER(18, "userdata"),
    COMMUNITY(19, "community");

    static {
        ALL_MAJORS = new Major[] { MAJOR_CONFIG, ARTICLE, DEPARTMENT, CONTENT, LAYOUT_ELEMENT, WORKFLOW,
                                   WORKFLOW_TYPE, REFERENCE_METADATA, INPUT_TEMPLATE, OUTPUT_TEMPLATE, APP_CONFIG, USER, COMMUNITY };
    }

    private static Major[] ALL_MAJORS;
    private int integerMajor;
    private String name;

    private Major(final int integerMajor,
                  final String name)
    {
        this.integerMajor = integerMajor;
        this.name = name;
    }

    public static Major getMajor(final String majorName)
    {
        for (Major major : ALL_MAJORS) {
            if (major.getName().equalsIgnoreCase(majorName) ||
                Integer.toString(major.getIntegerMajor()).equals(majorName)) {

                return major;
            }
        }

        return UNKNOWN;
    }

    public static Major getMajor(final int integerMajor)
    {
        for (Major major : ALL_MAJORS) {
            if (major.getIntegerMajor() == integerMajor) {
                return major;
            }
        }

        return UNKNOWN;
    }

    public int getIntegerMajor()
    {
        return integerMajor;
    }

    public String getName()
    {
        return name;
    }

    @Override
    public String toString()
    {
        return getName();
    }

    public int compare(final Major major)
    {
        return new Integer(getIntegerMajor()).compareTo(major.getIntegerMajor());
    }
}
