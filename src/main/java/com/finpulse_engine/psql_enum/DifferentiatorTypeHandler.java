package com.finpulse_engine.psql_enum;

import com.finpulse_engine.enums.DifferentiatorType;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.usertype.UserType;

import java.io.Serializable;
import java.sql.*;

public class DifferentiatorTypeHandler implements UserType<DifferentiatorType> {

    @Override
    public int getSqlType() {
        return Types.OTHER;
    }

    @Override
    public Class<DifferentiatorType> returnedClass() {
        return DifferentiatorType.class;
    }

    @Override
    public boolean equals(DifferentiatorType x, DifferentiatorType y) {
        return x == y;
    }

    @Override
    public int hashCode(DifferentiatorType x) {
        return x == null ? 0 : x.hashCode();
    }

    @Override
    public DifferentiatorType nullSafeGet(ResultSet rs, int position, SharedSessionContractImplementor session, Object owner) 
            throws SQLException {
        String value = rs.getString(position);
        if (value == null) {
            return null;
        }
        return DifferentiatorType.valueOf(value);
    }

    @Override
    public void nullSafeSet(PreparedStatement st, DifferentiatorType value, int index, SharedSessionContractImplementor session) 
            throws SQLException {
        if (value == null) {
            st.setNull(index, Types.OTHER);
        } else {
            st.setObject(index, value.name(), Types.OTHER);
        }
    }

    @Override
    public DifferentiatorType deepCopy(DifferentiatorType value) {
        return value;
    }

    @Override
    public boolean isMutable() {
        return false;
    }

    @Override
    public Serializable disassemble(DifferentiatorType value) {
        return value;
    }

    @Override
    public DifferentiatorType assemble(Serializable cached, Object owner) {
        return (DifferentiatorType) cached;
    }

    @Override
    public DifferentiatorType replace(DifferentiatorType detached, DifferentiatorType managed, Object owner) {
        return detached;
    }
}
