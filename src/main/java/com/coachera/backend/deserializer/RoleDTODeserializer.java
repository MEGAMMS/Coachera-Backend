package com.coachera.backend.deserializer;

import java.io.IOException;

import com.coachera.backend.dto.InstructorDTO;
import com.coachera.backend.dto.OrganizationDTO;
import com.coachera.backend.dto.RoleDTO;
import com.coachera.backend.dto.StudentDTO;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class RoleDTODeserializer extends JsonDeserializer<RoleDTO> {

    @Override
    public RoleDTO deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
        ObjectMapper mapper = (ObjectMapper) jp.getCodec();
        ObjectNode node = mapper.readTree(jp);

        // role is in the parent object
        JsonNode parent = node;
        String role = (parent.has("role") ? parent.get("role").asText() : null);

        if (role == null) {
            throw new RuntimeException("Role is required to deserialize details");
        }

        switch (role.toUpperCase()) {
            case "STUDENT":
                return mapper.treeToValue(node, StudentDTO.class);
            case "INSTRUCTOR":
                return mapper.treeToValue(node, InstructorDTO.class);
            case "ORGANIZATION":
                return mapper.treeToValue(node, OrganizationDTO.class);
            default:
                throw new RuntimeException("Unknown role: " + role);
        }
    }
}

