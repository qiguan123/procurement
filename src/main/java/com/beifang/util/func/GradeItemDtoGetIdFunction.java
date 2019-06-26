package com.beifang.util.func;

import com.beifang.service.dto.GradeItemDto;
import com.google.common.base.Function;

public class GradeItemDtoGetIdFunction implements Function<GradeItemDto, Long> {

	@Override
	public Long apply(GradeItemDto input) {
		return input.getId();
	}

}
