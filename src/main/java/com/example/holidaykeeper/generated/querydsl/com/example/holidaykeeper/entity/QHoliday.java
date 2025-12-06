package com.example.holidaykeeper.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QHoliday is a Querydsl query type for Holiday
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QHoliday extends EntityPathBase<Holiday> {

    private static final long serialVersionUID = -76413790L;

    public static final QHoliday holiday = new QHoliday("holiday");

    public final StringPath counties = createString("counties");

    public final StringPath countryCode = createString("countryCode");

    public final DateTimePath<java.time.OffsetDateTime> createdAt = createDateTime("createdAt", java.time.OffsetDateTime.class);

    public final DatePath<java.time.LocalDate> date = createDate("date", java.time.LocalDate.class);

    public final BooleanPath fixed = createBoolean("fixed");

    public final BooleanPath global = createBoolean("global");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final NumberPath<Integer> launchYear = createNumber("launchYear", Integer.class);

    public final StringPath localName = createString("localName");

    public final StringPath name = createString("name");

    public final StringPath type = createString("type");

    public QHoliday(String variable) {
        super(Holiday.class, forVariable(variable));
    }

    public QHoliday(Path<? extends Holiday> path) {
        super(path.getType(), path.getMetadata());
    }

    public QHoliday(PathMetadata metadata) {
        super(Holiday.class, metadata);
    }

}

