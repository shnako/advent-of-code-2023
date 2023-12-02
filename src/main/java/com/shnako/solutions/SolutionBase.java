package com.shnako.solutions;

import org.apache.commons.lang3.StringUtils;

public abstract class SolutionBase {
    public abstract String runPart1() throws Exception;

    public abstract String runPart2() throws Exception;

    protected String getDay() {
        return StringUtils.substringAfterLast(this.getClass().getPackageName(), ".").substring(3);
    }
}
