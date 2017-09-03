package com.luketrares.beadstest;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Level;
import java.util.logging.Logger;

class Bob {

   public double getProbability() {
       return probability;
   }

   public void setProbability(double probability) {
       this.probability = probability;
   }

   public Class<? extends DemoElement> getDemoElementClass() {
       return demoElementClass;
   }

   public void setDemoElementClass(Class<? extends DemoElement> demoElementClass) {
       this.demoElementClass = demoElementClass;
   }

   public List<PropertySetter> getPropertySetters() {
       return propertySetters;
   }

   public void setPropertySetters(List<PropertySetter> propertySetters) {
       this.propertySetters = propertySetters;
   }
   double probability;
   List<Bob> bobGroup;
   Class<? extends DemoElement> demoElementClass;
   List<PropertySetter> propertySetters = new ArrayList<>();
   boolean singleton;

   public boolean isSingleton() {
       return singleton;
   }

   public void setSingleton(boolean singleton) {
       this.singleton = singleton;
   }
   

   DemoElement createDemoElement() {
       DemoElement sound;
       try {
           sound = demoElementClass.newInstance();
           sound.setInstigator(this);
           for ( PropertySetter setter : propertySetters ) {
               setter.set( sound );
           }
           sound.initialize();
           
       } catch (Exception e) {
           Logger.getLogger(Bob.class.getName()).log(Level.SEVERE, null, e );
           throw new RuntimeException( "unable to create sound", e );
       }
       
       
       return sound;
   }

   boolean isActual(double ms) {
       return (ThreadLocalRandom.current().nextDouble() < this.probability*ms);
   }
   
}