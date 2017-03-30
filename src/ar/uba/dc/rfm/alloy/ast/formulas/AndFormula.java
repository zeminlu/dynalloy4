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
package ar.uba.dc.rfm.alloy.ast.formulas;

import static ar.uba.dc.rfm.dynalloy.xlator.AlloyPredicates.TRUE_CONSTANT;

public final class AndFormula extends AlloyFormula {

	private final AlloyFormula left;

	private final AlloyFormula right;
	
	public static AlloyFormula buildAndFormula(AlloyFormula... fs) {
		AlloyFormula result = null;
		for (int i = 0; i < fs.length; i++) {
			AlloyFormula f = fs[i];
			if (result == null)
				result = f;
			else
				result = new AndFormula(result, f);
		}
		if (result == null)
			return TRUE_CONSTANT;
		else
			return result;
	}

	public AndFormula(AlloyFormula left, AlloyFormula right) {
		super();
		this.left = left;
		this.right = right;
	}

	public Object accept(IFormulaVisitor visitor) {
		return visitor.visit(this);
	}

	@Override
	public boolean equals(Object arg0) {
		if (arg0 != null) {
			if (arg0.getClass().equals(AndFormula.class)) {
				AndFormula af = (AndFormula) arg0;
				return af.getLeft().equals(getLeft())
						&& af.getRight().equals(getRight());
			}
		}
		return false;
	}

	@Override
	public int hashCode() {
		return getLeft().hashCode() + getRight().hashCode();
	}

	@Override
	public String toString() {
		return "[" + getLeft().toString() + " And " + getRight().toString() + "]";
	}

	public AlloyFormula getLeft() {
		return this.left;
	}

	public AlloyFormula getRight() {
		return this.right;
	}

}
