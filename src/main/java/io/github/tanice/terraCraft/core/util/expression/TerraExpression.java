package io.github.tanice.terraCraft.core.util.expression;

import org.codehaus.commons.compiler.CompileException;
import org.codehaus.janino.ExpressionEvaluator;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 支持java math等库的api
 */
public class TerraExpression {

    private static final ConcurrentMap<String, ExpressionEvaluator> COMPILED_CACHE = new ConcurrentHashMap<>();

    private TerraExpression() {
        throw new UnsupportedOperationException("TerraExpression class cannot be instantiated");
    }

    /**
     * 注册并预编译表达式
     *
     * @param exprId      表达式唯一标识ID
     * @param expression  表达式字符串
     * @param returnType  表达式返回值类型
     * @param paramNames  参数名称数组
     * @param paramTypes  参数类型数组
     * @throws CompileException 编译错误抛出
     */
    public static void register(String exprId, String expression, Class<?> returnType, String[] paramNames, Class<?>[] paramTypes) throws CompileException {
        Objects.requireNonNull(exprId, "exprId cannot be null");
        Objects.requireNonNull(expression, "expression cannot be null");
        Objects.requireNonNull(returnType, "returnType cannot be null");
        if (COMPILED_CACHE.containsKey(exprId)) return;
        ExpressionEvaluator evaluator = new ExpressionEvaluator();
        evaluator.setParameters(paramNames, paramTypes);
        evaluator.setExpressionType(returnType);
        evaluator.cook(expression);
        COMPILED_CACHE.putIfAbsent(exprId, evaluator);
    }

    /**
     * 根据表达式ID计算结果
     *
     * @param exprId 已注册的表达式ID
     * @param params 实际参数值数组（与注册时的paramNames顺序对应）
     * @return 计算结果
     * @throws IllegalArgumentException 当表达式未注册时抛出
     * @throws Exception                计算过程中发生异常时抛出
     */
    public static Object calculate(String exprId, Object[] params) throws Exception {
        ExpressionEvaluator evaluator = COMPILED_CACHE.get(exprId);
        if (evaluator == null) throw new IllegalArgumentException("Invalid expression id: " + exprId);
        return evaluator.evaluate(params);
    }

    /**
     * 清除所有已注册的表达式
     */
    public static void clear() {
        COMPILED_CACHE.clear();
    }

    /**
     * 移除指定ID的表达式
     *
     * @param exprId 表达式ID
     */
    public static void remove(String exprId) {
        COMPILED_CACHE.remove(exprId);
    }
}
