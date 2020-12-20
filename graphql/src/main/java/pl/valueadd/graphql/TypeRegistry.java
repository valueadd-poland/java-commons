package pl.valueadd.graphql;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;
import graphql.language.*;
import graphql.schema.idl.TypeRuntimeWiring;


import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.*;

import static graphql.language.FieldDefinition.newFieldDefinition;
import static graphql.language.InputObjectTypeDefinition.newInputObjectDefinition;
import static graphql.language.InputValueDefinition.newInputValueDefinition;
import static graphql.language.ObjectTypeDefinition.newObjectTypeDefinition;
import static graphql.schema.idl.TypeRuntimeWiring.newTypeWiring;
import static java.util.Collections.EMPTY_MAP;

/**
 * @author Jakub Trzcinski kuba@valueadd.pl
 * @since 10-12-2020
 */
class TypeRegistry {

    final ObjectMapper mapper = new ObjectMapper();

    public HashMap<Class, InputObjectTypeDefinition> getInputs() {
        return inputs;
    }

    public HashMap<Class, ObjectTypeDefinition> getOutputs() {
        return outputs;
    }

    private final HashMap<Class, InputObjectTypeDefinition> inputs = new HashMap<>();

    private final HashMap<Class, ObjectTypeDefinition> outputs = new HashMap<>();

    private final List<Class> valueObjects = List.of(
            String.class,
            int.class, Integer.class,
            long.class, Long.class,
            UUID.class,
            boolean.class, Boolean.class
    );
    public InputObjectTypeDefinition registerAndGetInput(Class clazz) {
        var fields = new LinkedList<InputValueDefinition>();
        extractClasses(clazz).forEach((prop, type) -> {
            registerAndGetInput(type);
            fields.add(newInputValueDefinition().name(prop).type(getType(type)).build());
        });
        var ret = newInputObjectDefinition()
                .name(getType(clazz).getName())
                .inputValueDefinitions(fields)
                .build();

        if(!clazz.isAssignableFrom(Void.class) && !valueObjects.contains(clazz)) {
            inputs.put(clazz, ret);
        }

        return ret;
    }


    public ObjectTypeDefinition registerAndGetOutput(Class clazz) {
        var fields = new LinkedList<FieldDefinition>();
        extractClasses(clazz).forEach((prop, type) -> {
            registerAndGetOutput(type);
            fields.add(newFieldDefinition().name(prop).type(getType(type)).build());
        });
        var ret = newObjectTypeDefinition()
                .name(getType(clazz).getName())
                .fieldDefinitions(fields)
                .build();

        if(!clazz.isAssignableFrom(Void.class) && !valueObjects.contains(clazz)) {
            outputs.put(clazz, ret);
        }
        return ret;
    }

    private TypeName getType(Class clazz) {
        if (clazz.isAssignableFrom(Void.class)) {
            return new TypeName("Query");
        }
        if (clazz.isAssignableFrom(UUID.class)) {
            return new TypeName("ID");
        }
        if (clazz.isAssignableFrom(boolean.class)) {
            return new TypeName("Boolean");
        }
        if (clazz.isAssignableFrom(long.class)) {
            return new TypeName("Int");
        }
        if (clazz.isAssignableFrom(int.class)) {
            return new TypeName("Int");
        }
        if(valueObjects.contains(clazz)){
            return new TypeName("String");
        }
        return new TypeName(clazz.getSimpleName());
    }


    private Map<String, Class> extractClasses(Class type) {
        if (type == null) {
            return EMPTY_MAP;
        }
        if (type.isAssignableFrom(UUID.class)) {
            return EMPTY_MAP;
        }
        if (type.isAssignableFrom(Object.class)) {
            return EMPTY_MAP;
        }
        if (type.isAssignableFrom(Void.class)) {
            return EMPTY_MAP;
        }
        Map<String, Class> ret = new HashMap<>();
        for (Method method : type.getDeclaredMethods()) {
            if (method.isAnnotationPresent(JsonIgnore.class)) {
                continue;
            }

            if (!method.getName().startsWith("get")
                    && !method.getName().startsWith("is")) {
                continue;
            }

            var qlType = method.getReturnType();
            if (Collection.class.isAssignableFrom(method.getReturnType())) {
                qlType = (Class) ((ParameterizedType) method.getGenericReturnType()).getActualTypeArguments()[0];
            }
            var prop = method.getName();
            if (method.getName().startsWith("get")) {
                prop = prop.substring(3);
                prop = (prop.charAt(0) + "").toLowerCase() + prop.substring(1);
            }
            if (method.getName().startsWith("is")) {
                prop = prop.substring(2);
                prop = (prop.charAt(0) + "").toLowerCase() + prop.substring(1);
            }
            ret.put(prop, qlType);
        }
        ret.putAll(extractClasses(type.getSuperclass()));
        return ret;
    }

    public TypeRuntimeWiring.Builder addResolver(GraphQLFetcher resolver) {
        var root = registerAndGetInput(getRootClass(resolver));
        registerAndGetOutput(getReturnType(resolver));
        registerAndGetInput(getRequestType(resolver));
        return newTypeWiring(root.getName()).dataFetcher(
                resolver.getName(),
                res -> resolver.resolve(res.getSource(), mapArg(res.getArgument(resolver.getArgName()), getRequestType(resolver)))
        );
    }

    public TypeRuntimeWiring.Builder addMutator(GraphQLCreator resolver) {
        registerAndGetOutput(getReturnType(resolver));
        registerAndGetInput(getRequestType(resolver));
        return newTypeWiring("Mutation").dataFetcher(
                resolver.getName(),
                res -> resolver.create(mapArg(res.getArgument(resolver.getArgName()), getRequestType(resolver)))
        );
    }


    public TypeRuntimeWiring.Builder addMutator(GraphQLRemover resolver) {
        registerAndGetOutput(getReturnType(resolver));
        registerAndGetInput(getIDType(resolver));
        return newTypeWiring("Mutation")
                .dataFetcher(
                        resolver.getName(),
                        res -> resolver.remove(
                                mapArg(res.getArgument("id"), getIDType(resolver))
                        )
                );

    }

    public TypeRuntimeWiring.Builder addMutator(GraphQLUpdater resolver) {
        registerAndGetOutput(getReturnType(resolver));
        registerAndGetInput(getIDType(resolver));
        registerAndGetInput(getRequestType(resolver));
        return newTypeWiring("Mutation")
                .dataFetcher(
                        resolver.getName(),
                        res -> resolver.update(
                                mapArg(res.getArgument("id"), getIDType(resolver)),
                                mapArg(res.getArgument("payload"), getRequestType(resolver))
                        )
                );

    }

    private Object mapArg(Object res, Class type) {
        return mapper.convertValue(res, type);
    }


    private Class getRootClass(GraphQLFetcher q) {
        return (Class<?>) ((ParameterizedType) q.getClass().getGenericInterfaces()[0]).getActualTypeArguments()[0];
    }


    private Class getReturnType(GraphQLCreator q) {
        return (Class<?>) ((ParameterizedType) q.getClass().getGenericInterfaces()[0]).getActualTypeArguments()[0];
    }

    private Class getReturnType(GraphQLUpdater q) {
        return (Class<?>) ((ParameterizedType) q.getClass().getGenericInterfaces()[0]).getActualTypeArguments()[0];
    }

    private Class getReturnType(GraphQLFetcher q) {
        return (Class<?>) ((ParameterizedType) q.getClass().getGenericInterfaces()[0]).getActualTypeArguments()[1];
    }

    private Class getReturnType(GraphQLRemover q) {
        return (Class<?>) ((ParameterizedType) q.getClass().getGenericInterfaces()[0]).getActualTypeArguments()[1];
    }

    private Class getIDType(GraphQLRemover q) {
        return (Class<?>) ((ParameterizedType) q.getClass().getGenericInterfaces()[0]).getActualTypeArguments()[0];
    }

    private Class getIDType(GraphQLUpdater q) {
        return (Class<?>) ((ParameterizedType) q.getClass().getGenericInterfaces()[0]).getActualTypeArguments()[1];
    }

    private Class getRequestType(GraphQLUpdater q) {
        return (Class<?>) ((ParameterizedType) q.getClass().getGenericInterfaces()[0]).getActualTypeArguments()[2];
    }

    private Class getRequestType(GraphQLFetcher q) {
        return (Class<?>) ((ParameterizedType) q.getClass().getGenericInterfaces()[0]).getActualTypeArguments()[2];
    }

    private Class getRequestType(GraphQLCreator q) {
        return (Class<?>) ((ParameterizedType) q.getClass().getGenericInterfaces()[0]).getActualTypeArguments()[1];
    }

}
