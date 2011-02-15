package com.yahoo.hadoop_bsp;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.hadoop.conf.Configurable;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.Writable;

/**
 * A Writable for ListArray containing instances of a class.
 */
public class ArrayListWritable<M extends Writable> extends ArrayList<M>
          implements Writable, Configurable {
    /** Used for instantiation */
    private Class<M> refClass = null;
    /** Defining a layout version for a serializable class. */
    private static final long serialVersionUID = 1L;
    /** Configuration */
    private Configuration conf;

    /**
     * Using the default constructor requires that the user implement
     * setClass(), guaranteed to be invoked prior to instantiation in
     * readFields()
     */
    public ArrayListWritable() {
    }

    /**
     * This constructor allows setting the refClass during construction.
     *
     * @param refClass internal type class
     */
    public ArrayListWritable(Class<M> refClass) {
        super();
        this.refClass = refClass;
    }

    /**
     * This is a one-time operation to set the class type
     *
     * @param refClass internal type class
     */
    public void setClass(Class<M> refClass) {
        if (this.refClass != null) {
            throw new RuntimeException(
                "setClass: refClass is already set to " +
                this.refClass.getName());
        }
        this.refClass = refClass;
    }

    /**
     * Meant to be overriden by subclasses to set the class type
     */
    public void setClass() {
    }

    public void readFields(DataInput in) throws IOException {
        if (this.refClass == null) {
            setClass();
        }
        int numValues = in.readInt();            // read number of values
        ensureCapacity(numValues);
        try {
            for (int i = 0; i < numValues; i++) {
                M value = refClass.newInstance();
                value.readFields(in);                // read a value
                add(value);                          // store it in values
            }
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public void write(DataOutput out) throws IOException {
        int numValues = size();
        out.writeInt(numValues);                 // write number of values
        for (int i = 0; i < numValues; i++) {
            get(i).write(out);
        }
    }

    public final Configuration getConf() {
        return conf;
    }

    public final void setConf(Configuration conf) {
        this.conf = conf;
    }
}
