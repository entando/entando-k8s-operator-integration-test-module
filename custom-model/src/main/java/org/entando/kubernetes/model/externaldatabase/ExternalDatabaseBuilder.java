package org.entando.kubernetes.model.externaldatabase;

import io.fabric8.kubernetes.api.builder.Builder;

public class ExternalDatabaseBuilder extends ExternalDatabaseFluent<ExternalDatabaseBuilder> implements Builder<ExternalDatabase> {

    @Override
    public ExternalDatabase build() {
        return new ExternalDatabase(super.metadata.build(), super.spec.build());
    }
}
