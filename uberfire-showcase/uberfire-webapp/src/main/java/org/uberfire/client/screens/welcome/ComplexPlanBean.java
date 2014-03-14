package org.uberfire.client.screens.welcome;

import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public class ComplexPlanBean {

    private String text;
    private boolean bool;
    private Boolean bool2;
    private Integer integ;
    private int inti;
    private Long lon;
    private SampleEnum enumSample;

    public ComplexPlanBean(){};

    public ComplexPlanBean( String text,
                            boolean bool,
                            Boolean bool2,
                            Integer integ,
                            int inti,
                            Long lon,
                            SampleEnum enumSample ) {
        this.text = text;
        this.bool = bool;
        this.bool2 = bool2;
        this.integ = integ;
        this.inti = inti;
        this.lon = lon;
        this.enumSample = enumSample;
    }
}
