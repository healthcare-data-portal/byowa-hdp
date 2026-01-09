package dev.byowa.hdp.dto;

import java.time.Instant;
import java.time.LocalDate;

public class MeasurementResponse {
    private Integer id;

    // patient key (person_source_value), z.B. "patient002"
    private String patientId;

    private LocalDate measurementDate;
    private Instant measurementDatetime;

    // what was measured (your short label)
    private String source; // measurement_source_value

    // values (either numeric or text)
    private String value;  // "82.00" or "A" etc.
    private String unit;   // "beats/min", "kg", "%", ...

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getPatientId() { return patientId; }
    public void setPatientId(String patientId) { this.patientId = patientId; }

    public LocalDate getMeasurementDate() { return measurementDate; }
    public void setMeasurementDate(LocalDate measurementDate) { this.measurementDate = measurementDate; }

    public Instant getMeasurementDatetime() { return measurementDatetime; }
    public void setMeasurementDatetime(Instant measurementDatetime) { this.measurementDatetime = measurementDatetime; }

    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }

    public String getValue() { return value; }
    public void setValue(String value) { this.value = value; }

    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }
}
