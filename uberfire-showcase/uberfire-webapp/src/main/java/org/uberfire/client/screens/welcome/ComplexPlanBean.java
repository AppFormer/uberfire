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
    private Double dou;
    private double doubp;
    private Float f;
    private float fp;
    private Short s;
    private short sp;
    private SampleEnum enumSample;

    public ComplexPlanBean( String text,
                            boolean bool,
                            Boolean bool2,
                            Integer integ,
                            int inti,
                            Long lon,
                            Double dou,
                            double doubp,
                            Float f,
                            float fp,
                            Short s,
                            short sp,
                            SampleEnum enumSample ) {
        this.text = text;
        this.bool = bool;
        this.bool2 = bool2;
        this.integ = integ;
        this.inti = inti;
        this.lon = lon;
        this.dou = dou;
        this.doubp = doubp;
        this.f = f;
        this.fp = fp;
        this.s = s;
        this.sp = sp;
        this.enumSample = enumSample;
    }

    public ComplexPlanBean(){};


}