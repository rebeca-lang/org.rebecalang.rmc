package org.rebecalang.rmc.timedrebeca.network;

import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import static org.rebecalang.compiler.modelcompiler.timedrebeca.network.NetworkNameUtility.generateReactiveNetworkInterfaceName;
import static org.rebecalang.compiler.modelcompiler.timedrebeca.network.NetworkNameUtility.generateWiredReactiveClassName;

public class RebecaModelNetworkDecorator {
    private final RebecaModel rebecaModel;
    private HashMap<String, Type> networkInterfaceTypes = new HashMap<>();

    public RebecaModelNetworkDecorator(RebecaModel rebecaModel) {
        this.rebecaModel = rebecaModel;
        fillNetworkInterfaceTypes();
    }

    public RebecaModel decorate() {
        for (ReactiveClassDeclaration rd : rebecaModel.getRebecaCode().getReactiveClassDeclaration()) {
            rd.getKnownRebecs().addAll(generateNetworkKnownRebecsFor(rd));
        }
        return rebecaModel;
    }

    private List<FieldDeclaration> generateNetworkKnownRebecsFor(ReactiveClassDeclaration reactiveClassDeclaration) {
        List<FieldDeclaration> networkKnownRebecs = new ArrayList<>();
        for (FieldDeclaration knownRebec : reactiveClassDeclaration.getKnownRebecs()) {
            FieldDeclaration networkRebec = new FieldDeclaration();
            networkRebec.setType(getNetworkInterfaceType(knownRebec));
            networkRebec.setAccessModifier(knownRebec.getAccessModifier());
            networkRebec.getVariableDeclarators().clear();
            networkRebec.getVariableDeclarators().addAll(generateKnownNetworkRebecVariableDeclaratorsFor(knownRebec));
            networkRebec.setLineNumber(0);
            networkRebec.setCharacter(0);
            networkKnownRebecs.add(networkRebec);
        }
        return networkKnownRebecs;
    }

    private List<VariableDeclarator> generateKnownNetworkRebecVariableDeclaratorsFor(FieldDeclaration knownRebec) {
        List<VariableDeclarator> variableDeclarators = new ArrayList<>();
        for (VariableDeclarator vd : knownRebec.getVariableDeclarators()) {
            VariableDeclarator variableDeclarator = new VariableDeclarator();
            variableDeclarator.setVariableName(generateVariableNameForKnownRebecOfNetworkType(vd.getVariableName()));
            variableDeclarator.setLineNumber(0);
            variableDeclarator.setCharacter(0);
            variableDeclarators.add(variableDeclarator);
        }
        return variableDeclarators;
    }

    private Type getNetworkInterfaceType(FieldDeclaration knownRebec) {
        return networkInterfaceTypes.get(generateReactiveNetworkInterfaceName(knownRebec.getType().getTypeName()));
    }

    private void fillNetworkInterfaceTypes() {
        for (ReactiveClassDeclaration rd : rebecaModel.getRebecaCode().getReactiveClassDeclaration()) {
            findWiredReactiveClassOf(rd.getName()).ifPresent(reactiveClassDeclaration -> {
                Type type = reactiveClassDeclaration.getImplements().get(0);
                networkInterfaceTypes.put(type.getTypeName(), type);
            });
        }
    }

    private Optional<ReactiveClassDeclaration> findWiredReactiveClassOf(String originalReactiveClassName) {
        return rebecaModel.getRebecaCode().getReactiveClassDeclaration().stream()
                .filter(rd -> rd.getName().equals(generateWiredReactiveClassName(originalReactiveClassName)))
                .findAny();
    }

    private String generateVariableNameForKnownRebecOfNetworkType(String originalVarName) {
        return "_net_" + originalVarName;
    }
}
