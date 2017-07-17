import { downgradeComponent, downgradeInjectable } from '@angular/upgrade/static';
import { FactoryProvider } from '@angular/core';

export interface IComponentUpgradeOptions {
    inputs?: string[],
    outputs?: string[]
}

export interface IHybridHelper {
    downgradeComponent(moduleName: string, componentSelector: string, componentClass: any, options?: IComponentUpgradeOptions): IHybridHelper,
    downgradeProvider(moduleName: string, providerName: string, providerClass: any): IHybridHelper,
    buildProviderForUpgrade(ng1Name: string, ng2Name?: string): FactoryProvider
}

export const HybridHelper: IHybridHelper {

    downgradeComponent: (moduleName: string, componentName: string, componentClass: any, options?: IComponentUpgradeOptions): IHybridHelper => {
        options = options || {};
        const inputs = options.inputs || [];
        const outputs = options.outputs || [];
        const component = componentClass;

        angular.module(moduleName).directive(componentName, downgradeComponent({ 
            component, inputs, outputs 
        }) as angular.IDirectiveFactory);

        return HybridHelper;
    },

    downgradeProvider: (moduleName: string, providerName: string, providerClass: any): IHybridHelper => {
        angular.module(moduleName).factory(providerName, downgradeInjectable(providerClass));

        return HybridHelper;
    },

    buildProviderForUpgrade: (ng1Name: string, ng2Name?: string): FactoryProvider => {
        ng2Name = ng2Name || ng1Name;

        return {
            provide: ng2Name,
            useFactory: buildFactoryForUpgradeProvider(ng1Name),
            deps: ['$injector']
        };
    }
}

function buildFactoryForUpgradeProvider(ng1Name: string): Function {
    return (injector: any) => injector.get(ng1Name);
}