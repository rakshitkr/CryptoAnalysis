package crypto.analysis;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import boomerang.jimple.AllocVal;
import boomerang.jimple.Statement;
import boomerang.jimple.Val;
import crypto.analysis.util.StmtWithMethod;
import crypto.rules.CryptSLForbiddenMethod;
import crypto.rules.CryptSLRule;
import crypto.rules.StateMachineGraph;
import crypto.typestate.CryptSLMethodToSootMethod;
import crypto.typestate.ExtendedIDEALAnaylsis;
import soot.Body;
import soot.MethodOrMethodContext;
import soot.Scene;
import soot.SootMethod;
import soot.Unit;
import soot.jimple.InvokeExpr;
import soot.jimple.Stmt;
import soot.jimple.toolkits.callgraph.ReachableMethods;
import soot.jimple.toolkits.ide.icfg.BiDiInterproceduralCFG;
import soot.util.queue.QueueReader;
import sync.pds.solver.nodes.Node;

public class ClassSpecification {
	private ExtendedIDEALAnaylsis extendedIdealAnalysis;
	private CryptSLRule cryptSLRule;
	private final CryptoScanner cryptoScanner;

	public ClassSpecification(final CryptSLRule rule, final CryptoScanner cScanner) {
		this.cryptSLRule = rule;
		this.cryptoScanner = cScanner;
		this.extendedIdealAnalysis = new ExtendedIDEALAnaylsis() {
			@Override
			public StateMachineGraph getStateMachine() {
				return cryptSLRule.getUsagePattern();
			}

			@Override
			public CrySLAnalysisResultsAggregator analysisListener() {
				return null;
			}

			@Override
			public BiDiInterproceduralCFG<Unit, SootMethod> icfg() {
				return cryptoScanner.icfg();
			}
		};
	}

	public boolean isLeafRule() {
		return cryptSLRule.isLeafRule();
	}

	public Set<Node<Statement,AllocVal>> getInitialSeeds() {
		return extendedIdealAnalysis.computeInitialSeeds();
	}

	@Override
	public String toString() {
		return cryptSLRule.getClassName().toString();
	}

	public void checkForForbiddenMethods() {
		ReachableMethods rm = Scene.v().getReachableMethods();
		QueueReader<MethodOrMethodContext> listener = rm.listener();
		while (listener.hasNext()) {
			MethodOrMethodContext next = listener.next();
			SootMethod method = next.method();
			if (method == null || !method.hasActiveBody()) {
				continue;
			}
			invokesForbiddenMethod(method.getActiveBody());
		}
	}

	private void invokesForbiddenMethod(Body activeBody) {
		for (Unit u : activeBody.getUnits()) {
			if (u instanceof Stmt) {
				Stmt stmt = (Stmt) u;
				if (!stmt.containsInvokeExpr())
					continue;
				InvokeExpr invokeExpr = stmt.getInvokeExpr();
				SootMethod method = invokeExpr.getMethod();
				Optional<CryptSLForbiddenMethod> forbiddenMethod = isForbiddenMethod(method);
				if (forbiddenMethod.isPresent()){
					cryptoScanner.getAnalysisListener().callToForbiddenMethod(this, new StmtWithMethod(u, cryptoScanner.icfg().getMethodOf(u)),forbiddenMethod.get().getAlternatives());
				}
			}
		}
	}

	private Optional<CryptSLForbiddenMethod> isForbiddenMethod(SootMethod method) {
		// TODO replace by real specification once available.
		List<CryptSLForbiddenMethod> forbiddenMethods = cryptSLRule.getForbiddenMethods();
//		System.out.println(forbiddenMethods);
		//TODO Iterate over ICFG and report on usage of forbidden method.
		for(CryptSLForbiddenMethod m : forbiddenMethods){
			if(!m.getSilent()){
				Collection<SootMethod> matchingMatched = CryptSLMethodToSootMethod.v().convert(m.getMethod());
				if(matchingMatched.contains(method))
					return Optional.of(m);
				
			}
		}
		return Optional.empty();
	}


	public CryptSLRule getRule() {
		return cryptSLRule;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((cryptSLRule == null) ? 0 : cryptSLRule.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ClassSpecification other = (ClassSpecification) obj;
		if (cryptSLRule == null) {
			if (other.cryptSLRule != null)
				return false;
		} else if (!cryptSLRule.equals(other.cryptSLRule))
			return false;
		return true;
	}

	public Collection<SootMethod> getInvolvedMethods() {
		return extendedIdealAnalysis.getOrCreateTypestateChangeFunction().getInvolvedMethods();
	}

}
