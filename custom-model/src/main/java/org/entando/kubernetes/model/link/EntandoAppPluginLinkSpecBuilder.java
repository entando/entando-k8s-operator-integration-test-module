package org.entando.kubernetes.model.link;

public class EntandoAppPluginLinkSpecBuilder<N extends EntandoAppPluginLinkSpecBuilder> {

    private String entandoAppName;
    private String entandoAppNamespace;
    private String entandoPluginName;
    private String entandoPluginNamespace;

    public EntandoAppPluginLinkSpecBuilder() {
        //Useful
    }

    public EntandoAppPluginLinkSpecBuilder(EntandoAppPluginLinkSpec spec) {
        this.entandoAppNamespace = spec.getEntandoAppNamespace();
        this.entandoAppName = spec.getEntandoAppName();
        this.entandoPluginNamespace = spec.getEntandoPluginNamespace();
        this.entandoPluginName = spec.getEntandoPluginName();
    }

    public N withEntandoApp(String entandoAppNamespace, String entandoAppName) {
        this.entandoAppNamespace = entandoAppNamespace;
        this.entandoAppName = entandoAppName;
        return (N) this;
    }

    public N withEntandoPlugin(String entandoPluginNamespace, String entandoPluginName) {
        this.entandoPluginNamespace = entandoPluginNamespace;
        this.entandoPluginName = entandoPluginName;
        return (N) this;
    }

    public EntandoAppPluginLinkSpec build() {
        return new EntandoAppPluginLinkSpec(entandoAppNamespace, entandoAppName, entandoPluginNamespace, entandoPluginName);
    }
}
