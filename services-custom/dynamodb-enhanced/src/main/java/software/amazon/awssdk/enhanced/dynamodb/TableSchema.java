/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 *  http://aws.amazon.com/apache2.0
 *
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */

package software.amazon.awssdk.enhanced.dynamodb;

import java.lang.invoke.MethodHandles;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.annotations.ThreadSafe;
import software.amazon.awssdk.enhanced.dynamodb.document.DocumentTableSchema;
import software.amazon.awssdk.enhanced.dynamodb.document.EnhancedDocument;
import software.amazon.awssdk.enhanced.dynamodb.mapper.BeanTableSchema;
import software.amazon.awssdk.enhanced.dynamodb.mapper.BeanTableSchemaParams;
import software.amazon.awssdk.enhanced.dynamodb.mapper.ImmutableTableSchema;
import software.amazon.awssdk.enhanced.dynamodb.mapper.ImmutableTableSchemaParams;
import software.amazon.awssdk.enhanced.dynamodb.mapper.StaticImmutableTableSchema;
import software.amazon.awssdk.enhanced.dynamodb.mapper.StaticTableSchema;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbImmutable;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPreserveEmptyObject;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

/**
 * Interface for a mapper that is capable of mapping a modelled Java object into a map of {@link AttributeValue} that is
 * understood by the DynamoDb low-level SDK and back again. This object is also expected to know about the
 * structure of the table it is modelling, which is stored in a {@link TableMetadata} object.
 *
 * @param <T> The type of model object that is being mapped to records in the DynamoDb table.
 */
@SdkPublicApi
@ThreadSafe
public interface TableSchema<T> {
    /**
     * Returns a builder for the {@link StaticTableSchema} implementation of this interface which allows all attributes,
     * tags and table structure to be directly declared in the builder.
     *
     * @param itemClass The class of the item this {@link TableSchema} will map records to.
     * @param <T> The type of the item this {@link TableSchema} will map records to.
     * @return A newly initialized {@link StaticTableSchema.Builder}.
     */
    static <T> StaticTableSchema.Builder<T> builder(Class<T> itemClass) {
        return StaticTableSchema.builder(itemClass);
    }

    /**
     * Returns a builder for the {@link StaticTableSchema} implementation of this interface which allows all attributes,
     * tags and table structure to be directly declared in the builder.
     *
     * @param itemType The {@link EnhancedType} of the item this {@link TableSchema} will map records to.
     * @param <T> The type of the item this {@link TableSchema} will map records to.
     * @return A newly initialized {@link StaticTableSchema.Builder}.
     */
    static <T> StaticTableSchema.Builder<T> builder(EnhancedType<T> itemType) {
        return StaticTableSchema.builder(itemType);
    }

    /**
     * Returns a builder for the {@link StaticImmutableTableSchema} implementation of this interface which allows all
     * attributes, tags and table structure to be directly declared in the builder.
     *
     * @param immutableItemClass The class of the immutable item this {@link TableSchema} will map records to.
     * @param immutableBuilderClass The class that can be used to construct immutable items this {@link TableSchema}
     *                              maps records to.
     * @param <T> The type of the immutable item this {@link TableSchema} will map records to.
     * @param <B> The type of the builder used by this {@link TableSchema} to construct immutable items with.
     * @return A newly initialized {@link StaticImmutableTableSchema.Builder}
     */
    static <T, B> StaticImmutableTableSchema.Builder<T, B> builder(Class<T> immutableItemClass,
                                                                   Class<B> immutableBuilderClass) {
        return StaticImmutableTableSchema.builder(immutableItemClass, immutableBuilderClass);
    }

    /**
     * Returns a builder for the {@link StaticImmutableTableSchema} implementation of this interface which allows all
     * attributes, tags and table structure to be directly declared in the builder.
     *
     * @param immutableItemType The {@link EnhancedType} of the immutable item this {@link TableSchema} will map records to.
     * @param immutableBuilderType The {@link EnhancedType} of the class that can be used to construct immutable items this
     *                             {@link TableSchema} maps records to.
     * @param <T> The type of the immutable item this {@link TableSchema} will map records to.
     * @param <B> The type of the builder used by this {@link TableSchema} to construct immutable items with.
     * @return A newly initialized {@link StaticImmutableTableSchema.Builder}
     */
    static <T, B> StaticImmutableTableSchema.Builder<T, B> builder(EnhancedType<T> immutableItemType,
                                                                   EnhancedType<B> immutableBuilderType) {
        return StaticImmutableTableSchema.builder(immutableItemType, immutableBuilderType);
    }

    /**
     * Scans a bean class that has been annotated with DynamoDb bean annotations and then returns a
     * {@link BeanTableSchema} implementation of this interface that can map records to and from items of that bean
     * class.
     *
     * <p>
     * It's recommended to only create a {@link BeanTableSchema} once for a single bean class, usually at application start up,
     * because it's a moderately expensive operation.
     *
     * @param beanClass The bean class this {@link TableSchema} will map records to.
     * @param <T> The type of the item this {@link TableSchema} will map records to.
     * @return An initialized {@link BeanTableSchema}.
     */
    static <T> BeanTableSchema<T> fromBean(Class<T> beanClass) {
        return BeanTableSchema.create(beanClass);
    }

    /**
     * Scans a bean class that has been annotated with DynamoDb bean annotations and then returns a
     * {@link BeanTableSchema} implementation of this interface that can map records to and from items of that bean
     * class.
     * <p>
     * It's recommended to only create a {@link BeanTableSchema} once for a single bean class, usually at application start up,
     * because it's a moderately expensive operation.
     * <p>
     * Generally, this method should be preferred over {@link #fromBean(Class)} because it allows you to use a custom
     * {@link MethodHandles.Lookup} instance, which is necessary when your application runs in an environment where your
     * application code and dependencies like the AWS SDK for Java are loaded by different classloaders.
     *
     * @param params The parameters used to create the {@link BeanTableSchema}.
     * @param <T> The type of the item this {@link TableSchema} will map records to.
     * @return An initialized {@link BeanTableSchema}.
     */
    static <T> BeanTableSchema<T> fromBean(BeanTableSchemaParams<T> params) {
        return BeanTableSchema.create(params);
    }

    /**
     * Provides interfaces to interact with DynamoDB tables as {@link EnhancedDocument} where the complete Schema of the table is
     * not required.
     *
     * @return A {@link DocumentTableSchema.Builder} for instantiating DocumentTableSchema.
     */
    static DocumentTableSchema.Builder documentSchemaBuilder() {
        return DocumentTableSchema.builder();
    }

    /**
     * Scans an immutable class that has been annotated with DynamoDb immutable annotations and then returns a
     * {@link ImmutableTableSchema} implementation of this interface that can map records to and from items of that
     * immutable class.
     *
     * <p>
     * It's recommended to only create an {@link ImmutableTableSchema} once for a single immutable class, usually at application
     * start up, because it's a moderately expensive operation.
     *
     * @param immutableClass The immutable class this {@link TableSchema} will map records to.
     * @param <T> The type of the item this {@link TableSchema} will map records to.
     * @return An initialized {@link ImmutableTableSchema}.
     */
    static <T> ImmutableTableSchema<T> fromImmutableClass(Class<T> immutableClass) {
        return ImmutableTableSchema.create(immutableClass);
    }

    /**
     * Scans an immutable class that has been annotated with DynamoDb immutable annotations and then returns a
     * {@link ImmutableTableSchema} implementation of this interface that can map records to and from items of that
     * immutable class.
     *
     * <p>
     * It's recommended to only create an {@link ImmutableTableSchema} once for a single immutable class, usually at application
     * start up, because it's a moderately expensive operation.
     *
     * @param params The parameters used to create the {@link ImmutableTableSchema}.
     * @param <T> The type of the item this {@link TableSchema} will map records to.
     * @return An initialized {@link ImmutableTableSchema}.
     */
    static <T> ImmutableTableSchema<T> fromImmutableClass(ImmutableTableSchemaParams<T> params) {
        return ImmutableTableSchema.create(params);
    }

    /**
     * Scans a class that has been annotated with DynamoDb enhanced client annotations and then returns an appropriate
     * {@link TableSchema} implementation that can map records to and from items of that class. Currently supported
     * top level annotations (see documentation on those classes for more information on how to use them):
     * {@link DynamoDbBean}, {@link DynamoDbImmutable}.
     *
     * <p>
     * It's recommended to only invoke this operation once for a single class, usually at application start up,
     * because it's a moderately expensive operation.
     *
     * <p>
     * If this table schema is not behaving as you expect, enable debug logging for
     * {@code software.amazon.awssdk.enhanced.dynamodb.beans}.
     *
     * @param annotatedClass A class that has been annotated with DynamoDb enhanced client annotations.
     * @param <T> The type of the item this {@link TableSchema} will map records to.
     * @return An initialized {@link TableSchema}
     */
    static <T> TableSchema<T> fromClass(Class<T> annotatedClass) {
        if (annotatedClass.getAnnotation(DynamoDbImmutable.class) != null) {
            return fromImmutableClass(annotatedClass);
        }

        if (annotatedClass.getAnnotation(DynamoDbBean.class) != null) {
            return fromBean(annotatedClass);
        }

        throw new IllegalArgumentException("Class does not appear to be a valid DynamoDb annotated class. [class = " +
                                               "\"" + annotatedClass + "\"]");
    }

    /**
     * Takes a raw DynamoDb SDK representation of a record in a table and maps it to a Java object. A new object is
     * created to fulfil this operation.
     * <p>
     * If attributes are missing from the map, that will not cause an error, however if attributes are found in the
     * map which the mapper does not know how to map, an exception will be thrown.
     *
     * <p>
     * If all attribute values in the attributeMap are null, null will be returned. Use {@link #mapToItem(Map, boolean)}
     * instead if you need to preserve empty object.
     *
     * <p>
     * API Implementors Note:
     * <p>
     * {@link #mapToItem(Map, boolean)} must be implemented if {@code preserveEmptyObject} behavior is desired.
     *
     * @param attributeMap A map of String to {@link AttributeValue} that contains all the raw attributes to map.
     * @return A new instance of a Java object with all the attributes mapped onto it.
     * @throws IllegalArgumentException if any attributes in the map could not be mapped onto the new model object.
     * @see #mapToItem(Map, boolean)
     */
    T mapToItem(Map<String, AttributeValue> attributeMap);

    /**
     * Takes a raw DynamoDb SDK representation of a record in a table and maps it to a Java object. A new object is
     * created to fulfil this operation.
     * <p>
     * If attributes are missing from the map, that will not cause an error, however if attributes are found in the
     * map which the mapper does not know how to map, an exception will be thrown.
     *
     * <p>
     * In the scenario where all attribute values in the map are null, it will return null if {@code preserveEmptyObject}
     * is true. If it's false, an empty object will be returned.
     *
     * <p>
     * Note that {@code preserveEmptyObject} only applies to the top level Java object, if it has nested "empty" objects, they
     * will be mapped as null. You can use {@link DynamoDbPreserveEmptyObject} to configure this behavior for nested objects.
     *
     * <p>
     * API Implementors Note:
     * <p>
     * This method must be implemented if {@code preserveEmptyObject} behavior is to be supported
     *
     * @param attributeMap A map of String to {@link AttributeValue} that contains all the raw attributes to map.
     * @param preserveEmptyObject whether to initialize this Java object as empty class if all fields are null
     * @return A new instance of a Java object with all the attributes mapped onto it.
     * @throws IllegalArgumentException if any attributes in the map could not be mapped onto the new model object.
     * @throws UnsupportedOperationException if {@code preserveEmptyObject} is not supported in the implementation
     * @see #mapToItem(Map)
     */
    default T mapToItem(Map<String, AttributeValue> attributeMap, boolean preserveEmptyObject) {
        if (preserveEmptyObject) {
            throw new UnsupportedOperationException("preserveEmptyObject is not supported. You can set preserveEmptyObject to "
                                                    + "false to continue to call this operation. If you wish to enable "
                                                    + "preserveEmptyObject, please reach out to the maintainers of the "
                                                    + "implementation class for assistance.");
        }
        return mapToItem(attributeMap);
    }

    /**
     * Takes a modelled object and converts it into a raw map of {@link AttributeValue} that the DynamoDb low-level
     * SDK can work with.
     *
     * @param item The modelled Java object to convert into a map of attributes.
     * @param ignoreNulls If set to true; any null values in the Java object will not be added to the output map.
     *                    If set to false; null values in the Java object will be added as {@link AttributeValue} of
     *                    type 'nul' to the output map.
     * @return A map of String to {@link AttributeValue} representing all the modelled attributes in the model object.
     */
    Map<String, AttributeValue> itemToMap(T item, boolean ignoreNulls);

    /**
     * Takes a modelled object and extracts a specific set of attributes which are then returned as a map of
     * {@link AttributeValue} that the DynamoDb low-level SDK can work with. This method is typically used to extract
     * just the key attributes of a modelled item and will not ignore nulls on the modelled object.
     *
     * @param item The modelled Java object to extract the map of attributes from.
     * @param attributes A collection of attribute names to extract into the output map.
     * @return A map of String to {@link AttributeValue} representing the requested modelled attributes in the model
     * object.
     */
    Map<String, AttributeValue> itemToMap(T item, Collection<String> attributes);

    /**
     * Returns a single attribute value from the modelled object.
     *
     * @param item The modelled Java object to extract the attribute from.
     * @param attributeName The attribute name describing which attribute to extract.
     * @return A single {@link AttributeValue} representing the requested modelled attribute in the model object or
     * null if the attribute has not been set with a value in the modelled object.
     */
    AttributeValue attributeValue(T item, String attributeName);

    /**
     * Returns the object that describes the structure of the table being modelled by the mapper. This includes
     * information such as the table name, index keys and attribute tags.
     * @return A {@link TableMetadata} object that contains structural information about the table being modelled.
     */
    TableMetadata tableMetadata();

    /**
     * Returns the {@link EnhancedType} that represents the 'Type' of the Java object this table schema object maps to
     * and from.
     * @return The {@link EnhancedType} of the modelled item this TableSchema maps to.
     */
    EnhancedType<T> itemType();

    /**
     * Returns a complete list of attribute names that are mapped by this {@link TableSchema}
     */
    List<String> attributeNames();

    /**
     * A boolean value that represents whether this {@link TableSchema} is abstract which means that it cannot be used
     * to directly create records as it is lacking required structural elements to map to a table, such as a primary
     * key, but can be referred to and embedded by other schemata.
     *
     * @return true if it is abstract, and therefore cannot be used directly to create records but can be referred to
     * by other schemata, and false if it is concrete and may be used to map records directly.
     */
    boolean isAbstract();

    /**
     * {@link AttributeConverter} that is applied to the given key.
     *
     * @param key Attribute of the modelled item.
     * @return AttributeConverter defined for the given attribute key.
     */
    default AttributeConverter<T> converterForAttribute(Object key) {
        throw new UnsupportedOperationException();
    }
}
