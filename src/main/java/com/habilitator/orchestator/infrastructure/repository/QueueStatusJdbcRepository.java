package com.habilitator.orchestator.infrastructure.repository;

import com.habilitator.orchestator.domain.model.QueueStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

@Repository
public class QueueStatusJdbcRepository implements QueueStatusRepository {

    private final JdbcTemplate jdbcTemplate;

    @Value("${queue.table-name}")
    private String tableName;

    public QueueStatusJdbcRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    @Transactional
    public List<QueueStatus> findAndMarkInProcess() {
        String selectSql = String.format("SELECT ID, INFO, STATUS, APIGEE_PROXY_NAME FROM %s WHERE STATUS = 0 FOR UPDATE", tableName);

        List<QueueStatus> rows = jdbcTemplate.query(selectSql, (rs, rowNum) -> {
            QueueStatus status = new QueueStatus();
            status.setId(rs.getLong("ID"));
            status.setInfo(rs.getString("INFO"));
            status.setStatus(rs.getInt("STATUS"));
            return status;
        });

        String updateSql = String.format("UPDATE %s SET STATUS = 1 WHERE ID = ?", tableName);

        jdbcTemplate.batchUpdate(updateSql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ps.setLong(1, rows.get(i).getId());
            }

            @Override
            public int getBatchSize() {
                return rows.size();
            }
        });

        return rows;
    }
}
