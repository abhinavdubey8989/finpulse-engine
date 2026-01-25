package com.finpulse_engine.psql_enum;

import com.finpulse_engine.enums.SplitType;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.usertype.UserType;

import java.io.Serializable;
import java.sql.*;

public class SplitTypeHandler implements UserType<SplitType> {

    @Override
    public int getSqlType() {
        return Types.OTHER;
    }

    @Override
    public Class<SplitType> returnedClass() {
        return SplitType.class;
    }

    @Override
    public boolean equals(SplitType x, SplitType y) {
        return x == y;
    }

    @Override
    public int hashCode(SplitType x) {
        return x == null ? 0 : x.hashCode();
    }

    @Override
    public SplitType nullSafeGet(ResultSet rs, int position, SharedSessionContractImplementor session, Object owner) 
            throws SQLException {
        String value = rs.getString(position);
        if (value == null) {
            return null;
        }
        return SplitType.valueOf(value);
    }

    @Override
    public void nullSafeSet(PreparedStatement st, SplitType value, int index, SharedSessionContractImplementor session) 
            throws SQLException {
        if (value == null) {
            st.setNull(index, Types.OTHER);
        } else {
            st.setObject(index, value.name(), Types.OTHER);
        }
    }

    @Override
    public SplitType deepCopy(SplitType value) {
        return value;
    }

    @Override
    public boolean isMutable() {
        return false;
    }

    @Override
    public Serializable disassemble(SplitType value) {
        return value;
    }

    @Override
    public SplitType assemble(Serializable cached, Object owner) {
        return (SplitType) cached;
    }

    @Override
    public SplitType replace(SplitType detached, SplitType managed, Object owner) {
        return detached;
    }
}
