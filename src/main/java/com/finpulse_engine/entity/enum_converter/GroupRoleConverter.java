package com.finpulse_engine.entity.enum_converter;


import com.finpulse_engine.enums.GroupRole;
import com.finpulse_engine.enums.UserRole;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;



@Converter(autoApply = false)
public class GroupRoleConverter
        implements AttributeConverter<List<GroupRole>, String> {

    @Override
    public String convertToDatabaseColumn(List<GroupRole> attribute) {
        if (attribute == null || attribute.isEmpty()) {
            return null;
        }

        // Create PostgreSQL array literal with explicit casting
        StringBuilder sb = new StringBuilder("ARRAY[");
        for (int i = 0; i < attribute.size(); i++) {
            if (i > 0) sb.append(",");
            sb.append("'").append(attribute.get(i).name()).append("'::group_roles");
        }
        sb.append("]");
        return sb.toString();
    }

    @Override
    public List<GroupRole> convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isEmpty()) {
            return new ArrayList<>();
        }

        // Parse the array string - remove ARRAY[] wrapper
        String arrayContent = dbData.replace("ARRAY[", "").replace("]", "");
        if (arrayContent.isEmpty()) {
            return new ArrayList<>();
        }

        // Split and parse enum names
        String[] enumNames = arrayContent.split(",");
        List<GroupRole> result = new ArrayList<>();

        for (String enumStr : enumNames) {
            // Remove quotes and casting
            String enumName = enumStr.trim()
                    .replace("'", "")
                    .replace("::group_roles", "");
            result.add(GroupRole.valueOf(enumName));
        }

        return result;
    }
}