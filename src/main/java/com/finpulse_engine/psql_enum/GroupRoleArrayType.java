package com.finpulse_engine.psql_enum;

import com.finpulse_engine.enums.GroupRole;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.usertype.UserType;
import java.io.Serializable;
import java.sql.*;
import java.util.*;

public class GroupRoleArrayType implements UserType<List<GroupRole>> {

    @Override
    public int getSqlType() {
        return Types.ARRAY;
    }

    @Override
    public Class<List<GroupRole>> returnedClass() {
        return (Class<List<GroupRole>>) ((Class) List.class);
    }

    @Override
    public List<GroupRole> deepCopy(List<GroupRole> value) {
        return value == null ? null : new ArrayList<>(value);
    }

    @Override
    public boolean isMutable() {
        return true;
    }

    @Override
    public List<GroupRole> nullSafeGet(ResultSet rs, int position, SharedSessionContractImplementor session, Object owner) 
            throws SQLException {
        Array array = rs.getArray(position);
        if (array == null) {
            return new ArrayList<>();
        }

        String[] javaArray = (String[]) array.getArray();
        List<GroupRole> result = new ArrayList<>();
        
        for (String value : javaArray) {
            result.add(GroupRole.valueOf(value));
        }
        
        return result;
    }

    @Override
    public void nullSafeSet(PreparedStatement st, List<GroupRole> value, int index, SharedSessionContractImplementor session) 
            throws SQLException {
        if (value == null || value.isEmpty()) {
            st.setNull(index, Types.ARRAY);
        } else {
            String[] stringArray = value.stream()
                    .map(Enum::name)
                    .toArray(String[]::new);
            
            Connection connection = st.getConnection();
            Array array = connection.createArrayOf("group_roles", stringArray);
            st.setArray(index, array);
        }
    }

    @Override
    public boolean equals(List<GroupRole> x, List<GroupRole> y) {
        if (x == y) return true;
        if (x == null || y == null) return false;
        return x.equals(y);
    }

    @Override
    public int hashCode(List<GroupRole> x) {
        return x == null ? 0 : x.hashCode();
    }

    @Override
    public Serializable disassemble(List<GroupRole> value) {
        return (Serializable) deepCopy(value);
    }

    @Override
    public List<GroupRole> assemble(Serializable cached, Object owner) {
        return deepCopy((List<GroupRole>) cached);
    }

    @Override
    public List<GroupRole> replace(List<GroupRole> detached, List<GroupRole> managed, Object owner) {
        return deepCopy(detached);
    }
}