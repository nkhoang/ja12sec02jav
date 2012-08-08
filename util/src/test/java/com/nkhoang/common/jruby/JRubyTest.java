package com.nkhoang.common.jruby;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;


public class JRubyTest {
    private static Logger LOGGER = LoggerFactory.getLogger(JRubyTest.class.getCanonicalName());
    ScriptEngineManager scriptManager;
    ScriptEngine engine;

    @Before
    public void init() {
        scriptManager = new ScriptEngineManager();
        engine = scriptManager.getEngineByName("jruby");
    }

    @Test
    public void testRunRubyScript() throws Exception {
        engine.eval("puts 'Hello World'");
    }

    @Test
    public void testRunJRubyScriptWithJavaObject() throws Exception {
        Car aCar = new Car();
        aCar.setColor("red");
        aCar.setWeight(100.11f);
        aCar.setPrice(1000.24);

        engine.put("car", aCar);
        engine.eval("puts $car.getPrice");
    }
}
