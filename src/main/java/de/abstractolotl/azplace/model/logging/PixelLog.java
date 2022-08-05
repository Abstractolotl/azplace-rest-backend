package de.abstractolotl.azplace.model.logging;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@Builder
@Document(indexName = "backend-pixel")
public class PixelLog {

    @Id
    private String id;

    @Field(type = FieldType.Date) @NotBlank
    private LocalDateTime timestamp;

    @Field(type = FieldType.Integer)
    private Integer userId;

    @Field(type = FieldType.Integer)
    private Integer canvasId;

    @Field(type = FieldType.Keyword)
    private Integer[] position;

    @Field(type = FieldType.Byte)
    private int color;

}
