package com.fs.fsapi.entity.release;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(
    componentModel = MappingConstants.ComponentModel.SPRING,
    unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface ReleaseMapper {

    public Release releaseParseResultToRelease(ReleaseParseResult source);

    public void updateReleaseFromReleaseUpdate(ReleaseUpdate source, @MappingTarget Release target);
}
