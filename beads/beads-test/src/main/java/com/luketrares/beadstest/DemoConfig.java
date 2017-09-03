package com.luketrares.beadstest;

import java.util.ArrayList;
import java.util.List;

public class DemoConfig {
    int maxSounds = 32;

    List<Bob> bobs = new ArrayList<Bob>();
    
    void newSounds(double ms, List<DemoElement> demoElements ) {
        for ( Bob bob : bobs ) {
            if ( demoElements.size() >= maxSounds ) return;
            
            boolean skipSingleton = false;
            if ( bob.isSingleton() ) {
                for ( DemoElement demoElement : demoElements ) {
                    if ( demoElement.getInstigator().equals( bob ) ) {
                        skipSingleton = true;
                        break;
                    }
                }
            } //
            
            if ( skipSingleton ) continue;
            
            if ( bob.isActual(ms) ) {
            	DemoElement de = bob.createDemoElement();
            	
            	de.setInstigator(  bob );
                demoElements.add( de );
            } //if
        } //
    }

    
    
    
    
    public void addBob(Bob bob) {
        this.bobs.add(bob);
    }
}
