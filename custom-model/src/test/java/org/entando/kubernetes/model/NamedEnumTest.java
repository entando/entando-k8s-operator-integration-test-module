package org.entando.kubernetes.model;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import org.entando.kubernetes.model.capability.CapabilityProvisioningStrategy;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.api.Test;

@Tags({@Tag("in-process"), @Tag("pre-deployment")})
class NamedEnumTest {

    @Test
    void testResolve() {
        assertThat(CapabilityProvisioningStrategy.forValue("delegatetooperator"), is(CapabilityProvisioningStrategy.DELEGATE_TO_OPERATOR));
        assertThat(CapabilityProvisioningStrategy.forValue("delegate_to_operator"),
                is(CapabilityProvisioningStrategy.DELEGATE_TO_OPERATOR));
        assertThat(CapabilityProvisioningStrategy.forValue("delegate-to-operator"),
                is(CapabilityProvisioningStrategy.DELEGATE_TO_OPERATOR));
        assertThat(CapabilityProvisioningStrategy.forValue("delegate to operator"),
                is(CapabilityProvisioningStrategy.DELEGATE_TO_OPERATOR));
        assertThat(CapabilityProvisioningStrategy.forValue("delegate.to.operator"),
                is(CapabilityProvisioningStrategy.DELEGATE_TO_OPERATOR));
        assertThat(CapabilityProvisioningStrategy.forValue("DELEGATETOOPERATOR"), is(CapabilityProvisioningStrategy.DELEGATE_TO_OPERATOR));
        assertThat(CapabilityProvisioningStrategy.forValue("DELEGATE_TO_OPERATOR"),
                is(CapabilityProvisioningStrategy.DELEGATE_TO_OPERATOR));
        assertThat(CapabilityProvisioningStrategy.forValue("DELEGATE.TO.OPERATOR"),
                is(CapabilityProvisioningStrategy.DELEGATE_TO_OPERATOR));
        assertThat(CapabilityProvisioningStrategy.forValue("DelegateToOperator"), is(CapabilityProvisioningStrategy.DELEGATE_TO_OPERATOR));
    }
}
