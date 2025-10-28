package com.chatq.assist.config;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.usertype.UserType;
import org.postgresql.util.PGobject;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

/**
 * Custom Hibernate UserType for pgvector's vector type
 */
public class VectorType implements UserType<float[]> {

    @Override
    public int getSqlType() {
        return Types.OTHER;
    }

    @Override
    public Class<float[]> returnedClass() {
        return float[].class;
    }

    @Override
    public boolean equals(float[] x, float[] y) throws HibernateException {
        if (x == y) return true;
        if (x == null || y == null) return false;
        if (x.length != y.length) return false;
        for (int i = 0; i < x.length; i++) {
            if (Float.compare(x[i], y[i]) != 0) return false;
        }
        return true;
    }

    @Override
    public int hashCode(float[] x) throws HibernateException {
        return x != null ? java.util.Arrays.hashCode(x) : 0;
    }

    @Override
    public float[] nullSafeGet(ResultSet rs, int position, SharedSessionContractImplementor session, Object owner)
            throws SQLException {
        String value = rs.getString(position);
        if (value == null) {
            return null;
        }
        return parseVector(value);
    }

    @Override
    public void nullSafeSet(PreparedStatement st, float[] value, int index, SharedSessionContractImplementor session)
            throws SQLException {
        if (value == null) {
            st.setNull(index, Types.OTHER);
        } else {
            PGobject pgObject = new PGobject();
            pgObject.setType("vector");
            pgObject.setValue(formatVector(value));
            st.setObject(index, pgObject);
        }
    }

    @Override
    public float[] deepCopy(float[] value) throws HibernateException {
        return value == null ? null : value.clone();
    }

    @Override
    public boolean isMutable() {
        return true;
    }

    @Override
    public Serializable disassemble(float[] value) throws HibernateException {
        return deepCopy(value);
    }

    @Override
    public float[] assemble(Serializable cached, Object owner) throws HibernateException {
        return deepCopy((float[]) cached);
    }

    @Override
    public float[] replace(float[] detached, float[] managed, Object owner) throws HibernateException {
        return deepCopy(detached);
    }

    /**
     * Parse PostgreSQL vector format: [0.1,0.2,0.3] -> float[]
     */
    private float[] parseVector(String value) {
        if (value == null || value.isEmpty()) {
            return null;
        }

        // Remove brackets
        String trimmed = value.trim();
        if (trimmed.startsWith("[") && trimmed.endsWith("]")) {
            trimmed = trimmed.substring(1, trimmed.length() - 1);
        }

        if (trimmed.isEmpty()) {
            return new float[0];
        }

        String[] parts = trimmed.split(",");
        float[] result = new float[parts.length];
        for (int i = 0; i < parts.length; i++) {
            result[i] = Float.parseFloat(parts[i].trim());
        }
        return result;
    }

    /**
     * Format float[] to PostgreSQL vector format: float[] -> [0.1,0.2,0.3]
     */
    private String formatVector(float[] value) {
        if (value == null || value.length == 0) {
            return "[]";
        }

        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < value.length; i++) {
            if (i > 0) sb.append(",");
            sb.append(value[i]);
        }
        sb.append("]");
        return sb.toString();
    }
}
