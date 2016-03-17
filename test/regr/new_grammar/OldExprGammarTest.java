package regr.new_grammar;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.junit.Test;

import antlr.RecognitionException;
import antlr.TokenStreamException;
import ar.uba.dc.rfm.alloy.AlloyTyping;
import ar.uba.dc.rfm.alloy.ast.AlloyModule;
import ar.uba.dc.rfm.alloy.ast.expressions.AlloyExpression;
import ar.uba.dc.rfm.alloy.ast.formulas.AlloyFormula;
import ar.uba.dc.rfm.alloy.util.AlloyPrinter;
import ar.uba.dc.rfm.dynalloy.DynAlloyAnalyzer;
import ar.uba.dc.rfm.dynalloy.DynAlloyCompiler;
import ar.uba.dc.rfm.dynalloy.DynAlloyOptions;
import ar.uba.dc.rfm.dynalloy.parser.AssertionNotFound;
import ar.uba.dc.rfm.dynalloy.plugin.AlloyStringPlugin;
import ar.uba.dc.rfm.dynalloy.trace.DynAlloySolution;
import ar.uba.dc.rfm.dynalloy.visualization.AlloyCommand;
import ar.uba.dc.rfm.dynalloy.visualization.VizException;

public class OldExprGammarTest {

	@Test
	public void old_expr_grammar_translate() throws RecognitionException,
			TokenStreamException, IOException, AssertionNotFound {
		DynAlloyCompiler c = new DynAlloyCompiler();
		DynAlloyOptions options = new DynAlloyOptions();
		options.setAssertionToCheck("grammarExtAssert");
		options.setLoopUnroll(3);
		options.setStrictUnroll(false);
		options.setRemoveQuantifier(false);
		options.setRunAlloyAnalyzer(false);
		options.setBuildDynAlloyTrace(false);
		
		AlloyModule alloyAST = c.compile("test/regr/new_grammar/old_expr_grammar.dals",
				"test/regr/new_grammar/old_expr_grammar.als", options, new HashMap<String, AlloyTyping>(), new HashMap<String, List<AlloyFormula>>(),
				new HashMap<String, AlloyTyping>(), new HashMap<String, List<AlloyFormula>>());

		// Print Alloy AST
		String optionsHeader = c.buildOptionsHeader(options);
		AlloyPrinter printer = new AlloyPrinter();
		String alloyStr = (String) alloyAST.accept(printer);
		String alloyStrWithHeader = optionsHeader + "\n" + alloyStr;

		// Apply AlloyString plugins
		for (AlloyStringPlugin string_plugin: c.getAlloyStringPlugins()) {
			alloyStrWithHeader = string_plugin.transform(alloyStrWithHeader);
		}

		// System.out.println(alloyStrWithHeader);
		// Write Alloy file
		c.writeFile("test/regr/new_grammar/old_expr_grammar.als", alloyStrWithHeader);

	
	}

	@Test
	public void old_expr_grammar_translate_And_check()
			throws RecognitionException, TokenStreamException, IOException,
			AssertionNotFound, VizException {

		DynAlloyOptions options = new DynAlloyOptions();
		options.setRemoveQuantifier(false);
		options.setLoopUnroll(3);
		options.setStrictUnroll(false);
		options.setRunAlloyAnalyzer(false);

		DynAlloyAnalyzer analyzer = new DynAlloyAnalyzer();
		File dynalloy_file = new File(
				"test/regr/new_grammar/old_expr_grammar.dals");
		List<AlloyCommand> commands = analyzer
				.getAvailableCommands(dynalloy_file);
		for (AlloyCommand cmd : commands) {
			DynAlloySolution result = analyzer.analyzeCommand(dynalloy_file,
					cmd, options);
			if (cmd.getFullCommand().startsWith("run")) {
				assertTrue(result.isSAT());
			}
			if (cmd.getFullCommand().startsWith("check")) {
				assertTrue(result.isSAT());
			}
		}

	}

	@Test
	public void old_expr_grammar_in_assume_Translate()
			throws RecognitionException, TokenStreamException, IOException,
			AssertionNotFound {
		DynAlloyCompiler c = new DynAlloyCompiler();
		DynAlloyOptions options = new DynAlloyOptions();

		options.setAssertionToCheck(null);
		options.setLoopUnroll(3);
		options.setStrictUnroll(false);
		options.setRemoveQuantifier(false);
		options.setRunAlloyAnalyzer(false);
		options.setBuildDynAlloyTrace(false);

		AlloyModule alloyAST = c.compile("test/regr/new_grammar/old_expr_in_assume.dals",
				"test/regr/new_grammar/old_expr_in_assume.als", options, new HashMap<String, AlloyTyping>(), new HashMap<String, List<AlloyFormula>>(),
				new HashMap<String, AlloyTyping>(), new HashMap<String, List<AlloyFormula>>());

		// Print Alloy AST
		String optionsHeader = c.buildOptionsHeader(options);
		AlloyPrinter printer = new AlloyPrinter();
		String alloyStr = (String) alloyAST.accept(printer);
		String alloyStrWithHeader = optionsHeader + "\n" + alloyStr;

		// Apply AlloyString plugins
		for (AlloyStringPlugin string_plugin: c.getAlloyStringPlugins()) {
			alloyStrWithHeader = string_plugin.transform(alloyStrWithHeader);
		}

		// System.out.println(alloyStrWithHeader);
		// Write Alloy file
		c.writeFile("test/regr/new_grammar/old_expr_in_assume.als", alloyStrWithHeader);

	
	}

}
