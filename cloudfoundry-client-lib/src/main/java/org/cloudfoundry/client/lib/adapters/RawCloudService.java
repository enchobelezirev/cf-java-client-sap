package org.cloudfoundry.client.lib.adapters;

import java.util.Optional;

import org.cloudfoundry.client.lib.domain.CloudService;
import org.cloudfoundry.client.lib.domain.ImmutableCloudService;
import org.cloudfoundry.client.lib.domain.annotation.Nullable;
import org.cloudfoundry.client.v2.Resource;
import org.cloudfoundry.client.v2.serviceinstances.UnionServiceInstanceEntity;
import org.cloudfoundry.client.v2.serviceplans.ServicePlanEntity;
import org.cloudfoundry.client.v2.services.ServiceEntity;
import org.immutables.value.Value;

@Value.Immutable
public abstract class RawCloudService extends RawCloudEntity<CloudService> {

    @Value.Parameter
    public abstract Resource<UnionServiceInstanceEntity> getResource();

    @Nullable
    public abstract Resource<ServicePlanEntity> getServicePlanResource();

    @Nullable
    public abstract Resource<ServiceEntity> getServiceResource();

    @Override
    public CloudService derive() {
        Resource<UnionServiceInstanceEntity> resource = getResource();
        UnionServiceInstanceEntity entity = resource.getEntity();
        return ImmutableCloudService.builder()
                                    .metadata(parseResourceMetadata(resource))
                                    .name(entity.getName())
                                    .plan(parsePlan(getServicePlanResource()))
                                    .label(parseLabel(getServiceResource()))
                                    .build();
    }

    private static String parsePlan(Resource<ServicePlanEntity> resource) {
        return Optional.ofNullable(resource)
                       .map(Resource::getEntity)
                       .map(ServicePlanEntity::getName)
                       .orElse(null);
    }

    private static String parseLabel(Resource<ServiceEntity> serviceResource) {
        return Optional.ofNullable(serviceResource)
                       .map(Resource::getEntity)
                       .map(ServiceEntity::getLabel)
                       .orElse(null);
    }

}
