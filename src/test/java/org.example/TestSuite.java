package org.example;

import org.junit.platform.suite.api.SelectPackages;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectPackages("org.example") // all test classes in this package will run
public class TestSuite { }