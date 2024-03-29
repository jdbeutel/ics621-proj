package com.sun.electric.tool.simulation.test;

import java.util.List;
import java.util.ArrayList;
import java.util.Random;
import java.util.Iterator;

/*
 * VerilogScan.java
 *
 * Copyright (c) 2004,2005 by Sun Microsystems, Inc.
 *
 * Created on Apr 20, 2005
 */

/**
 * A JtagTester that interfaces with a verilog model of the Device Under Test.
 *
 * @author gainsley
 */
public class VerilogJtagTester extends JtagTesterModel {

    /**
     * Create a new Verilog JtagTester.  This implements a JtagTester, but does so
     * as an interface to a Verilog simulation of the chip under test.  This should only
     * be called by VerilogModel.
     * @param vm the verilog model that the tester will interface with
     * @param tck the name of the tck port
     * @param tms the name of the tms port
     * @param trstb the name of the trstb port
     * @param tdi the name of the tdi port
     * @param tdob the name of the tdob port
     */
    VerilogJtagTester(VerilogModel vm, String tck, String tms, String trstb, String tdi, String tdob) {
        super(vm, tck, tms, trstb, tdi, tdob);
    }

    String formatDataNetName(String dataNetName) {
        return VerilogModel.formatDataNetName(dataNetName);
    }


    /** Unit test */
    public static void main(String args[]) {
        VerilogModel vm = new VerilogModel();
        VerilogJtagTester tester = (VerilogJtagTester)vm.createJtagTester("TCK", "TMS", "TRSTb", "TDI", "TDOb");
        vm.start("verilog", VerilogModel.getExampleVerilogChipFile(), VerilogModel.NORECORD);

        // test private methods separately
        tester.reset();
        tester.task_load_instruction("11000010");
        tester.task_scan_data("1000100010001111");

        // test public methods which use private methods
        ChainNode testNode = new ChainNode("testNode", "1001", 156, "node for unit test");
        Random rand = new Random(309402934);
        for (int i=0; i<testNode.getInBits().getNumBits(); i++) {
            testNode.getInBits().set(i, rand.nextBoolean());
        }
        System.out.println("Note that data shifted out is inverted sense of data shifted in,");
        System.out.println("  unless it goes through one of our inverting output pads first.");
        System.out.println("Unit Test: Shifting in: "+testNode.getInBits().getState());
        tester.shift(testNode, true, true, 0);
        System.out.println("Unit Test: Shifted out: "+testNode.getOutBits().getState());

        // We sent to a chain that had no elements, so scan in == scan out.
        boolean match = true;
        for (int i=0; i<testNode.getInBits().getNumBits(); i++) {
            if (testNode.getInBits().get(i) != !testNode.getOutBits().get(i)) {
                match = false;
                break;
            }
        }
        if (!match) {
            System.out.println("Unit Test Error: scan data in should match scan data out when chain is looped back.");
        } else {
            System.out.println("Unit Test OK.");
        }
        vm.finish();
    }
}
