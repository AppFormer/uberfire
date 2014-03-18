package org.uberfire.org.uberfire.server.impl.beans;


public class ComplexPlanBean {

    private String text;
    private boolean bool;
    private Boolean bool2;
    private Integer integ;
    private int inti;
    private Long lon;
    private long plong;
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
                            long plong,
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
        this.plong = plong;
        this.dou = dou;
        this.doubp = doubp;
        this.f = f;
        this.fp = fp;
        this.s = s;
        this.sp = sp;
        this.enumSample = enumSample;
    }

    public ComplexPlanBean(){};

    public ComplexPlanBean( String text,
                            boolean bool,
                            Boolean bool2,
                            Integer integ,
                            int inti,
                            Long lon,
                            long plong) {
        this.text = text;
        this.bool = bool;
        this.bool2 = bool2;
        this.integ = integ;
        this.inti = inti;
        this.lon = lon;
        this.plong = plong;
    }

    public ComplexPlanBean( String text,
                            boolean bool,
                            boolean bool2,
                            Integer integ,
                            int inti,
                            long lon,
                            long plong,
                            SampleEnum enumSample ) {
        this.text = text;
        this.bool = bool;
        this.bool2 = bool2;
        this.integ = integ;
        this.inti = inti;
        this.lon = lon;
        this.plong = plong;
        this.enumSample = enumSample;
    }
}
