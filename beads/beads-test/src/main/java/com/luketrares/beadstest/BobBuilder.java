package com.luketrares.beadstest;

public class BobBuilder {

    Bob bob = new Bob();
    
    public Bob build() {
        return bob;
    }    
    public BobBuilder recorderElement() {
        bob.setDemoElementClass( Recorder.class );
        return this;
    }

    BobBuilder rangedProperty(String propertyName, double bottom, double top) {
        PropertySetter ps = new PropertySetter();
        ps.setPropertyName( propertyName);
        ps.setRangedValue( new Double[]{ bottom, top } );
        bob.getPropertySetters().add(ps);
        return this;
    }

    BobBuilder probability(double d) {
        bob.setProbability(d);
        return this;
    }

   BobBuilder singleton(boolean b) {
       bob.setSingleton(true);
       return this;
    }

    BobBuilder demoSample() {
        bob.setDemoElementClass( DemoSample.class );
        return this;
    }

    BobBuilder arrayProperty(String propertyName, Object[] array ) {
        PropertySetter ps = new PropertySetter();
        ps.setPropertyName(propertyName);
        ps.setArrayValue( array );
        bob.getPropertySetters().add(ps);
        return this;
    }
    
    BobBuilder darkBell() {
        bob.setDemoElementClass( DarkBell.class );
        return this;
    }
    
    BobBuilder fixedProperty(String propertyName, Object value ) {
        PropertySetter ps = new PropertySetter();
        ps.setPropertyName(propertyName);
        ps.setFixedValue( value );
        bob.getPropertySetters().add(ps);
        return this;
        
    }

    
    
}
