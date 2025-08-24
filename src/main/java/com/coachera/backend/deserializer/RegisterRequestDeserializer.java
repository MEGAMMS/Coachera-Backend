package com.coachera.backend.deserializer;

import java.io.IOException;

// import com.coachera.backend.dto.InstructorDTO;
import com.coachera.backend.dto.InstructorRequestDTO;
// import com.coachera.backend.dto.OrganizationDTO;
import com.coachera.backend.dto.OrganizationRequestDTO;
import com.coachera.backend.dto.RegisterRequest;
// import com.coachera.backend.dto.StudentDTO;
import com.coachera.backend.dto.StudentRequestDTO;
import com.coachera.backend.entity.enums.RoleType;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class RegisterRequestDeserializer extends JsonDeserializer<RegisterRequest> {

    @Override
    public RegisterRequest deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
        ObjectMapper mapper = (ObjectMapper) jp.getCodec();
        ObjectNode node = mapper.readTree(jp);

        RegisterRequest request = new RegisterRequest();
        request.setUsername(node.get("username").asText());
        request.setEmail(node.get("email").asText());
        request.setPassword(node.get("password").asText());
        request.setRole(RoleType.valueOf(node.get("role").asText().toUpperCase()));
        if (node.has("profileImageUrl")) {
            request.setProfileImageUrl(node.get("profileImageUrl").asText());
        }

        // Handle details based on role
        JsonNode detailsNode = node.get("details");
        if (detailsNode != null) {
            switch (request.getRole()) {
                case STUDENT:
                    request.setDetails(mapper.treeToValue(detailsNode, StudentRequestDTO.class));
                    break;
                case INSTRUCTOR:
                    request.setDetails(mapper.treeToValue(detailsNode, InstructorRequestDTO.class));
                    break;
                case ORGANIZATION:
                    request.setDetails(mapper.treeToValue(detailsNode, OrganizationRequestDTO.class));
                    break;
                default:
                    throw new RuntimeException("Unknown role: " + request.getRole());
            }
        }

        return request;
    }
}

