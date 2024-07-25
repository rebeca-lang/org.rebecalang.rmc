package org.rebecalang.rmc.timedrebeca.network;

import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.RebecaModel;
import org.rebecalang.compiler.modelcompiler.timedrebeca.objectmodel.TimedRebecaCode;

public class AddNetwork {
    private RebecaModel rebecaModel;
    public AddNetwork(RebecaModel rebecaModel) {
        this.rebecaModel = rebecaModel;
    }

    public void AddNetworkToRebecaModel() {
        if (!((TimedRebecaCode) rebecaModel.getRebecaCode()).getNetworkDeclaration().isEmpty()) {
            RebecaModelNetworkDecorator rebecaModelNetworkDecorator = new RebecaModelNetworkDecorator(rebecaModel);
            rebecaModel = rebecaModelNetworkDecorator.decorate();

            ConvertNetwork convertNetwork = new ConvertNetwork((TimedRebecaCode) rebecaModel.getRebecaCode(), rebecaModelNetworkDecorator);
            convertNetwork.changeRebecaCode();
        }
    }
}
