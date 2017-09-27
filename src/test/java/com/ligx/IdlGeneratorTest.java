package com.ligx;

import org.junit.Test;

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

}