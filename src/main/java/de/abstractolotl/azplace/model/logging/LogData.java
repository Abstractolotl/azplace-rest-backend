package de.abstractolotl.azplace.model.logging;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@Builder
@Document(indexName = "backend-logs")
public class LogData {

    @Id
    private String id;

    @Field(type = FieldType.Date) @NotBlank
    private LocalDateTime timestamp;

    @Field(type = FieldType.Keyword) @NotNull
    private String message;

    @Field(type = FieldType.Keyword)
    private String controller;

}
