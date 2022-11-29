
package com.weidentity.weid.cpt2000000;

import java.util.HashMap;
import java.util.Map;


/**
 * cpt
 * <p>
 * this is cpt
 * 
 */
public class Cpt2000000 {

    /**
     * the age of certificate owner
     * (Required)
     * 
     */
    private Double age;
    /**
     * the gender of certificate owner
     * 
     */
    private Cpt2000000 .Gender gender;
    /**
     * the name of certificate owner
     * (Required)
     * 
     */
    private String name;

    /**
     * the age of certificate owner
     * (Required)
     * 
     */
    public Double getAge() {
        return age;
    }

    /**
     * the age of certificate owner
     * (Required)
     * 
     */
    public void setAge(Double age) {
        this.age = age;
    }

    public Cpt2000000 withAge(Double age) {
        this.age = age;
        return this;
    }

    /**
     * the gender of certificate owner
     * 
     */
    public Cpt2000000 .Gender getGender() {
        return gender;
    }

    /**
     * the gender of certificate owner
     * 
     */
    public void setGender(Cpt2000000 .Gender gender) {
        this.gender = gender;
    }

    public Cpt2000000 withGender(Cpt2000000 .Gender gender) {
        this.gender = gender;
        return this;
    }

    /**
     * the name of certificate owner
     * (Required)
     * 
     */
    public String getName() {
        return name;
    }

    /**
     * the name of certificate owner
     * (Required)
     * 
     */
    public void setName(String name) {
        this.name = name;
    }

    public Cpt2000000 withName(String name) {
        this.name = name;
        return this;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(Cpt2000000 .class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("age");
        sb.append('=');
        sb.append(((this.age == null)?"<null>":this.age));
        sb.append(',');
        sb.append("gender");
        sb.append('=');
        sb.append(((this.gender == null)?"<null>":this.gender));
        sb.append(',');
        sb.append("name");
        sb.append('=');
        sb.append(((this.name == null)?"<null>":this.name));
        sb.append(',');
        if (sb.charAt((sb.length()- 1)) == ',') {
            sb.setCharAt((sb.length()- 1), ']');
        } else {
            sb.append(']');
        }
        return sb.toString();
    }

    @Override
    public int hashCode() {
        int result = 1;
        result = ((result* 31)+((this.name == null)? 0 :this.name.hashCode()));
        result = ((result* 31)+((this.gender == null)? 0 :this.gender.hashCode()));
        result = ((result* 31)+((this.age == null)? 0 :this.age.hashCode()));
        return result;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof Cpt2000000) == false) {
            return false;
        }
        Cpt2000000 rhs = ((Cpt2000000) other);
        return ((((this.name == rhs.name)||((this.name!= null)&&this.name.equals(rhs.name)))&&((this.gender == rhs.gender)||((this.gender!= null)&&this.gender.equals(rhs.gender))))&&((this.age == rhs.age)||((this.age!= null)&&this.age.equals(rhs.age))));
    }

    public enum Gender {

        F("F"),
        M("M");
        private final String value;
        private final static Map<String, Cpt2000000 .Gender> CONSTANTS = new HashMap<String, Cpt2000000 .Gender>();

        static {
            for (Cpt2000000 .Gender c: values()) {
                CONSTANTS.put(c.value, c);
            }
        }

        private Gender(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return this.value;
        }

        public String value() {
            return this.value;
        }

        public static Cpt2000000 .Gender fromValue(String value) {
            Cpt2000000 .Gender constant = CONSTANTS.get(value);
            if (constant == null) {
                throw new IllegalArgumentException(value);
            } else {
                return constant;
            }
        }

    }

}
