package com.example.test;

import org.junit.Test;

import static org.junit.Assert.*;

import com.txl.blockmoonlighttreasurebox.info.BoxMessage;
import com.txl.blockmoonlighttreasurebox.utils.BoxMessageUtils;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void parse_normal_msg() {
        String msg = ">>>>> Dispatching to Handler (android.view.ViewRootImpl$ViewRootHandler) {3346d43} com.example.test.MainActivity$1@7250fab: 0";
        BoxMessage res = BoxMessageUtils.parseLooperStart(msg);
        assertEquals("android.view.ViewRootImpl$ViewRootHandler", res.getHandleName());
        assertEquals(" com.example.test.MainActivity$1@7250fab", res.getCallbackName());
        assertEquals(0, res.getMessageWhat());
        assertEquals("3346d43", res.getHandlerAddress());
    }
}