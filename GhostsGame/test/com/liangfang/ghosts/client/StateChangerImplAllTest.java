package com.liangfang.ghosts.client;



public class StateChangerImplAllTest extends AbstractStateChangerAllTest {

	@Override
	public StateChanger getStateChanger() {
		return new StateChangerImpl();
	}
	
}