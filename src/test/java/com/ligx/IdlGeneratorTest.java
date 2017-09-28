package com.ligx;

import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.*;

/**
 * Author: ligongxing.
 * Date: 2017年09月27日.
 */
public class IdlGeneratorTest {

    @Test
    public void generateIdl() throws Exception {
        IdlGenerator.generateIdl("/Users/lgx/dev/idl-generator-test/demo.thrift", "com.ligx");
    }

    @Test
    public void test() {
        Class clazz = int.class;

        Date date = new Date();

        Integer i = 100000;

        System.out.println(i.getClass() == Integer.class);
    }
}