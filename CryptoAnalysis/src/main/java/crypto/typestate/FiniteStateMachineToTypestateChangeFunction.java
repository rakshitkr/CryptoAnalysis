package crypto.typestate;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import com.google.common.collect.Sets;

import boomerang.accessgraph.AccessGraph;
import crypto.rules.StateMachineGraph;
import crypto.rules.StateNode;
import crypto.rules.StatementLabel;
import heros.utilities.DefaultValueMap;
import soot.Local;
import soot.Scene;
import soot.SootClass;
import soot.SootMethod;
import soot.Unit;
import soot.jimple.AssignStmt;
import soot.jimple.InstanceInvokeExpr;
import soot.jimple.InvokeExpr;
import soot.jimple.Stmt;
import typestate.TypestateDomainValue;
import typestate.finiteautomata.MatcherStateMachine;
import typestate.finiteautomata.MatcherTransition;
import typestate.finiteautomata.MatcherTransition.Parameter;
import typestate.finiteautomata.MatcherTransition.Type;
import typestate.finiteautomata.Transition;

public class FiniteStateMachineToTypestateChangeFunction extends MatcherStateMachine<StateNode> {

	private StateNode initialState;
	private Collection<SootMethod> initialTransitonLabel;
	private Collection<SootMethod> allMatchedMethods = Sets.newHashSet();
	
	private CryptoTypestateAnaylsisProblem analysisProblem;
	private StateMachineGraph stateMachineGraph;

	public FiniteStateMachineToTypestateChangeFunction(CryptoTypestateAnaylsisProblem analysisProblem) {
		this.analysisProblem = analysisProblem;
		stateMachineGraph = analysisProblem.getStateMachineGraph();
		initialTransitonLabel = convert(stateMachineGraph.getInitialTransition().getLabel());
		initialState = stateMachineGraph.getInitialTransition().to();
		for (final typestate.interfaces.Transition<StateNode> t : stateMachineGraph.getAllTransitions()) {
			this.addTransition(new LabeledMatcherTransition(t.from(), t.getLabel(),
					Parameter.This, t.to(), Type.OnCallToReturn));
		}
	}

	private Collection<SootMethod> convert(List<StatementLabel> label) {
		Collection<SootMethod> converted = StatementLabelToSootMethod.v().convert(label);
		allMatchedMethods.addAll(converted);
		return converted;
	}


	private Collection<SootMethod> convert(StatementLabel label) {
		Collection<SootMethod> converted = StatementLabelToSootMethod.v().convert(label);
		allMatchedMethods.addAll(converted);
		return converted;
	}
	@Override
	public Set<Transition<StateNode>> getCallToReturnTransitionsFor(AccessGraph d1, Unit callSite, AccessGraph d2,
			Unit returnSite, AccessGraph d3) {
		Set<Transition<StateNode>> res = super.getCallToReturnTransitionsFor(d1, callSite, d2, returnSite, d3);
		for (Transition<StateNode> t : res) {
			if (!(t instanceof LabeledMatcherTransition))
				continue;
			injectQueryAtCallSite(((LabeledMatcherTransition)t).label,callSite, d1);
		}
		return res;
	}
	
	private void injectQueryAtCallSite(List<StatementLabel> list, Unit callSite, AccessGraph context) {
		for(StatementLabel matchingDescriptor : list){
			for(SootMethod m : convert(matchingDescriptor)){
				if (!(callSite instanceof Stmt))
					continue;
				Stmt stmt = (Stmt) callSite;
				if (!stmt.containsInvokeExpr())
					continue;
				SootMethod method = stmt.getInvokeExpr().getMethod();
				if (!m.equals(method))
					continue;
				{
					int index = 0;
					List<Boolean> backward = matchingDescriptor.getBackward();
					for(Entry<String, String> param : matchingDescriptor.getParameters()){
						if( index > 0 && backward.get(index) ){
							if(!param.getKey().equals("_")){
								soot.Type parameterType = method.getParameterType(index - 1);
								if(parameterType.toString().equals(param.getValue())){
									analysisProblem.addQueryAtCallsite(param.getValue(), stmt, index - 1, context);
								}
							}
						}
						index++;
					}
				}
			}
		}
	}

	@Override
	public Collection<AccessGraph> generateSeed(SootMethod method, Unit unit, Collection<SootMethod> optional) {
		Set<AccessGraph> out = new HashSet<>();
		if (!(unit instanceof Stmt) || !((Stmt) unit).containsInvokeExpr())
			return out;
		InvokeExpr invokeExpr = ((Stmt) unit).getInvokeExpr();
		SootMethod calledMethod = invokeExpr.getMethod();
		if (!initialTransitonLabel.contains(calledMethod))
			return out;
		if (calledMethod.isStatic()) {
			AssignStmt stmt = (AssignStmt) unit;
			out.add(new AccessGraph((Local) stmt.getLeftOp(), stmt.getLeftOp().getType()));
		} else if (invokeExpr instanceof InstanceInvokeExpr){
			InstanceInvokeExpr iie = (InstanceInvokeExpr) invokeExpr;
			out.add(new AccessGraph((Local)  iie.getBase(), iie.getBase().getType()));
		}
		injectQueryAtCallSite(stateMachineGraph.getInitialTransition().getLabel(),unit,null);
		return out;
	}

	

	@Override
	public TypestateDomainValue<StateNode> getBottomElement() {
		return new TypestateDomainValue<StateNode>(initialState);
	}

	
	public Collection<SootMethod> getAllMatchedMethods(){
		return Sets.newHashSet(allMatchedMethods);
	}
	
	private class LabeledMatcherTransition extends MatcherTransition<StateNode>{

		private final List<StatementLabel> label;

		public LabeledMatcherTransition(StateNode from, List<StatementLabel> label,
				Parameter param, StateNode to,
				Type type) {
			super(from,convert(label), param, to, type);
			this.label = label;
		}}
}
