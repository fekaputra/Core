package eu.unifiedviews.master.converter;

import java.util.ArrayList;
import java.util.List;

import cz.cuni.mff.xrg.odcs.commons.app.pipeline.Pipeline;
import eu.unifiedviews.master.model.PipelineDTO;

public class PipelineDTOConverter {

    public static PipelineDTO convert(Pipeline pipeline) {
        PipelineDTO dto = null;
        if (pipeline != null) {
            dto = new PipelineDTO();
            dto.setId(pipeline.getId());
            dto.setName(pipeline.getName());
            dto.setDescription(pipeline.getDescription());
            if (pipeline.getOwner() != null) {
                dto.setUserExternalId(pipeline.getOwner().getExternalIdentifier());
            } else {
                dto.setUserExternalId(null);
            }
            if (pipeline.getActor() != null) {
                dto.setUserActorExternalId(pipeline.getActor().getExternalId());
            }
        }
        return dto;
    }

    public static List<PipelineDTO> convert(List<Pipeline> pipelines) {

        if (pipelines == null) {
            pipelines = new ArrayList<>();
        }

        List<PipelineDTO> dtos  = new ArrayList<PipelineDTO>();
        for (Pipeline pipeline : pipelines) {
            PipelineDTO dto = convert(pipeline);
            if (dto != null) {
                dtos.add(dto);
            }
        }

        return dtos;
    }

    public static Pipeline convertFromDTO(PipelineDTO dto, Pipeline pipeline) {
        pipeline.setName(dto.getName());
        pipeline.setDescription(dto.getDescription());
        return pipeline;
    }
}
