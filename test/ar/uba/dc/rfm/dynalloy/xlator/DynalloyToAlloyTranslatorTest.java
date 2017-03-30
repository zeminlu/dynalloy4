/*
 * Dynalloy Translator
 * Copyright (c) 2007 Universidad de Buenos Aires
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA,
 * 02110-1301, USA
 */
package ar.uba.dc.rfm.dynalloy.xlator;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import org.junit.Test;

import ar.uba.dc.rfm.alloy.AlloyCheckCommand;
import ar.uba.dc.rfm.alloy.AlloyTyping;
import ar.uba.dc.rfm.alloy.AlloyVariable;
import ar.uba.dc.rfm.alloy.VariableId;
import ar.uba.dc.rfm.alloy.ast.AlloyModule;
import ar.uba.dc.rfm.alloy.ast.expressions.AlloyExpression;
import ar.uba.dc.rfm.alloy.ast.expressions.ExprVariable;
import ar.uba.dc.rfm.alloy.ast.formulas.AlloyFormula;
import ar.uba.dc.rfm.alloy.ast.formulas.AndFormula;
import ar.uba.dc.rfm.alloy.ast.formulas.ImpliesFormula;
import ar.uba.dc.rfm.alloy.ast.formulas.PredicateFormula;
import ar.uba.dc.rfm.alloy.util.AlloyBuffer;
import ar.uba.dc.rfm.dynalloy.DynAlloyOptions;
import ar.uba.dc.rfm.dynalloy.ast.ActionDeclaration;
import ar.uba.dc.rfm.dynalloy.ast.AssertionDeclaration;
import ar.uba.dc.rfm.dynalloy.ast.DynalloyModule;
import ar.uba.dc.rfm.dynalloy.ast.programs.DynalloyProgram;
import ar.uba.dc.rfm.dynalloy.ast.programs.InvokeAction;
import ar.uba.dc.rfm.dynalloy.ast.programs.TestPredicate;
import ar.uba.dc.rfm.dynalloy.util.DynalloySpecBuffer;

public class DynalloyToAlloyTranslatorTest {

	private static final String STRICT_A4_SPEC = "module ...";

	private static final String TYPE_B = "T2";

	private static final String TYPE_A = "T1";

	private static final String PREDICATE_ID = "P";

	private static final String ACTION_ID = "A";

	private final static String ASSERTION_ID = "A";

	private static final AlloyCheckCommand CHK_CMD = new AlloyCheckCommand();

	private DynalloyModule buildDynA4Spec(DynalloyProgram p) {
		DynalloySpecBuffer buff = new DynalloySpecBuffer();
		buff.setModuleId("moduleId");
		buff.setCompilableA4Spec(STRICT_A4_SPEC);
		buff.putAction(ACTION_ID, buildActionBody());
		buff.addAssertion(buildAssertionBody(p));
		return buff.toDynalloySpec();
	}

	private AssertionDeclaration buildAssertionBody(DynalloyProgram p) {
		AlloyTyping ps = new AlloyTyping();
		ps.put(a, TYPE_A);
		ps.put(b, TYPE_B);

		return new AssertionDeclaration(ASSERTION_ID, ps, buildPred(a, b), p,
				buildPred(a_prime, b_prime));
	}

	private InvokeAction buildInvokeAction(VariableId v1, VariableId v2) {
		List<AlloyExpression> ps = new LinkedList<AlloyExpression>();
		ps.add(new ExprVariable(new AlloyVariable(v1)));
		ps.add(new ExprVariable(new AlloyVariable(v2)));
		return new InvokeAction(null, ACTION_ID, ps);
	}

	private TestPredicate buildTestPredicate(VariableId v1, VariableId v2) {
		/*
		 return new Choice(new Composition(buildInvokeAction(v1, v2),
		 buildInvokeAction(v2, v1)), new TestPredicate(buildPred(
		 new AlloyVariable(v1), new AlloyVariable(v2))));
		 */
		return new TestPredicate(buildPred(new AlloyVariable(v1),
				new AlloyVariable(v2)));
	}

	private PredicateFormula buildPred(AlloyVariable v1, AlloyVariable v2) {
		List<AlloyExpression> ps = new LinkedList<AlloyExpression>();
		ps.add(new ExprVariable(v1));
		ps.add(new ExprVariable(v2));
		return new PredicateFormula(null, PREDICATE_ID, ps);
	}

	private VariableId aId = new VariableId("a");

	private AlloyVariable a = new AlloyVariable(aId);

	private AlloyVariable a_prime = new AlloyVariable(aId, true);

	private AlloyVariable a_0 = new AlloyVariable(aId, 0);

	private AlloyVariable a_1 = new AlloyVariable(aId, 1);

	private VariableId bId = new VariableId("b");

	private AlloyVariable b = new AlloyVariable(bId);

	private AlloyVariable b_0 = new AlloyVariable(bId, 0);

	private AlloyVariable b_prime = new AlloyVariable(bId, true);

	private VariableId xId = new VariableId("x");

	private VariableId yId = new VariableId("y");

	private AlloyVariable x = new AlloyVariable(xId);

	private AlloyVariable x_prime = new AlloyVariable(xId, true);

	private AlloyVariable y = new AlloyVariable(yId);

	private ActionDeclaration buildActionBody() {
		List<VariableId> formalPs = new LinkedList<VariableId>();
		formalPs.add(xId);
		formalPs.add(yId);
		AlloyTyping alloyTyping = new AlloyTyping();
		alloyTyping.put(x, "T1");
		alloyTyping.put(y, "T2");
		return new ActionDeclaration(ACTION_ID, formalPs, buildPred(x, y),
				buildPred(x, x_prime), alloyTyping);
	}

	@Test
	public void translateTestPredicate() {
		// input 
		TestPredicate p = buildTestPredicate(aId, bId);
		DynalloyModule dynalloyAST = buildDynA4Spec(p);
		// expected
		AlloyBuffer buff = new AlloyBuffer();
		buff.addQuantifiedVar(a_0, TYPE_A);
		buff.addQuantifiedVar(b_0, TYPE_B);
		buff.setAssertionId(ASSERTION_ID);
		buff.setCompilableA4Spec(STRICT_A4_SPEC);
		buff.setAssertion(buildTranslatedTestPredicate());
		buff.setCompilableA4Spec("module ..." + "\n"
				+ AlloyPredicates.TRUE_PRED_SPEC + "\n"
				+ AlloyPredicates.FALSE_PRED_SPEC + "\n");
		AlloyModule expected = buff.toAlloyModule();

		// perform test
		SpecContext specContext = new SpecContext(dynalloyAST);
		specContext.setFields(new AlloyTyping());
		DynalloyXlatorVisitor translator = new DynalloyXlatorVisitor(specContext, DynAlloyOptions.DEFAULT_DYNALLOY_OPTIONS, new HashMap<String, AlloyTyping>(), new HashMap<String, List<AlloyFormula>>(),
				new HashMap<String, AlloyTyping>(), new HashMap<String, List<AlloyFormula>>(), false);
		AlloyModule actual = (AlloyModule) dynalloyAST.accept(translator);

		// check result
		assertEquals(expected.getAssertion(ASSERTION_ID), actual.getAssertion(ASSERTION_ID));

	}

	private ImpliesFormula buildTranslatedTestPredicate() {
		return new ImpliesFormula(new AndFormula(buildPred(a_0, b_0),
				buildPred(a_0, b_0)), buildPred(a_0, b_0));
	}

	private ImpliesFormula buildTranslatedInvokeAction() {
		return new ImpliesFormula(new AndFormula(buildPred(a_0, b_0),
				new AndFormula(buildPred(a_0, b_0), buildPred(a_0, a_1))),
				buildPred(a_1, b_0));
	}

	@Test
	public void translateInvokeAction() {
		// input 
		InvokeAction p = buildInvokeAction(aId, bId);
		DynalloyModule dynalloyAST = buildDynA4Spec(p);
		// expected
		AlloyBuffer buff = new AlloyBuffer();
		buff.addQuantifiedVar(a_0, TYPE_A);
		buff.addQuantifiedVar(a_1, TYPE_A);
		buff.addQuantifiedVar(b_0, TYPE_B);
		buff.setAssertionId(ASSERTION_ID);
		buff.setCompilableA4Spec(STRICT_A4_SPEC);
		buff.setCompilableA4Spec("module ..." + "\n"
				+ AlloyPredicates.TRUE_PRED_SPEC + "\n"
				+ AlloyPredicates.FALSE_PRED_SPEC + "\n");
		buff.setAssertion(buildTranslatedInvokeAction());
		AlloyModule expected = buff.toAlloyModule();

		// perform test
		SpecContext specContext = new SpecContext(dynalloyAST);
		specContext.setFields(new AlloyTyping());
		DynalloyXlatorVisitor translator = new DynalloyXlatorVisitor(specContext, DynAlloyOptions.DEFAULT_DYNALLOY_OPTIONS, new HashMap<String, AlloyTyping>(), new HashMap<String, List<AlloyFormula>>(),
				new HashMap<String, AlloyTyping>(), new HashMap<String, List<AlloyFormula>>(), false);
		AlloyModule actual = (AlloyModule) dynalloyAST.accept(translator);


	}
}
