/*
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

/*
 *    MathFunctions.java
 *    Copyright (C) 2015 University of Waikato, Hamilton, New Zealand
 *
 */

package weka.core.expressionlanguage.common;

import java.util.HashMap;
import java.util.Map;
import weka.core.expressionlanguage.common.Primitives.DoubleExpression;
import weka.core.expressionlanguage.core.Macro;
import weka.core.expressionlanguage.core.MacroDeclarations;
import weka.core.expressionlanguage.core.Node;
import weka.core.expressionlanguage.core.SemanticException;

/**
 * Macro declarations for common mathematical functions.</p>
 *
 * The following functions are being exposed through macros:</br>
 * <ul>
 * <li>{@link Math.abs(double)} as abs</li>
 * <li>{@link Math.sqrt(double)} as sqrt</li>
 * <li>{@link Math.log(double)} as log</li>
 * <li>{@link Math.exp(double)} as exp</li>
 * <li>{@link Math.sin(double)} as sin</li>
 * <li>{@link Math.cos(double)} as cos</li>
 * <li>{@link Math.tan(double)} as tan</li>
 * <li>{@link Math.rint(double)} as rint</li>
 * <li>{@link Math.floor(double)} as floor</li>
 * <li>{@link Math.ceil(double)} as ceil</li>
 * <li>{@link Math.pow(double)} as pow</li>
 * </ul>
 *
 * @author Benjamin Weber ( benweber at student dot ethz dot ch )
 * @version $Revision: 1000 $
 */
public class MathFunctions implements MacroDeclarations {

  /** the macros to be exposed */
  private static Map<String, Macro> macros = new HashMap<String, Macro>();

  static {
    macros.put("abs", new DoubleUnaryMacro(AbsFunction.class));
    macros.put("sqrt", new DoubleUnaryMacro(SqrtFunction.class));
    macros.put("log", new DoubleUnaryMacro(LogFunction.class));
    macros.put("exp", new DoubleUnaryMacro(ExpFunction.class));
    macros.put("sin", new DoubleUnaryMacro(SinFunction.class));
    macros.put("cos", new DoubleUnaryMacro(CosFunction.class));
    macros.put("tan", new DoubleUnaryMacro(TanFunction.class));
    macros.put("rint", new DoubleUnaryMacro(RintFunction.class));
    macros.put("floor", new DoubleUnaryMacro(FloorFunction.class));
    macros.put("ceil", new DoubleUnaryMacro(CeilFunction.class));
    macros.put("pow", new PowMacro());
  }

  /**
   * Whether the macro is declared
   *
   * @param name of the macro
   * @return whether the macro is declared
   */
  @Override public boolean hasMacro(String name) {
    return macros.containsKey(name);
  }

  /**
   * Tries to fetch the macro</p>
   *
   * The same invariant of {@link MacroDeclarations} applies here too.
   *
   * @param name name of the macro
   * @return a macro
   */
  @Override public Macro getMacro(String name) {
    if (macros.containsKey(name)) return macros.get(name);
    throw new RuntimeException("Macro '" + name + "' undefined!");
  }

  private static class DoubleUnaryMacro implements Macro {

    private final Class<? extends DoubleUnaryFunction> func;

    public DoubleUnaryMacro(Class<? extends DoubleUnaryFunction> func) {
      this.func = func;
    }

    @Override public Node evaluate(Node... params) throws SemanticException {
      if (params.length != 1) {
        throw new SemanticException("'" + name() + "' takes exactly one argument!");
      }
      if (!(params[0] instanceof DoubleExpression)) {
        throw new SemanticException("'" + name() + "'s first argument must be double!");
      }

      try {
        Node node = func.getConstructor(DoubleExpression.class).newInstance(params[0]);
        return node;
      } catch (Exception e) {
        throw new RuntimeException("Failed to instantiate '" + name() + "'!", e);
      }
    }

    private String name() {
      return func.getSimpleName();
    }
  }

  private static abstract class DoubleUnaryFunction implements DoubleExpression {

    final DoubleExpression expr;

    DoubleUnaryFunction(DoubleExpression expr) {
      this.expr = expr;
    }
  }

  private static class AbsFunction extends DoubleUnaryFunction {

    public AbsFunction(DoubleExpression expr) {
      super(expr);
    }

    @Override public double evaluate() {
      return Math.abs(expr.evaluate());
    }
  }

  private static class SqrtFunction extends DoubleUnaryFunction {

    public SqrtFunction(DoubleExpression expr) {
      super(expr);
    }

    @Override public double evaluate() {
      return Math.sqrt(expr.evaluate());
    }
  }

  private static class LogFunction extends DoubleUnaryFunction {

    public LogFunction(DoubleExpression expr) {
      super(expr);
    }

    @Override public double evaluate() {
      return Math.log(expr.evaluate());
    }
  }

  private static class ExpFunction extends DoubleUnaryFunction {

    public ExpFunction(DoubleExpression expr) {
      super(expr);
    }

    @Override public double evaluate() {
      return Math.exp(expr.evaluate());
    }
  }

  private static class SinFunction extends DoubleUnaryFunction {

    public SinFunction(DoubleExpression expr) {
      super(expr);
    }

    @Override public double evaluate() {
      return Math.sin(expr.evaluate());
    }
  }

  private static class CosFunction extends DoubleUnaryFunction {

    public CosFunction(DoubleExpression expr) {
      super(expr);
    }

    @Override public double evaluate() {
      return Math.cos(expr.evaluate());
    }
  }

  private static class TanFunction extends DoubleUnaryFunction {

    public TanFunction(DoubleExpression expr) {
      super(expr);
    }

    @Override public double evaluate() {
      return Math.tan(expr.evaluate());
    }
  }

  private static class RintFunction extends DoubleUnaryFunction {

    public RintFunction(DoubleExpression expr) {
      super(expr);
    }

    @Override public double evaluate() {
      return Math.rint(expr.evaluate());
    }
  }

  private static class FloorFunction extends DoubleUnaryFunction {

    public FloorFunction(DoubleExpression expr) {
      super(expr);
    }

    @Override public double evaluate() {
      return Math.floor(expr.evaluate());
    }
  }

  private static class CeilFunction extends DoubleUnaryFunction {

    public CeilFunction(DoubleExpression expr) {
      super(expr);
    }

    @Override public double evaluate() {
      return Math.ceil(expr.evaluate());
    }
  }

  private static class PowMacro implements Macro {

    @Override public Node evaluate(Node... params) throws SemanticException {
      if (params.length != 2) throw new SemanticException("pow takes exactly two arguments!");
      if (!(params[0] instanceof DoubleExpression)) {
        throw new SemanticException("pow's first argument must be double!");
      }
      if (!(params[1] instanceof DoubleExpression)) {
        throw new SemanticException("pow's second argument must be double!");
      }
      return new PowFunction((DoubleExpression) params[0], (DoubleExpression) params[1]);
    }
  }

  private static class PowFunction implements DoubleExpression {

    private final DoubleExpression base;
    private final DoubleExpression exponent;

    public PowFunction(DoubleExpression base, DoubleExpression exponent) {
      this.base = base;
      this.exponent = exponent;
    }

    @Override public double evaluate() {
      return Math.pow(base.evaluate(), exponent.evaluate());
    }
  }
}
