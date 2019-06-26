package com.beifang.util.func;

import com.beifang.model.Expert;
import com.google.common.base.Function;

public class ExpertGetIdFunction implements Function<Expert, Long> {

	@Override
	public Long apply(Expert input) {
		return input.getId();
	}

}
