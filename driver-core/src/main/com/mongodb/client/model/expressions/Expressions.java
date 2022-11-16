/*
 * Copyright 2008-present MongoDB, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mongodb.client.model.expressions;

import com.mongodb.assertions.Assertions;
import com.mongodb.lang.NonNull;
import org.bson.BsonArray;
import org.bson.BsonBoolean;
import org.bson.BsonDateTime;
import org.bson.BsonDecimal128;
import org.bson.BsonDocument;
import org.bson.BsonDouble;
import org.bson.BsonInt32;
import org.bson.BsonInt64;
import org.bson.BsonNull;
import org.bson.BsonString;
import org.bson.BsonValue;
import org.bson.conversions.Bson;
import org.bson.types.Decimal128;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.mongodb.client.model.expressions.MqlExpression.AstPlaceholder;

/**
 * Convenience methods related to {@link Expression}.
 */
public final class Expressions {

    private Expressions() {}

    /**
     * Returns an expression having the same boolean value as the provided
     * boolean primitive.
     *
     * @param of the boolean primitive
     * @return the boolean expression
     */
    public static BooleanExpression of(final boolean of) {
        // we intentionally disallow ofBoolean(null)
        return new MqlExpression<>((codecRegistry) -> new AstPlaceholder(new BsonBoolean(of)));
    }

    /**
     * Returns an array expression containing the same boolean values as the
     * provided array of booleans.
     *
     * @param array the array of booleans
     * @return the boolean array expression
     */
    public static ArrayExpression<BooleanExpression> ofBooleanArray(final boolean... array) {
        List<BsonValue> result = new ArrayList<>();
        for (boolean b : array) {
            result.add(new BsonBoolean(b));
        }
        return new MqlExpression<>((cr) -> new AstPlaceholder(new BsonArray(result)));
    }

    /**
     * Returns an expression having the same integer value as the provided
     * int primitive.
     *
     * @param of the int primitive
     * @return the integer expression
     */
    public static IntegerExpression of(final int of) {
        return new MqlExpression<>((codecRegistry) -> new AstPlaceholder(new BsonInt32(of)));
    }

    public static ArrayExpression<IntegerExpression> ofIntegerArray(final int... array) {
        List<BsonValue> list = Arrays.stream(array)
                .mapToObj(BsonInt32::new)
                .collect(Collectors.toList());
        return new MqlExpression<>((cr) -> new AstPlaceholder(new BsonArray(list)));
    }

    public static IntegerExpression of(final long of) {
        return new MqlExpression<>((codecRegistry) -> new AstPlaceholder(new BsonInt64(of)));
    }

    public static ArrayExpression<IntegerExpression> ofIntegerArray(final long... array) {
        List<BsonValue> list = Arrays.stream(array)
                .mapToObj(BsonInt64::new)
                .collect(Collectors.toList());
        return new MqlExpression<>((cr) -> new AstPlaceholder(new BsonArray(list)));
    }

    public static NumberExpression of(final double of) {
        return new MqlExpression<>((codecRegistry) -> new AstPlaceholder(new BsonDouble(of)));
    }

    public static ArrayExpression<IntegerExpression> ofNumberArray(final double... array) {
        List<BsonValue> list = Arrays.stream(array)
                .mapToObj(BsonDouble::new)
                .collect(Collectors.toList());
        return new MqlExpression<>((cr) -> new AstPlaceholder(new BsonArray(list)));
    }

    public static NumberExpression of(@NonNull final BigDecimal of) {
        Assertions.notNull("BigDecimal", of);
        return new MqlExpression<>((codecRegistry) -> new AstPlaceholder(new BsonDecimal128(new Decimal128(of))));
    }

    public static ArrayExpression<IntegerExpression> ofNumberArray(final BigDecimal... array) {
        List<BsonValue> result = new ArrayList<>();
        for (BigDecimal e : array) {
            Assertions.notNull("elements of array", e);
            result.add(new BsonDecimal128(new Decimal128(e)));
        }
        return new MqlExpression<>((cr) -> new AstPlaceholder(new BsonArray(result)));
    }

    public static NumberExpression of(final Decimal128 of) {
        Assertions.notNull("Decimal128", of);
        return new MqlExpression<>((codecRegistry) -> new AstPlaceholder(new BsonDecimal128(of)));
    }

    public static ArrayExpression<IntegerExpression> ofNumberArray(final Decimal128... array) {
        List<BsonValue> result = new ArrayList<>();
        for (Decimal128 e : array) {
            Assertions.notNull("elements of array", e);
            result.add(new BsonDecimal128(e));
        }
        return new MqlExpression<>((cr) -> new AstPlaceholder(new BsonArray(result)));
    }

    public static DateExpression of(@NonNull final Instant of) {
        Assertions.notNull("Instant", of);
        return new MqlExpression<>((codecRegistry) -> new AstPlaceholder(new BsonDateTime(of.toEpochMilli())));
    }

    public static ArrayExpression<DateExpression> ofDateArray(final Instant... array) {
        List<BsonValue> result = new ArrayList<>();
        for (Instant e : array) {
            Assertions.notNull("elements of array", e);
            result.add(new BsonDateTime(e.toEpochMilli()));
        }
        return new MqlExpression<>((cr) -> new AstPlaceholder(new BsonArray(result)));
    }

    /**
     * Returns an expression having the same string value as the provided
     * string.
     *
     * @param of the string
     * @return the string expression
     */
    public static StringExpression of(@NonNull final String of) {
        Assertions.notNull("String", of);
        return new MqlExpression<>((codecRegistry) -> new AstPlaceholder(new BsonString(of)));
    }


    public static ArrayExpression<StringExpression> ofStringArray(final String... array) {
        List<BsonValue> result = new ArrayList<>();
        for (String e : array) {
            Assertions.notNull("elements of array", e);
            result.add(new BsonString(e));
        }
        return new MqlExpression<>((cr) -> new AstPlaceholder(new BsonArray(result)));
    }

    @SafeVarargs  // nothing is stored in the array
    public static <T extends Expression> ArrayExpression<T> ofArray(final T... array) {
        Assertions.notNull("array", array);
        return new MqlExpression<>((cr) -> {
            List<BsonValue> array2 = Arrays.stream(array)
                    .map(v -> ((MqlExpression<?>) v).toBsonValue(cr))
                    .collect(Collectors.toList());
            return new AstPlaceholder(new BsonArray(array2));
        });
    }

    public static DocumentExpression ofDocument(final Bson document) {
        Assertions.notNull("document", document);
        // All documents are wrapped in a $literal. If we don't wrap, we need to
        // check for empty documents and documents that are actually expressions
        // (and need to be wrapped in $literal anyway). This would be brittle.
        return new MqlExpression<>((cr) -> new AstPlaceholder(new BsonDocument("$literal",
                document.toBsonDocument(BsonDocument.class, cr))));
    }

    public static Expression ofNull() {
        // There is no specific expression type corresponding to Null,
        // and Null is not a value in any other expression type.
        return new MqlExpression<>((cr) -> new AstPlaceholder(new BsonNull()))
                .assertImplementsAllExpressions();
    }
}