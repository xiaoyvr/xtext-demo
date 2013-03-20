
package com.tw.demo;

/**
 * Initialization support for running Xtext languages 
 * without equinox extension registry
 */
public class HKDslStandaloneSetup extends HKDslStandaloneSetupGenerated{

	public static void doSetup() {
		new HKDslStandaloneSetup().createInjectorAndDoEMFRegistration();
	}
}

