package com.beifang.util.func;

import com.beifang.service.dto.PackageDto;
import com.google.common.base.Function;

public class PkgDtoGetIdFunction implements Function<PackageDto, Long> {

	@Override
	public Long apply(PackageDto input) {
		return input.getId();
	}

}
