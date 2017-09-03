package com.luketrares.beadstest;

import java.lang.reflect.Field;
import java.util.concurrent.ThreadLocalRandom;


class PropertySetter {

    public String getPropertyName() {
        return propertyName;
    }

    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }

    public Object getFixedValue() {
        return fixedValue;
    }

    public void setFixedValue(Object fixedValue) {
        this.fixedValue = fixedValue;
    }

    public Object[] getArrayValue() {
        return arrayValue;
    }

    public void setArrayValue(Object[] arrayValue) {
        this.arrayValue = arrayValue;
    }
    String propertyName;
    Object fixedValue;
    Double[] rangedValue;

    public Double[] getRangedValue() {
        return rangedValue;
    }

    public void setRangedValue(Double[] rangedValue) {
        this.rangedValue = rangedValue;
    }
    Object[] arrayValue;

    void set(DemoElement demoElement) throws NoSuchFieldException, IllegalArgumentException, IllegalAccessException {

        try {
            Field f = demoElement.getClass().getDeclaredField(propertyName);
            f.setAccessible(true);
            if (fixedValue != null) {
                f.set(demoElement, fixedValue);
                return;
            }

            if (rangedValue != null) {
                double b = rangedValue[0].doubleValue();
                double t = rangedValue[1].doubleValue();

                double diff = t - b;

                double result = b + ThreadLocalRandom.current().nextDouble() * diff;

                f.set(demoElement, getNumberType(f.getType(), result));

                System.out.println("setting " + f.getName() + " on " + demoElement.getClass().getName() + " to " + result);
                return;
            } //

            if (arrayValue != null) {
                f.set(demoElement, arrayValue[ThreadLocalRandom.current().nextInt(arrayValue.length)]);
                return;
            } //

            f.set(demoElement, null);

        } catch (Exception e) {
            System.out.println("unable to set property " + propertyName + " on " + demoElement.getClass().getName() );
            e.printStackTrace();
        }

    }

    private Object getNumberType(Class<?> type, Double value) {
        if (value == null) {
            return null;
        }

        if (type.equals(Double.class)) {
            return value;
        }

        if (type.equals(Long.class)) {
            return value.longValue();
        }

        if (type.equals(Integer.class)) {
            return value.intValue();
        }

        if (type.equals(Float.class)) {
            return value.floatValue();
        }

        if (type.equals(Byte.class)) {
            return value.byteValue();
        }

        return value;

    }

}
